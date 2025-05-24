package ai.agent.aiagent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PgVector向量存储与恋爱文档关联测试类
 * 测试与document路径下三篇恋爱文章的关联功能：
 * - 恋爱常见问题与解答-单身篇.md
 * - 恋爱常见问题与解答-恋爱篇.md
 * - 恋爱常见问题与解答-已婚篇.md
 * 
 * Created with IntelliJ IDEA.
 * User: 姚东名
 * Date: 2025-05-25
 */
@Slf4j
@SpringBootTest
class PgVectorLoveDocumentsTest {

    @Resource
    private VectorStore pgVectorVectorStore;
    
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    /**
     * 测试文档加载功能
     * 验证是否正确加载了三篇恋爱文档
     */
    @Test
    void testDocumentLoading() {
        log.info("=== 测试文档加载功能 ===");
        
        // 加载文档
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        
        // 验证文档数量（每个markdown文件可能被分割成多个Document）
        assertNotNull(documents, "文档列表不应为null");
        assertFalse(documents.isEmpty(), "应该加载到文档");
        
        log.info("成功加载 {} 个文档片段", documents.size());
        
        // 验证文档包含正确的文件名元数据
        boolean hasSingleDoc = documents.stream()
            .anyMatch(doc -> "恋爱常见问题与解答-单身篇.md".equals(doc.getMetadata().get("filename")));
        boolean hasDatingDoc = documents.stream()
            .anyMatch(doc -> "恋爱常见问题与解答-恋爱篇.md".equals(doc.getMetadata().get("filename")));
        boolean hasMarriedDoc = documents.stream()
            .anyMatch(doc -> "恋爱常见问题与解答-已婚篇.md".equals(doc.getMetadata().get("filename")));
        
        assertTrue(hasSingleDoc, "应该包含单身篇文档");
        assertTrue(hasDatingDoc, "应该包含恋爱篇文档");
        assertTrue(hasMarriedDoc, "应该包含已婚篇文档");
        
        // 打印文档详情
        documents.forEach(doc -> {
            log.info("文档文件名: {}, 内容片段: {}", 
                doc.getMetadata().get("filename"), 
                doc.getText().substring(0, Math.min(100, doc.getText().length())) + "...");
        });
    }

    /**
     * 测试单身状态相关查询
     */
    @Test
    void testSingleStatusQuery() {
        log.info("=== 测试单身状态相关查询 ===");
        
        String query = "单身如何脱单，怎么找到合适的对象";
        List<Document> results = pgVectorVectorStore.similaritySearch(
            SearchRequest.builder()
                .query(query)
                .topK(3)
                .similarityThreshold(0.5)
                .build()
        );
        
        assertNotNull(results, "查询结果不应为null");
        assertFalse(results.isEmpty(), "应该找到相关文档");
        
        log.info("查询: '{}', 找到 {} 个相关文档", query, results.size());
        
        // 验证结果中包含单身篇相关内容
        boolean containsSingleContent = results.stream()
            .anyMatch(doc -> {
                String filename = (String) doc.getMetadata().get("filename");
                return filename != null && filename.contains("单身篇");
            });
        
        assertTrue(containsSingleContent, "查询结果应该包含单身篇相关内容");
        
        // 打印查询结果
        results.forEach(doc -> {
            log.info("相关文档: {}, 内容: {}", 
                doc.getMetadata().get("filename"),
                doc.getText().substring(0, Math.min(200, doc.getText().length())) + "...");
        });
    }

    /**
     * 测试恋爱状态相关查询
     */
    @Test
    void testDatingStatusQuery() {
        log.info("=== 测试恋爱状态相关查询 ===");
        
        String query = "恋爱中沟通困难，情侣吵架怎么办";
        List<Document> results = pgVectorVectorStore.similaritySearch(
            SearchRequest.builder()
                .query(query)
                .topK(3)
                .similarityThreshold(0.5)
                .build()
        );
        
        assertNotNull(results, "查询结果不应为null");
        assertFalse(results.isEmpty(), "应该找到相关文档");
        
        log.info("查询: '{}', 找到 {} 个相关文档", query, results.size());
        
        // 验证结果中包含恋爱篇相关内容
        boolean containsDatingContent = results.stream()
            .anyMatch(doc -> {
                String filename = (String) doc.getMetadata().get("filename");
                return filename != null && filename.contains("恋爱篇");
            });
        
        assertTrue(containsDatingContent, "查询结果应该包含恋爱篇相关内容");
        
        // 打印查询结果
        results.forEach(doc -> {
            log.info("相关文档: {}, 内容: {}", 
                doc.getMetadata().get("filename"),
                doc.getText().substring(0, Math.min(200, doc.getText().length())) + "...");
        });
    }

//    /**
//     * 测试已婚状态相关查询
//    @Test
//    void testMarriedStatusQuery() {
//        log.info("=== 测试已婚状态相关查询 ===");
//
//        String query = "婚后感情变淡，夫妻关系不和谐";
//        List<Document> results = pgVectorVectorStore.similaritySearch(
//            SearchRequest.builder()
//                .query(query)
//                .topK(3)
//                .similarityThreshold(0.5)
//                .build()
//        );
//
//        assertNotNull(results, "查询结果不应为null");
//        assertFalse(results.isEmpty(), "应该找到相关文档");
//
//        log.info("查询: '{}', 找到 {} 个相关文档", query, results.size());
//
//        // 验证结果中包含已婚篇相关内容
//        boolean containsMarriedContent = results.stream()
//            .anyMatch(doc -> {
//                String filename = (String) doc.getMetadata().get("filename");
//                return filename != null && filename.contains("已婚篇");
//            });
//
//        assertTrue(containsMarriedContent, "查询结果应该包含已婚篇相关内容");
//
//        // 打印查询结果
//        results.forEach(doc -> {
//            log.info("相关文档: {}, 内容: {}",
//                doc.getMetadata().get("filename"),
//                doc.getText().substring(0, Math.min(200, doc.getText().length())) + "...");
//        });
//    }*/

    /**
     * 测试文档元数据
     * 验证每个文档是否包含正确的元数据信息
     */
    @Test
    void testDocumentMetadata() {
        log.info("=== 测试文档元数据 ===");
        
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        
        // 统计各文档的片段数量
        long singleCount = documents.stream()
            .filter(doc -> "恋爱常见问题与解答-单身篇.md".equals(doc.getMetadata().get("filename")))
            .count();
        long datingCount = documents.stream()
            .filter(doc -> "恋爱常见问题与解答-恋爱篇.md".equals(doc.getMetadata().get("filename")))
            .count();
        long marriedCount = documents.stream()
            .filter(doc -> "恋爱常见问题与解答-已婚篇.md".equals(doc.getMetadata().get("filename")))
            .count();
        
        log.info("单身篇文档片段数: {}", singleCount);
        log.info("恋爱篇文档片段数: {}", datingCount);
        log.info("已婚篇文档片段数: {}", marriedCount);
        
        assertTrue(singleCount > 0, "单身篇应该有文档片段");
        assertTrue(datingCount > 0, "恋爱篇应该有文档片段");
        assertTrue(marriedCount > 0, "已婚篇应该有文档片段");
        
        // 验证所有文档都有filename元数据
        documents.forEach(doc -> {
            assertNotNull(doc.getMetadata().get("filename"), "每个文档都应该有filename元数据");
            assertTrue(doc.getMetadata().get("filename").toString().endsWith(".md"), 
                "filename应该以.md结尾");
        });
    }

    /**
     * 测试跨状态查询
     * 验证通用恋爱问题能否匹配到多个状态的文档
     */
    @Test
    void testCrossStatusQueries() {
        log.info("=== 测试跨状态查询 ===");
        
        String generalQuery = "恋爱沟通技巧";
        List<Document> results = pgVectorVectorStore.similaritySearch(
            SearchRequest.builder()
                .query(generalQuery)
                .topK(5)
                .similarityThreshold(0.4)
                .build()
        );
        
        assertNotNull(results, "查询结果不应为null");
        assertFalse(results.isEmpty(), "应该找到相关文档");
        
        log.info("通用查询: '{}', 找到 {} 个相关文档", generalQuery, results.size());
        
        // 统计不同状态文档的出现次数
        long singleMatches = results.stream()
            .filter(doc -> {
                String filename = (String) doc.getMetadata().get("filename");
                return filename != null && filename.contains("单身篇");
            })
            .count();
        
        long datingMatches = results.stream()
            .filter(doc -> {
                String filename = (String) doc.getMetadata().get("filename");
                return filename != null && filename.contains("恋爱篇");
            })
            .count();
        
        long marriedMatches = results.stream()
            .filter(doc -> {
                String filename = (String) doc.getMetadata().get("filename");
                return filename != null && filename.contains("已婚篇");
            })
            .count();
        
        log.info("匹配结果 - 单身篇: {}, 恋爱篇: {}, 已婚篇: {}", 
            singleMatches, datingMatches, marriedMatches);
        
        // 验证至少匹配到一种状态的文档
        assertTrue(singleMatches + datingMatches + marriedMatches > 0, 
            "通用查询应该至少匹配到一种状态的文档");
        
        // 打印详细结果
        results.forEach(doc -> {
            log.info("匹配文档: {}, 内容片段: {}", 
                doc.getMetadata().get("filename"),
                doc.getText().substring(0, Math.min(150, doc.getText().length())) + "...");
        });
    }

    /**
     * 测试向量存储配置
     * 验证PgVectorVectorStore的基本配置是否正确
     */
    @Test
    void testVectorStoreConfiguration() {
        log.info("=== 测试向量存储配置 ===");
        
        assertNotNull(pgVectorVectorStore, "PgVectorVectorStore不应为null");
        
        // 测试基本的添加和查询功能
        Document testDoc = new Document("这是一个测试文档，用于验证向量存储功能");
        pgVectorVectorStore.add(List.of(testDoc));
        
        List<Document> searchResults = pgVectorVectorStore.similaritySearch(
            SearchRequest.builder()
                .query("测试文档")
                .topK(1)
                .build()
        );
        
        assertNotNull(searchResults, "搜索结果不应为null");
        assertFalse(searchResults.isEmpty(), "应该能找到测试文档");
        
        log.info("向量存储配置测试通过，能够正常添加和查询文档");
    }
}