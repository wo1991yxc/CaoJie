import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 使用Java标准库将字符串按逗号分割成List<String>
 * 无需任何第三方依赖
 */
public class SimpleStringSplit {
    
    public static void main(String[] args) {
        String testString = "apple,banana,orange,grape,watermelon";
        String testStringWithSpaces = "apple, banana , orange, grape , watermelon";
        String testStringWithEmptyElements = "apple,,banana,";
        
        System.out.println("演示不同的字符串分割方法\n");
        
        // 方法1: 最简单的分割
        System.out.println("1. 最简单的分割：");
        List<String> result1 = simpleSplit(testString);
        System.out.println("输入: " + testString);
        System.out.println("结果: " + result1);
        System.out.println();
        
        // 方法2: 使用Stream API
        System.out.println("2. 使用Stream API：");
        List<String> result2 = streamSplit(testStringWithSpaces);
        System.out.println("输入: " + testStringWithSpaces);
        System.out.println("结果: " + result2);
        System.out.println();
        
        // 方法3: 处理空元素
        System.out.println("3. 处理包含空元素的字符串：");
        List<String> result3 = splitAndFilterEmpty(testStringWithEmptyElements);
        System.out.println("输入: " + testStringWithEmptyElements);
        System.out.println("结果: " + result3);
        System.out.println();
        
        // 方法4: 更高级的Stream处理
        System.out.println("4. 高级Stream处理（去重、排序）：");
        String duplicateString = "apple,banana,apple,orange,banana";
        List<String> result4 = advancedStreamSplit(duplicateString);
        System.out.println("输入: " + duplicateString);
        System.out.println("结果: " + result4);
    }
    
    /**
     * 方法1: 最简单的字符串分割
     * 使用String.split()和Arrays.asList()
     */
    public static List<String> simpleSplit(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(input.split(","));
    }
    
    /**
     * 方法2: 使用Stream API进行分割和处理
     * 自动去除首尾空格并过滤空字符串
     */
    public static List<String> streamSplit(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(input.split(","))
                .map(String::trim)           // 去除首尾空格
                .filter(s -> !s.isEmpty())   // 过滤空字符串
                .collect(Collectors.toList());
    }
    
    /**
     * 方法3: 分割并过滤空元素
     * 专门处理包含空元素的情况
     */
    public static List<String> splitAndFilterEmpty(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * 方法4: 高级Stream处理
     * 分割、去重、排序
     */
    public static List<String> advancedStreamSplit(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()                   // 去重
                .sorted()                     // 排序
                .collect(Collectors.toList());
    }
    
    /**
     * 方法5: 使用正则表达式分割
     * 可以同时处理逗号和空格
     */
    public static List<String> regexSplit(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(input.split("\\s*,\\s*"))  // 匹配逗号及其前后的空格
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * 方法6: 返回不可变列表
     * 使用Collections.unmodifiableList()
     */
    public static List<String> immutableSplit(String input) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<String> result = Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        
        return Collections.unmodifiableList(result);
    }
    
    /**
     * 方法7: 使用Optional处理null安全
     */
    public static List<String> nullSafeSplit(String input) {
        return Optional.ofNullable(input)
                .filter(s -> !s.isEmpty())
                .map(s -> Arrays.stream(s.split(","))
                        .map(String::trim)
                        .filter(str -> !str.isEmpty())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
    
    /**
     * 通用工具方法：支持自定义分隔符
     */
    public static List<String> splitByDelimiter(String input, String delimiter) {
        if (input == null || input.isEmpty() || delimiter == null) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(input.split(delimiter))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}