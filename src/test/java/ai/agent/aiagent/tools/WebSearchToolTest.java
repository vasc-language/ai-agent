package ai.agent.aiagent.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class WebSearchToolTest {

    @Value("${search-api.api-key}")
    private String searchApiKey;
    
    private WebSearchTool webSearchTool;
    private WebSearchTool debugWebSearchTool;

    @BeforeEach
    void setUp() {
        webSearchTool = new WebSearchTool(searchApiKey);
        debugWebSearchTool = new WebSearchTool(searchApiKey, true, 3);
    }

    @Test
    void testSearchWeb_ValidQuery() {
        String query = "程序员鱼皮编程导航 codefather.cn";
        String result = webSearchTool.searchWeb(query);
        
        assertNotNull(result, "搜索结果不应为null");
        assertFalse(result.trim().isEmpty(), "搜索结果不应为空");
        assertFalse(result.startsWith("错误"), "不应返回错误信息");
        
        System.out.println("搜索结果：");
        System.out.println(result);
    }
    
    @Test
    void testSearchWeb_EmptyQuery() {
        String result = webSearchTool.searchWeb("");
        assertTrue(result.contains("搜索关键词不能为空"), "空查询应返回错误信息");
    }
    
    @Test
    void testSearchWeb_NullQuery() {
        String result = webSearchTool.searchWeb(null);
        assertTrue(result.contains("搜索关键词不能为空"), "null查询应返回错误信息");
    }
    
    @Test
    void testSearchWeb_CommonQuery() {
        String query = "Java Spring Boot";
        String result = webSearchTool.searchWeb(query);
        
        assertNotNull(result);
        assertFalse(result.trim().isEmpty());
        
        System.out.println("通用查询结果：");
        System.out.println(result);
    }
    
    @Test
    void testSearchWeb_DebugMode() {
        String query = "人工智能";
        String result = debugWebSearchTool.searchWeb(query);
        
        assertNotNull(result);
        assertFalse(result.trim().isEmpty());
        
        System.out.println("调试模式搜索结果：");
        System.out.println(result);
    }
    
    @Test
    void testSearchWeb_SpecialCharacters() {
        String query = "程序员 & 软件开发";
        String result = webSearchTool.searchWeb(query);
        
        assertNotNull(result);
        // 特殊字符不应导致崩溃
    }
}
