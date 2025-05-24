package ai.agent.aiagent.demo.invoke;

public class TestApiKey {
    // 从系统环境变量中获取API_KEY，如果不存在则使用默认值
    public static final String API_KEY = System.getenv("AI_DASHSCOPE_API_KEY") != null ? 
                                      System.getenv("AI_DASHSCOPE_API_KEY") : "";
}
