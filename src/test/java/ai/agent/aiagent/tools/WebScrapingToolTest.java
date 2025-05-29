package ai.agent.aiagent.tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("网页抓取工具测试")
public class WebScrapingToolTest {

    private WebScrapingTool webScrapingTool;

    @BeforeEach
    void setUp() {
        webScrapingTool = new WebScrapingTool();
    }

    @Test
    @DisplayName("测试抓取正常网页 - 百度首页")
    void testScrapeWebPage_BaiduHomePage() {
        String url = "https://www.baidu.com";
        String result = webScrapingTool.scrapeWebPage(url);
        
        assertNotNull(result, "抓取结果不应为null");
        assertFalse(result.trim().isEmpty(), "抓取结果不应为空");
        assertFalse(result.startsWith("Error"), "不应返回错误信息");
        
        // 验证HTML基本结构
        assertTrue(result.contains("<html"), "应包含HTML标签");
        assertTrue(result.contains("</html>"), "应包含HTML结束标签");
        assertTrue(result.contains("百度"), "应包含百度相关内容");
        
        System.out.println("=== 百度首页抓取结果 ===");
        System.out.println("内容长度: " + result.length() + " 字符");
        System.out.println("前500字符预览:");
        System.out.println(result.substring(0, Math.min(500, result.length())));
        System.out.println("========================");
    }

    @Test
    @DisplayName("测试抓取新闻网站 - 人民网")
    void testScrapeWebPage_NewsWebsite() {
        String url = "http://www.people.com.cn/";
        String result = webScrapingTool.scrapeWebPage(url);
        
        assertNotNull(result);
        assertFalse(result.trim().isEmpty());
        
        if (!result.startsWith("Error")) {
            assertTrue(result.contains("<html"), "应包含HTML标签");
            assertTrue(result.contains("人民网"), "应包含人民网相关内容");
            
            System.out.println("=== 人民网首页抓取结果 ===");
            System.out.println("内容长度: " + result.length() + " 字符");
            System.out.println("前300字符预览:");
            System.out.println(result.substring(0, Math.min(300, result.length())));
            System.out.println("========================");
        } else {
            System.out.println("人民网抓取失败: " + result);
        }
    }

    @Test
    @DisplayName("测试抓取技术网站 - GitHub")
    void testScrapeWebPage_GitHub() {
        String url = "https://github.com";
        String result = webScrapingTool.scrapeWebPage(url);
        
        assertNotNull(result);
        assertFalse(result.trim().isEmpty());
        
        if (!result.startsWith("Error")) {
            assertTrue(result.contains("<html"), "应包含HTML标签");
            
            System.out.println("=== GitHub首页抓取结果 ===");
            System.out.println("内容长度: " + result.length() + " 字符");
            System.out.println("前400字符预览:");
            System.out.println(result.substring(0, Math.min(400, result.length())));
            System.out.println("========================");
        } else {
            System.out.println("GitHub抓取失败: " + result);
        }
    }

    @Test
    @DisplayName("测试抓取静态HTML页面")
    void testScrapeWebPage_StaticPage() {
        String url = "https://httpbin.org/html";
        String result = webScrapingTool.scrapeWebPage(url);
        
        assertNotNull(result);
        
        if (!result.startsWith("Error")) {
            assertTrue(result.contains("<html"), "应包含HTML标签");
            assertTrue(result.contains("</html>"), "应包含HTML结束标签");
            
            System.out.println("=== 静态HTML页面抓取结果 ===");
            System.out.println("完整内容:");
            System.out.println(result);
            System.out.println("==========================");
        } else {
            System.out.println("静态页面抓取失败: " + result);
        }
    }

    @Test
    @DisplayName("测试无效URL")
    void testScrapeWebPage_InvalidUrl() {
        String url = "invalid-url";
        String result = webScrapingTool.scrapeWebPage(url);
        
        assertNotNull(result);
        assertTrue(result.startsWith("Error"), "无效URL应返回错误信息");
        
        System.out.println("=== 无效URL测试结果 ===");
        System.out.println("错误信息: " + result);
        System.out.println("====================");
    }

    @Test
    @DisplayName("测试不存在的网址")
    void testScrapeWebPage_NonExistentUrl() {
        String url = "https://this-website-definitely-does-not-exist-12345.com";
        String result = webScrapingTool.scrapeWebPage(url);
        
        assertNotNull(result);
        assertTrue(result.startsWith("Error"), "不存在的网址应返回错误信息");
        
        System.out.println("=== 不存在网址测试结果 ===");
        System.out.println("错误信息: " + result);
        System.out.println("======================");
    }

    @Test
    @DisplayName("测试HTTPS网站")
    void testScrapeWebPage_HttpsWebsite() {
        String url = "https://www.google.com";
        String result = webScrapingTool.scrapeWebPage(url);
        
        assertNotNull(result);
        
        if (!result.startsWith("Error")) {
            assertTrue(result.contains("<html"), "应包含HTML标签");
            
            System.out.println("=== HTTPS网站抓取结果 ===");
            System.out.println("内容长度: " + result.length() + " 字符");
            System.out.println("前200字符预览:");
            System.out.println(result.substring(0, Math.min(200, result.length())));
            System.out.println("=======================");
        } else {
            System.out.println("HTTPS网站抓取失败: " + result);
        }
    }

    @Test
    @DisplayName("测试中文网站编码")
    void testScrapeWebPage_ChineseWebsite() {
        String url = "https://www.sina.com.cn";
        String result = webScrapingTool.scrapeWebPage(url);
        
        assertNotNull(result);
        
        if (!result.startsWith("Error")) {
            assertTrue(result.contains("<html"), "应包含HTML标签");
            // 检查中文编码是否正确
            boolean hasChineseContent = result.contains("新浪") || 
                                      result.contains("中国") || 
                                      result.contains("新闻");
            
            System.out.println("=== 中文网站抓取结果 ===");
            System.out.println("内容长度: " + result.length() + " 字符");
            System.out.println("包含中文内容: " + hasChineseContent);
            System.out.println("前300字符预览:");
            System.out.println(result.substring(0, Math.min(300, result.length())));
            System.out.println("======================");
        } else {
            System.out.println("中文网站抓取失败: " + result);
        }
    }

    @Test
    @DisplayName("测试API接口返回JSON")
    void testScrapeWebPage_JsonApi() {
        String url = "https://httpbin.org/json";
        String result = webScrapingTool.scrapeWebPage(url);
        
        assertNotNull(result);
        
        if (!result.startsWith("Error")) {
            // JSON API通常返回JSON格式，被包装在HTML中
            System.out.println("=== JSON API抓取结果 ===");
            System.out.println("完整内容:");
            System.out.println(result);
            System.out.println("======================");
        } else {
            System.out.println("JSON API抓取失败: " + result);
        }
    }

    @Test
    @DisplayName("测试原始提供的网站")
    void testScrapeWebPage_OriginalWebsite() {
        String url = "http://www.cnsoftbei.com/";
        String result = webScrapingTool.scrapeWebPage(url);
        
        assertNotNull(result);
        
        if (!result.startsWith("Error")) {
            assertTrue(result.contains("<html"), "应包含HTML标签");
            
            System.out.println("=== 中国软件杯网站抓取结果 ===");
            System.out.println("内容长度: " + result.length() + " 字符");
            System.out.println("前500字符预览:");
            System.out.println(result.substring(0, Math.min(500, result.length())));
            System.out.println("===========================");
        } else {
            System.out.println("中国软件杯网站抓取失败: " + result);
        }
    }
}
