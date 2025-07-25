import java.util.*;
import java.util.stream.Collectors;

/**
 * 字符串分割工具类
 * 提供多种常用的字符串分割方法
 */
public class StringSplitUtils {
    
    /**
     * 最常用的方法：按逗号分割字符串，自动去除空格和空元素
     * 
     * @param input 输入字符串
     * @return 分割后的字符串列表
     */
    public static List<String> split(String input) {
        return splitByDelimiter(input, ",", true, true);
    }
    
    /**
     * 按指定分隔符分割字符串
     * 
     * @param input 输入字符串
     * @param delimiter 分隔符
     * @return 分割后的字符串列表
     */
    public static List<String> splitByDelimiter(String input, String delimiter) {
        return splitByDelimiter(input, delimiter, true, true);
    }
    
    /**
     * 完整的分割方法，支持各种选项
     * 
     * @param input 输入字符串
     * @param delimiter 分隔符
     * @param trimResults 是否去除每个元素的首尾空格
     * @param omitEmpty 是否忽略空字符串
     * @return 分割后的字符串列表
     */
    public static List<String> splitByDelimiter(String input, String delimiter, 
                                              boolean trimResults, boolean omitEmpty) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        
        if (delimiter == null) {
            delimiter = ",";
        }
        
        return Arrays.stream(input.split(delimiter))
                .map(s -> trimResults ? s.trim() : s)
                .filter(s -> !omitEmpty || !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * 使用正则表达式分割，可以处理复杂的分隔符模式
     * 
     * @param input 输入字符串
     * @param regexPattern 正则表达式模式
     * @return 分割后的字符串列表
     */
    public static List<String> splitByRegex(String input, String regexPattern) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(input.split(regexPattern))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * 分割并去重
     * 
     * @param input 输入字符串
     * @param delimiter 分隔符
     * @return 去重后的字符串列表
     */
    public static List<String> splitAndDistinct(String input, String delimiter) {
        return splitByDelimiter(input, delimiter, true, true)
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }
    
    /**
     * 分割、去重并排序
     * 
     * @param input 输入字符串
     * @param delimiter 分隔符
     * @return 去重并排序后的字符串列表
     */
    public static List<String> splitDistinctAndSort(String input, String delimiter) {
        return splitByDelimiter(input, delimiter, true, true)
                .stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
    
    /**
     * 分割并转换为Set
     * 
     * @param input 输入字符串
     * @param delimiter 分隔符
     * @return 字符串Set
     */
    public static Set<String> splitToSet(String input, String delimiter) {
        return splitByDelimiter(input, delimiter, true, true)
                .stream()
                .collect(Collectors.toSet());
    }
    
    /**
     * 限制分割数量
     * 
     * @param input 输入字符串
     * @param delimiter 分隔符
     * @param limit 最大分割数量
     * @return 分割后的字符串列表
     */
    public static List<String> splitWithLimit(String input, String delimiter, int limit) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        
        return Arrays.stream(input.split(delimiter, limit))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * 分割并转换为指定类型
     * 
     * @param input 输入字符串
     * @param delimiter 分隔符
     * @param mapper 转换函数
     * @param <T> 目标类型
     * @return 转换后的列表
     */
    public static <T> List<T> splitAndMap(String input, String delimiter, 
                                         java.util.function.Function<String, T> mapper) {
        return splitByDelimiter(input, delimiter, true, true)
                .stream()
                .map(mapper)
                .collect(Collectors.toList());
    }
    
    /**
     * 常用的预定义分割方法
     */
    public static class CommonSplitters {
        
        /**
         * 按逗号分割
         */
        public static List<String> byComma(String input) {
            return split(input);
        }
        
        /**
         * 按分号分割
         */
        public static List<String> bySemicolon(String input) {
            return splitByDelimiter(input, ";");
        }
        
        /**
         * 按空格分割
         */
        public static List<String> bySpace(String input) {
            return splitByRegex(input, "\\s+");
        }
        
        /**
         * 按制表符分割
         */
        public static List<String> byTab(String input) {
            return splitByDelimiter(input, "\t");
        }
        
        /**
         * 按换行符分割
         */
        public static List<String> byNewline(String input) {
            return splitByRegex(input, "\\r?\\n");
        }
        
        /**
         * 按管道符分割
         */
        public static List<String> byPipe(String input) {
            return splitByDelimiter(input, "|");
        }
    }
    
    // 示例和测试
    public static void main(String[] args) {
        System.out.println("=== 字符串分割工具类演示 ===\n");
        
        // 基本分割
        String fruits = "apple,banana,orange,grape";
        System.out.println("基本分割: " + split(fruits));
        
        // 带空格的分割
        String fruitsWithSpaces = "apple, banana , orange, grape ";
        System.out.println("带空格分割: " + split(fruitsWithSpaces));
        
        // 包含空元素
        String fruitsWithEmpty = "apple,,banana,";
        System.out.println("过滤空元素: " + split(fruitsWithEmpty));
        
        // 去重排序
        String duplicateFruits = "apple,banana,apple,orange,banana";
        System.out.println("去重排序: " + splitDistinctAndSort(duplicateFruits, ","));
        
        // 不同分隔符
        System.out.println("分号分割: " + CommonSplitters.bySemicolon("a;b;c;d"));
        System.out.println("空格分割: " + CommonSplitters.bySpace("hello world java"));
        
        // 转换为数字
        String numbers = "1,2,3,4,5";
        List<Integer> intList = splitAndMap(numbers, ",", Integer::parseInt);
        System.out.println("转换为数字: " + intList);
        
        // 限制分割数量
        String longString = "a,b,c,d,e,f,g";
        System.out.println("限制分割(3): " + splitWithLimit(longString, ",", 3));
    }
}