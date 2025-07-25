# 字符串分割为List<String>的多种方法

本项目演示了如何使用Java将字符串按逗号分割成`List<String>`的多种方法，包括原生Java API、Stream API和第三方开源工具。

## 快速开始

### 1. 最简单的方法（推荐）

```java
import java.util.*;
import java.util.stream.Collectors;

// 方法1: 基本分割
String input = "apple,banana,orange";
List<String> result = Arrays.asList(input.split(","));

// 方法2: 使用Stream API（推荐）
List<String> result = Arrays.stream(input.split(","))
    .map(String::trim)          // 去除空格
    .filter(s -> !s.isEmpty())  // 过滤空字符串
    .collect(Collectors.toList());
```

### 2. 使用工具类

```java
// 使用本项目的工具类
List<String> result = StringSplitUtils.split("apple,banana,orange");
```

## 详细方法

### 原生Java方法

#### 1. String.split() + Arrays.asList()
```java
String input = "apple,banana,orange";
List<String> result = Arrays.asList(input.split(","));
```

#### 2. Stream API处理
```java
List<String> result = Arrays.stream(input.split(","))
    .map(String::trim)
    .filter(s -> !s.isEmpty())
    .collect(Collectors.toList());
```

#### 3. 正则表达式分割
```java
// 同时处理逗号和空格
List<String> result = Arrays.stream(input.split("\\s*,\\s*"))
    .filter(s -> !s.isEmpty())
    .collect(Collectors.toList());
```

### 第三方开源工具

#### 1. Apache Commons Lang3
```xml
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```

```java
import org.apache.commons.lang3.StringUtils;

List<String> result = Arrays.asList(StringUtils.split(input, ","));
```

#### 2. Google Guava
```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>32.1.3-jre</version>
</dependency>
```

```java
import com.google.common.base.Splitter;

List<String> result = Splitter.on(',')
    .trimResults()      // 去除空格
    .omitEmptyStrings() // 忽略空字符串
    .splitToList(input);
```

## 高级用法

### 1. 去重和排序
```java
List<String> result = Arrays.stream(input.split(","))
    .map(String::trim)
    .filter(s -> !s.isEmpty())
    .distinct()     // 去重
    .sorted()       // 排序
    .collect(Collectors.toList());
```

### 2. 转换为其他类型
```java
// 转换为数字列表
String numbers = "1,2,3,4,5";
List<Integer> intList = Arrays.stream(numbers.split(","))
    .map(String::trim)
    .map(Integer::parseInt)
    .collect(Collectors.toList());
```

### 3. 转换为Set
```java
Set<String> resultSet = Arrays.stream(input.split(","))
    .map(String::trim)
    .filter(s -> !s.isEmpty())
    .collect(Collectors.toSet());
```

### 4. 限制分割数量
```java
// 最多分割3个元素
List<String> result = Arrays.asList(input.split(",", 3));
```

## 工具类使用

本项目提供了`StringSplitUtils`工具类，包含多种常用方法：

```java
// 基本分割（推荐）
List<String> result = StringSplitUtils.split("apple,banana,orange");

// 指定分隔符
List<String> result = StringSplitUtils.splitByDelimiter(input, ";");

// 去重并排序
List<String> result = StringSplitUtils.splitDistinctAndSort(input, ",");

// 转换为数字
List<Integer> numbers = StringSplitUtils.splitAndMap("1,2,3", ",", Integer::parseInt);

// 常用分隔符
StringSplitUtils.CommonSplitters.byComma(input);     // 逗号
StringSplitUtils.CommonSplitters.bySemicolon(input); // 分号
StringSplitUtils.CommonSplitters.bySpace(input);     // 空格
```

## 性能比较

| 方法 | 性能 | 功能 | 推荐度 |
|------|------|------|--------|
| String.split() | 快 | 基础 | ⭐⭐⭐ |
| Stream API | 中等 | 强大 | ⭐⭐⭐⭐⭐ |
| Apache Commons | 快 | 丰富 | ⭐⭐⭐⭐ |
| Google Guava | 快 | 优雅 | ⭐⭐⭐⭐⭐ |

## 运行示例

```bash
# 编译并运行简单示例
javac SimpleStringSplit.java && java SimpleStringSplit

# 编译并运行工具类示例
javac StringSplitUtils.java && java StringSplitUtils

# 使用Maven运行（需要依赖）
mvn compile exec:java -Dexec.mainClass="StringSplitExamples"
```

## 最佳实践

1. **一般情况**：使用Stream API方式，功能强大且易读
2. **性能敏感**：使用原生`String.split()`
3. **复杂处理**：使用Google Guava的Splitter
4. **项目已有依赖**：使用Apache Commons Lang3

### 推荐的通用方法
```java
public static List<String> splitString(String input) {
    if (input == null || input.isEmpty()) {
        return new ArrayList<>();
    }
    
    return Arrays.stream(input.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());
}
```

这个方法具有以下优点：
- ✅ 处理null和空字符串
- ✅ 自动去除空格
- ✅ 过滤空元素
- ✅ 返回可修改的ArrayList
- ✅ 无需第三方依赖
