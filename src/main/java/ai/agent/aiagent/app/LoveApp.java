package ai.agent.aiagent.app;

import ai.agent.aiagent.advisor.MyLoggerAdvisor;
import ai.agent.aiagent.advisor.ReReadingAdvisor;
import ai.agent.aiagent.chatmemory.FileBasedChatMemory;
import ai.agent.aiagent.rag.LoveAppRagCustomAdvisorFactory;
import ai.agent.aiagent.rag.QueryRewriter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Lazy;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 姚东名
 * Date: 2025-05-24
 * Time: 18:10
 */
@Slf4j
@Component
public class LoveApp {

    private final ChatClient chatClient;
    @Resource
    private VectorStore loveAppVectorStore; // 基于内存存储的向量数据库
    @Resource
    private Advisor loveAppRagCloudAdvisor; // 基于云知识库的检索增强服务
    @Resource
    private QueryRewriter queryRewriter; // 查询重写器
    @Resource
    private ToolCallback[] allTools; // 调用工具箱
    @Resource
    @Lazy // 延迟初始化，避免启动时立即连接MCP服务器
    private ToolCallbackProvider toolCallbackProvider; // 调用 MCP server 工具

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";


    public LoveApp(ChatModel dashscopeChatModel) {
        // 初始化基于文件的对话记忆
        String fileDir = System.getProperty("user.dir") + "/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();
    }


    /**
     * 编写对话方法
     */
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(advisorSpec -> advisorSpec
                        // 指定对话ID和对话记忆大小
                        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * 定义恋爱报告类
     */
    record LoveReport(String title, List<String> suggestions) {

    }

    /**
     * 复用之前的chatClient对象，在原来的添加结构化输出代码即可
     */
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                // 构建请求链中的一个运行配置的方法，这些 Advisor 会每次请求时都会被应用
                // 为已存在的 Advisor 传递参数，这些参数会被添加到请求的 userParam
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }

    /**
     * 添加问答拦截器 QuestionAnswerAdvisor
     * 查询增强的原理其实很简单。向量数据库存储着 AI 模型本身不知道的数据，当用户问题发送给 AI 模型时，
     * QuestionAnswerAdvisor 会查询向量数据库，获取与用户问题相关的文档。然后从向量数据库返回的响应会被附加到用户文本中，
     * 为 AI 模型提供上下文，帮助其生成回答。
     */
    public String doChatWithRag(String message, String chatId) {
        String queryRewriteMessage = queryRewriter.doQueryRewrite(message);
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(queryRewriteMessage)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                // 应用 RAG 知识库问答
                // .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                // 应用增强检索服务（云知识库服务）
                .advisors(loveAppRagCloudAdvisor)
                // 应用自定义的 RAG 检索增强生成服务（文档检索器 + 上下文增强器）
                .advisors(
                        LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(
                                loveAppVectorStore, "已婚"
                        )
                )
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * 工具调用
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    /**
     * AI 调用MCP服务
     */
    public String doChatWithMcp(String message, String chatId) {
        try {
            ChatResponse chatResponse = chatClient
                    .prompt()
                    .user(message)
                    .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                    // 开启日志，便于观察效果
                    .advisors(new MyLoggerAdvisor())
                    .tools(toolCallbackProvider)
                    .call()
                    .chatResponse();
            String content = chatResponse.getResult().getOutput().getText();
            log.info("content: {}", content);
            return content;
        } catch (Exception e) {
            log.error("MCP服务调用失败，降级到普通对话模式: {}", e.getMessage(), e);
            // 降级到普通对话模式
            return doChat(message, chatId);
        }
    }

}
