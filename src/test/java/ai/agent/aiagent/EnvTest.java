package ai.agent.aiagent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EnvTest {

    @Value("${AI_DASHSCOPE_API_KEY}")
    private String apiKey;

    @Test
    public void testApiKey() {
        System.out.println("API Key: " + apiKey);
        assert apiKey != null && !apiKey.isEmpty() : "API Key should not be null or empty";
    }
} 