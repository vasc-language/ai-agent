package ai.agent.aiagent.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 添加以下导入语句
import ai.agent.aiagent.tools.FileOperationTool;
import ai.agent.aiagent.tools.WebSearchTool;
import ai.agent.aiagent.tools.WebScrapingTool;
import ai.agent.aiagent.tools.ResourceDownloadTool;
import ai.agent.aiagent.tools.TerminalOperationTool;
import ai.agent.aiagent.tools.PDFGenerationTool;
import ai.agent.aiagent.tools.TerminateTool;

/**
 * 集中注册：可以给AI一次性提供所有的工具
 * 1. 工厂模式：allTools() 作为一个工厂方法，负责创建和配置多个工厂实例，然后包装成统一的数组返回。符合工厂模式的核心：集中创建对象并隐藏创建细节
 * 2. 依赖注入模式
 * 3. 注册模式
 * 4. 适配器模式的应用：ToolCallback.from 方法可以看成一种适配器，他将不同的工具转换成统一的 ToolCallback 数组，使系统能够以一致的方式处理它们
 */
@Configuration
public class ToolRegistration {
    @Value("${search-api.api-key}")
    private String searchApiKey;

    @Bean
    public ToolCallback[] allTools() {
        // 文件操作工具
        FileOperationTool fileOperationTool = new FileOperationTool();
        // 网页浏览工具
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        // 网页抓取工具
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        // 资源下载工具
        ResourceDownloadTool resourceDownloadTool = new ResourceDownloadTool();
        // 终端操作工具
        TerminalOperationTool terminalOperationTool = new TerminalOperationTool();
        // PDF 生成工具
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        // 任务终止工具
        TerminateTool terminateTool = new TerminateTool();
        // 人机交互工具
        // AskHumanTool askHumanTool = new AskHumanTool();

        return ToolCallbacks.from(
                fileOperationTool,
                webSearchTool,
                webScrapingTool,
                resourceDownloadTool,
                terminalOperationTool,
                pdfGenerationTool,
                terminateTool//,
                // askHumanTool
        );
    }
}
