package ai.agent.aiagent.demo;

import ai.agent.aiagent.agent.Manus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 人机交互功能演示
 * 展示Manus如何通过AskHumanTool与用户进行交互
 */
@Slf4j
@Component
public class HumanInteractionDemo implements CommandLineRunner {

    @Autowired
    private Manus manus;

    @Override
    public void run(String... args) throws Exception {
        // 如果启动参数包含 --demo=human-interaction，则运行演示
        if (args.length > 0 && "human-interaction".equals(args[0].replace("--demo=", ""))) {
            runDemo();
        }
    }

    public void runDemo() {
        log.info("=== Manus 人机交互功能演示 (使用AskHumanTool) ===");
        
        // 测试场景1：基本问答
        System.out.println("\n" + "=".repeat(60));
        System.out.println("测试场景1：让Manus询问用户喜好并做推荐");
        System.out.println("=".repeat(60));
        
        String result1 = manus.run("我想找一个好的餐厅，但我不确定想吃什么类型的食物。请帮我选择。");
        System.out.println("Manus执行结果:");
        System.out.println(result1);
        
        // 测试场景2：确认操作
        System.out.println("\n" + "=".repeat(60));
        System.out.println("测试场景2：让Manus执行文件操作前确认");
        System.out.println("=".repeat(60));
        
        String result2 = manus.run("请帮我创建一个名为'test_document.txt'的文件，内容为'Hello World'，但在创建前请确认。");
        System.out.println("Manus执行结果:");
        System.out.println(result2);
        
        // 测试场景3：收集用户信息
        System.out.println("\n" + "=".repeat(60));
        System.out.println("测试场景3：让Manus收集用户信息");
        System.out.println("=".repeat(60));
        
        String result3 = manus.run("我需要创建一个用户档案。请收集我的姓名、邮箱和年龄信息。");
        System.out.println("Manus执行结果:");
        System.out.println(result3);
        
        log.info("=== 演示完成 ===");
    }
    
    /**
     * 手动运行演示的方法
     */
    public void manualDemo() {
        runDemo();
    }
} 