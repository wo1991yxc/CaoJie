import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import com.google.common.base.Splitter;

/**
 * 字符串按逗号分割成List<String>的多种实现方式
 */
public class StringSplitExamples {
    
    public static void main(String[] args) {
        String testString = "apple,banana,orange,grape,watermelon";
        String testStringWithSpaces = "apple, banana , orange, grape , watermelon";
        String testStringEmpty = "";
        String testStringWithEmptyElements = "apple,,banana,";
        
        System.out.println("原始字符串: " + testString);
        System.out.println("带空格的字符串: " + testStringWithSpaces);
        System.out.println("包含空元素的字符串: " + testStringWithEmptyElements);
        System.out.println("\n" + "=".repeat(50) + "\n");
        
        // 方法1: 使用原生String.split()
        demonstrateNativeSplit(testString, testStringWithSpaces, testStringWithEmptyElements);
        
        // 方法2: 使用Stream API
        demonstrateStreamSplit(testString, testStringWithSpaces);
        
        // 方法3: 使用Apache Commons Lang
        demonstrateApacheCommons(testString, testStringWithSpaces, testStringWithEmptyElements);
        
        // 方法4: 使用Google Guava
        demonstrateGuavaSplitter(testString, testStringWithSpaces, testStringWithEmptyElements);
        
        // 方法5: 自定义Stream处理
        demonstrateCustomStreamProcessing(testString, testStringWithSpaces);
    }
    
    /**
     * 方法1: 使用原生String.split()
     */
    public static void demonstrateNativeSplit(String... testStrings) {
        System.out.println("1. 使用原生String.split()方法:");
        
        for (String str : testStrings) {
            // 基本分割
            List<String> result1 = Arrays.asList(str.split(","));
            System.out.println("基本分割 '" + str + "': " + result1);
            
            // 分割并去除空白
            List<String> result2 = Arrays.stream(str.split(","))
                    .map(String::trim)
                    .collect(Collectors.toList());
            System.out.println("分割+去空白 '" + str + "': " + result2);
            
            // 分割并过滤空字符串
            List<String> result3 = Arrays.stream(str.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            System.out.println("分割+过滤空值 '" + str + "': " + result3);
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * 方法2: 使用Stream API
     */
    public static void demonstrateStreamSplit(String... testStrings) {
        System.out.println("2. 使用Stream API:");
        
        for (String str : testStrings) {
            // 使用Stream.of + flatMap
            List<String> result1 = Stream.of(str)
                    .flatMap(s -> Arrays.stream(s.split(",")))
                    .collect(Collectors.toList());
            System.out.println("Stream基本分割 '" + str + "': " + result1);
            
            // 使用Stream + 复杂处理
            List<String> result2 = Stream.of(str)
                    .filter(s -> !s.isEmpty())
                    .flatMap(s -> Arrays.stream(s.split(",")))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            System.out.println("Stream完整处理 '" + str + "': " + result2);
            
            // 转换为不可变列表
            List<String> result3 = Stream.of(str)
                    .flatMap(s -> Arrays.stream(s.split(",")))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(), 
                            Collections::unmodifiableList));
            System.out.println("Stream不可变列表 '" + str + "': " + result3);
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * 方法3: 使用Apache Commons Lang
     */
    public static void demonstrateApacheCommons(String... testStrings) {
        System.out.println("3. 使用Apache Commons Lang:");
        
        for (String str : testStrings) {
            // 基本分割
            List<String> result1 = Arrays.asList(StringUtils.split(str, ","));
            System.out.println("Commons基本分割 '" + str + "': " + result1);
            
            // 分割并去除空白
            String[] splitArray = StringUtils.split(str, ",");
            List<String> result2 = Arrays.stream(splitArray)
                    .map(String::trim)
                    .collect(Collectors.toList());
            System.out.println("Commons分割+去空白 '" + str + "': " + result2);
            
            // 使用splitByWholeSeparator
            if (!str.isEmpty()) {
                List<String> result3 = Arrays.asList(StringUtils.splitByWholeSeparator(str, ","));
                System.out.println("Commons wholeSeparator '" + str + "': " + result3);
            }
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * 方法4: 使用Google Guava
     */
    public static void demonstrateGuavaSplitter(String... testStrings) {
        System.out.println("4. 使用Google Guava:");
        
        for (String str : testStrings) {
            // 基本分割
            List<String> result1 = Splitter.on(',').splitToList(str);
            System.out.println("Guava基本分割 '" + str + "': " + result1);
            
            // 去除空白并忽略空字符串
            List<String> result2 = Splitter.on(',')
                    .trimResults()
                    .omitEmptyStrings()
                    .splitToList(str);
            System.out.println("Guava完整处理 '" + str + "': " + result2);
            
            // 限制分割数量
            List<String> result3 = Splitter.on(',')
                    .limit(3)
                    .splitToList(str);
            System.out.println("Guava限制分割 '" + str + "': " + result3);
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * 方法5: 自定义Stream处理
     */
    public static void demonstrateCustomStreamProcessing(String... testStrings) {
        System.out.println("5. 自定义Stream处理:");
        
        for (String str : testStrings) {
            // 使用正则表达式分割
            List<String> result1 = Arrays.stream(str.split("\\s*,\\s*"))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
            System.out.println("正则分割 '" + str + "': " + result1);
            
            // 转换为特定类型并处理
            List<String> result2 = Arrays.stream(str.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
            System.out.println("转小写处理 '" + str + "': " + result2);
            
            // 去重并排序
            List<String> result3 = Arrays.stream(str.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .distinct()
                    .sorted()
                    .collect(Collectors.toList());
            System.out.println("去重排序 '" + str + "': " + result3);
            System.out.println();
        }
    }
    
    /**
     * 工具方法: 简单的字符串分割
     */
    public static List<String> simpleSplit(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(input.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * 工具方法: 使用Stream的高级分割
     */
    public static List<String> advancedStreamSplit(String input, String delimiter) {
        return Optional.ofNullable(input)
                .filter(s -> !s.isEmpty())
                .map(s -> Arrays.stream(s.split(delimiter))
                        .map(String::trim)
                        .filter(str -> !str.isEmpty())
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
}