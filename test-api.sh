#!/bin/bash

# SpringBoot 业务流程日志解决方案 Demo - API测试脚本

echo "=========================================="
echo "SpringBoot 业务流程日志解决方案 Demo"
echo "API 测试脚本"
echo "=========================================="

BASE_URL="http://localhost:8080"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检查服务是否启动
echo -e "\n${BLUE}1. 检查服务状态...${NC}"
curl -s ${BASE_URL}/api/orders/health > /dev/null
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 服务运行正常${NC}"
else
    echo -e "${RED}✗ 服务未启动，请先运行: mvn spring-boot:run${NC}"
    exit 1
fi

echo -e "\n${YELLOW}请查看控制台日志，观察完整的业务流程日志记录${NC}"

# 测试创建订单
echo -e "\n${BLUE}2. 测试创建订单...${NC}"
ORDER_RESPONSE=$(curl -s -X POST ${BASE_URL}/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "PROD-001",
    "productName": "测试商品",
    "quantity": 2,
    "price": 99.99,
    "userId": "USER-001"
  }')

echo -e "${GREEN}创建订单响应:${NC}"
echo $ORDER_RESPONSE | python3 -m json.tool 2>/dev/null || echo $ORDER_RESPONSE

# 提取订单ID（如果响应是JSON格式）
ORDER_ID=$(echo $ORDER_RESPONSE | python3 -c "import sys, json; print(json.load(sys.stdin)['orderId'])" 2>/dev/null || echo "ORD-TEST123")

echo -e "\n${YELLOW}等待2秒，让日志输出完整...${NC}"
sleep 2

# 测试查询订单
echo -e "\n${BLUE}3. 测试查询订单...${NC}"
echo -e "查询订单ID: ${ORDER_ID}"
QUERY_RESPONSE=$(curl -s -X GET ${BASE_URL}/api/orders/${ORDER_ID})

echo -e "${GREEN}查询订单响应:${NC}"
echo $QUERY_RESPONSE | python3 -m json.tool 2>/dev/null || echo $QUERY_RESPONSE

echo -e "\n${YELLOW}等待2秒，让日志输出完整...${NC}"
sleep 2

# 测试取消订单
echo -e "\n${BLUE}4. 测试取消订单...${NC}"
echo -e "取消订单ID: ${ORDER_ID}"
CANCEL_RESPONSE=$(curl -s -X DELETE ${BASE_URL}/api/orders/${ORDER_ID})

echo -e "${GREEN}取消订单响应:${NC}"
echo $CANCEL_RESPONSE | python3 -m json.tool 2>/dev/null || echo $CANCEL_RESPONSE

echo -e "\n${YELLOW}等待2秒，让日志输出完整...${NC}"
sleep 2

# 测试异常情况
echo -e "\n${BLUE}5. 测试异常情况（空的商品ID）...${NC}"
ERROR_RESPONSE=$(curl -s -X POST ${BASE_URL}/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "",
    "productName": "测试商品",
    "quantity": 2,
    "price": 99.99,
    "userId": "USER-001"
  }')

echo -e "${RED}异常响应:${NC}"
echo $ERROR_RESPONSE

echo -e "\n${YELLOW}等待2秒，让日志输出完整...${NC}"
sleep 2

# 完成测试
echo -e "\n${GREEN}=========================================="
echo "API 测试完成！"
echo "=========================================="
echo -e "请查看以下位置的日志文件："
echo -e "- logs/business.log    (业务流程日志)"
echo -e "- logs/application.log (应用程序日志)"
echo -e "或者直接查看控制台输出的实时日志${NC}"

echo -e "\n${BLUE}日志特点说明：${NC}"
echo -e "✓ 每个业务流程都有唯一的TraceId"
echo -e "✓ 可以通过TraceId串联整个流程的所有操作"
echo -e "✓ 记录了方法参数、返回值、执行时间"
echo -e "✓ 异常情况有详细的错误日志"
echo -e "✓ 统一的日志格式，便于分析和监控"