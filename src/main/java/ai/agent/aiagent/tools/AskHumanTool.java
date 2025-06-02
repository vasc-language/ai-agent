package ai.agent.aiagent.tools;

import ai.agent.aiagent.model.AgentState;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * 人机交互工具类
 * 合并了HumanInteractionTool和AskHuman的功能
 * 既提供Spring AI工具接口，又包含完整的人机交互逻辑
 */
@Slf4j
@Component
public class AskHumanTool {
    
    private int maxRetries = 3;
    
    /**
     * 向用户询问问题并获取回答
     * 
     * @param question 要询问的问题
     * @return 用户的回答
     */
    @Tool(name = "askQuestion", description = "向用户询问问题并获取文本回答。当你需要从用户那里获取信息、意见或指示时使用此工具。")
    public String askQuestion(String question) {
        try {
            log.info("AI正在询问用户: {}", question);
            String userResponse = performBasicInteraction(question);
            log.info("用户回答: {}", userResponse);
            return userResponse != null ? userResponse : "用户未提供回答";
        } catch (Exception e) {
            log.error("询问用户时发生错误", e);
            return "交互过程中发生错误: " + e.getMessage();
        }
    }

    /**
     * 向用户提供选择题
     * 
     * @param question 问题描述
     * @param options 选项，用逗号分隔
     * @return 用户选择的选项内容
     */
    @Tool(name = "askChoice", description = "向用户提供多个选项让其选择。options参数应该是用逗号分隔的选项列表。返回用户选择的具体选项内容。")
    public String askChoice(String question, String options) {
        try {
            log.info("AI正在向用户提供选择: {} 选项: {}", question, options);
            String[] optionArray = options.split(",");
            for (int i = 0; i < optionArray.length; i++) {
                optionArray[i] = optionArray[i].trim();
            }
            
            int choiceIndex = performChoiceInteraction(question, optionArray);
            String selectedOption = optionArray[choiceIndex - 1]; // 返回的是1开始的索引
            log.info("用户选择了: {}", selectedOption);
            return selectedOption;
        } catch (Exception e) {
            log.error("提供选择时发生错误", e);
            return "选择过程中发生错误: " + e.getMessage();
        }
    }

    /**
     * 向用户询问确认问题
     * 
     * @param question 确认问题
     * @return 确认结果，"确认"或"拒绝"
     */
    @Tool(name = "askConfirmation", description = "向用户询问是否确认某项操作。返回'确认'或'拒绝'。")
    public String askConfirmation(String question) {
        try {
            log.info("AI正在询问用户确认: {}", question);
            boolean confirmed = performConfirmationInteraction(question);
            String result = confirmed ? "确认" : "拒绝";
            log.info("用户确认结果: {}", result);
            return result;
        } catch (Exception e) {
            log.error("确认询问时发生错误", e);
            return "确认过程中发生错误: " + e.getMessage();
        }
    }

    /**
     * 向用户询问数字输入
     * 
     * @param question 问题描述
     * @param min 最小值
     * @param max 最大值
     * @return 用户输入的数字
     */
    @Tool(name = "askNumber", description = "向用户询问数字输入，可以指定数值范围。")
    public String askNumber(String question, int min, int max) {
        try {
            log.info("AI正在询问用户数字输入: {} 范围: {}-{}", question, min, max);
            
            Predicate<String> validator = input -> {
                try {
                    int number = Integer.parseInt(input);
                    return number >= min && number <= max;
                } catch (NumberFormatException e) {
                    return false;
                }
            };
            
            String fullQuestion = String.format("%s (请输入 %d-%d 之间的数字)", question, min, max);
            String errorMessage = String.format("请输入有效的数字 (%d-%d)", min, max);
            
            String response = performValidatedInteraction(fullQuestion, validator, errorMessage);
            log.info("用户输入的数字: {}", response);
            return response;
        } catch (Exception e) {
            log.error("数字询问时发生错误", e);
            return "数字输入过程中发生错误: " + e.getMessage();
        }
    }

    /**
     * 向用户询问邮箱地址
     * 
     * @param question 问题描述
     * @return 用户输入的邮箱地址
     */
    @Tool(name = "askEmail", description = "向用户询问邮箱地址，会验证邮箱格式的有效性。")
    public String askEmail(String question) {
        try {
            log.info("AI正在询问用户邮箱: {}", question);
            
            Predicate<String> emailValidator = input -> {
                return input != null && input.matches("^[A-Za-z0-9+_.-]+@(.+)$");
            };
            
            String response = performValidatedInteraction(
                question + " (请输入有效的邮箱地址)", 
                emailValidator, 
                "请输入有效的邮箱地址格式"
            );
            log.info("用户输入的邮箱: {}", response);
            return response;
        } catch (Exception e) {
            log.error("邮箱询问时发生错误", e);
            return "邮箱输入过程中发生错误: " + e.getMessage();
        }
    }
    
    // ========== 以下是内部交互实现方法 ==========
    
    /**
     * 执行基本的问答交互
     * 
     * @param question 问题
     * @return 用户回答
     */
    private String performBasicInteraction(String question) {
        if (StrUtil.isBlank(question)) {
            return "没有问题需要询问";
        }
        
        try {
            // 显示问题给用户
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Bot: " + question);
            System.out.println("=".repeat(50));
            System.out.print("You: ");
            System.out.flush(); // 强制刷新输出缓冲区
            
            // 创建新的Scanner实例，避免静态Scanner被关闭的问题
            Scanner scanner = new Scanner(System.in);
            
            // 检查Scanner是否可用
            if (!scanner.hasNextLine()) {
                scanner.close();
                return "输入流不可用";
            }
            
            // 读取用户输入
            String input = scanner.nextLine().trim();
            
            // 注意：不关闭Scanner，因为它会关闭System.in
            
            if (StrUtil.isBlank(input)) {
                return "用户输入为空";
            }
            
            return input;
        } catch (Exception e) {
            log.error("读取用户输入时发生错误", e);
            return "读取输入失败: " + e.getMessage();
        }
    }
    
    /**
     * 执行选择题交互
     * 
     * @param question 问题
     * @param options 选项数组
     * @return 用户选择的选项索引（从1开始）
     */
    private int performChoiceInteraction(String question, String[] options) {
        StringBuilder prompt = new StringBuilder(question);
        prompt.append("\n\n选项:");
        
        for (int i = 0; i < options.length; i++) {
            prompt.append(String.format("\n%d. %s", i + 1, options[i]));
        }
        prompt.append("\n\n请输入选项编号 (1-").append(options.length).append("):");
        
        String response = performValidatedInteraction(
            prompt.toString(),
            input -> {
                try {
                    int choice = Integer.parseInt(input);
                    return choice >= 1 && choice <= options.length;
                } catch (NumberFormatException e) {
                    return false;
                }
            },
            "请输入有效的选项编号 (1-" + options.length + ")"
        );
        
        return Integer.parseInt(response);
    }
    
    /**
     * 执行确认交互
     * 
     * @param question 确认问题
     * @return true表示确认，false表示拒绝
     */
    private boolean performConfirmationInteraction(String question) {
        String response = performValidatedInteraction(
            question + " (请输入 y/yes/是 表示确认，n/no/否 表示拒绝)",
            input -> {
                String lower = input.toLowerCase();
                return lower.equals("y") || lower.equals("yes") || lower.equals("是") ||
                       lower.equals("n") || lower.equals("no") || lower.equals("否");
            },
            "请输入有效的确认选项 (y/yes/是 或 n/no/否)"
        );
        
        String lower = response.toLowerCase();
        return lower.equals("y") || lower.equals("yes") || lower.equals("是");
    }
    
    /**
     * 执行带验证的交互
     * 
     * @param question 问题
     * @param validator 验证器
     * @param errorMessage 验证失败时的错误消息
     * @return 用户回答
     */
    private String performValidatedInteraction(String question, Predicate<String> validator, String errorMessage) {
        int currentRetry = 0;
        
        while (currentRetry < maxRetries) {
            String response = performBasicInteraction(question);
            
            if (response != null && validator.test(response)) {
                return response;
            }
            
            currentRetry++;
            if (currentRetry < maxRetries) {
                System.out.println(errorMessage + " (" + currentRetry + "/" + maxRetries + ")");
                System.out.flush();
            }
        }
        
        throw new RuntimeException("用户输入验证失败，已达到最大重试次数");
    }
    
    /**
     * 异步询问用户
     * 
     * @param question 问题
     * @param timeoutSeconds 超时时间（秒）
     * @return CompletableFuture包装的用户回答
     */
    public CompletableFuture<String> askAsync(String question, int timeoutSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            return performBasicInteraction(question);
        }).orTimeout(timeoutSeconds, TimeUnit.SECONDS);
    }
    
    /**
     * 设置最大重试次数
     * 
     * @param maxRetries 最大重试次数
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }
} 