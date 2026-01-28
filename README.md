# CaoJie

## Sharded Query Design (8 shards)

This repository documents a reference design for querying 8 shard
databases. The system decides whether to query a single shard or
fan-out across multiple shards based on the presence of an account
condition.

### Goals

- Route to a single shard when account condition is present.
- Otherwise aggregate across multiple shards with concurrency controls.
- Provide a consistent result schema and deterministic ordering.
- Support partial failures with clear error reporting.

### Interaction (API Contract)

Example request (HTTP/JSON):

```
POST /v1/search
{
  "account_id": "acc_123",        // optional
  "filters": { "status": "open" },
  "sort": [ { "field": "created_at", "order": "desc" } ],
  "page": { "limit": 50, "cursor": "..." }
}
```

Response (simplified):

```
{
  "data": [ ... ],
  "page": { "next_cursor": "..." },
  "meta": {
    "queried_shards": [0, 3, 5],
    "partial_failures": []
  }
}
```

### Routing Rules

1. If account_id is present, route to the shard for that account.
2. If account_id is absent, determine candidate shards:
   - If other filters can reduce shard set (e.g. region), apply them.
   - Otherwise query all 8 shards.

Shard mapping example:

```
shard_id = hash(account_id) % 8
```

### Query Aggregation

- Fan-out queries in parallel with a concurrency limit.
- Collect shard responses and merge:
  - If sorted, perform a k-way merge.
  - Apply pagination using a stable composite cursor.
- Deduplicate by primary key if needed.

### Error Strategy

- Fail fast for account_id queries (single shard must succeed).
- For multi-shard queries, allow partial failures:
  - Return successful data plus partial_failures list.
  - Include a retry token per failed shard.

### Code Structure (Reference)

```
search/
  router.py       # decide shard set
  executor.py     # parallel query execution
  merge.py        # k-way merge + pagination
  cursor.py       # shard-aware cursor encoding
  errors.py       # error policy + response mapping
```

### Pseudocode

```
def search(req):
    shards = route(req)
    results = execute_in_parallel(shards, req)
    if req.account_id and results.has_failure():
        raise ShardQueryError(...)
    merged = merge(results, req.sort, req.page)
    return response(merged, results.failures)
```