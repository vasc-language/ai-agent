package ai.agent.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 姚东名
 * Date: 2025-06-03
 * Time: 14:40
 */
@SpringBootTest
class ExaWebSearchToolTest {
    @Value("${search-api.api-key}")
    private String exaApiKey;
    @Test
    void exaSearch() {
        ExaWebSearchTool exaWebSearchTool = new ExaWebSearchTool(exaApiKey);
        String query = "查一下上海浦东有什么好吃的";
        String result = exaWebSearchTool.exaSearch(query);
        assertNotNull(result);
    }
}