package ai.agent.aiagent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 恋爱文档关联关系测试类
 * 专门测试三篇恋爱文档之间的内容关联和相似度分析
 * 
 * Created with IntelliJ IDEA.
 * User: 姚东名
 * Date: 2025-05-25
 */
@Slf4j
@SpringBootTest
class LoveDocumentRelationshipTest {

    @Resource
    private VectorStore pgVectorVectorStore;

    /**
     * 测试恋爱状态进阶查询
     * 模拟用户从单身到恋爱到已婚的进阶问题
     */
    @Test
    void testLoveStatusProgressionQueries() {
        log.info("=== 测试恋爱状态进阶查询 ===");
        
        // 定义不同阶段的问题
        List<String> progressionQueries = Arrays.asList(
            "如何从单身状态开始恋爱", // 单身→恋爱
            "恋爱中如何维持长期关系", // 恋爱→已婚准备
            "已婚后如何保持婚姻幸福"  // 已婚维护
        );
        
        progressionQueries.forEach(query -> {
            log.info("\n--- 查询: {} ---", query);
            
            List<Document> results = pgVectorVectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(query)
                    .topK(5)
                    .similarityThreshold(0.3)
                    .build()
            );
            
            assertNotNull(results, "查询结果不应为null");
            
            // 统计不同状态文档的匹配情况
            Map<String, Long> statusCounts = results.stream()
                .collect(Collectors.groupingBy(
                    doc -> extractStatus((String) doc.getMetadata().get("filename")),
                    Collectors.counting()
                ));
            
            log.info("匹配结果分布: {}", statusCounts);
            
            // 显示前3个最相关的结果
            results.stream()
                .limit(3)
                .forEach(doc -> {
                    String filename = (String) doc.getMetadata().get("filename");
                    String status = extractStatus(filename);
                    log.info("文档状态: {}, 内容: {}", 
                        status,
                        doc.getText().substring(0, Math.min(100, doc.getText().length())) + "...");
                });
        });
    }

    /**
     * 测试沟通问题的跨状态关联
     * 沟通是恋爱、已婚状态的共同话题
     */
    @Test
    void testCommunicationTopicCorrelation() {
        log.info("=== 测试沟通问题的跨状态关联 ===");
        
        String communicationQuery = "如何改善沟通，避免争吵";
        List<Document> results = pgVectorVectorStore.similaritySearch(
            SearchRequest.builder()
                .query(communicationQuery)
                .topK(6)
                .similarityThreshold(0.3)
                .build()
        );
        
        assertNotNull(results, "查询结果不应为null");
        assertFalse(results.isEmpty(), "应该找到沟通相关文档");
        
        // 验证沟通话题应该主要出现在恋爱篇和已婚篇
        long datingCommunication = results.stream()
            .filter(doc -> extractStatus((String) doc.getMetadata().get("filename")).equals("恋爱"))
            .count();
        
        long marriedCommunication = results.stream()
            .filter(doc -> extractStatus((String) doc.getMetadata().get("filename")).equals("已婚"))
            .count();
        
        log.info("沟通相关文档分布 - 恋爱篇: {}, 已婚篇: {}", datingCommunication, marriedCommunication);
        
        assertTrue(datingCommunication + marriedCommunication > 0, 
            "沟通话题应该在恋爱篇或已婚篇中找到相关内容");
        
        // 展示相关文档
        results.forEach(doc -> {
            String status = extractStatus((String) doc.getMetadata().get("filename"));
            log.info("状态: {}, 沟通建议: {}", 
                status,
                doc.getText().substring(0, Math.min(120, doc.getText().length())) + "...");
        });
    }

    /**
     * 测试情感发展阶段的内容关联
     */
    @Test
    void testEmotionalDevelopmentStages() {
        log.info("=== 测试情感发展阶段的内容关联 ===");
        
        // 测试不同发展阶段的关键词
        Map<String, String> stageQueries = Map.of(
            "初期", "如何开始一段感情，建立联系",
            "发展", "如何深化感情，增进了解", 
            "稳定", "如何维持长期稳定的关系",
            "危机", "如何处理感情危机和矛盾"
        );
        
        stageQueries.forEach((stage, query) -> {
            log.info("\n--- {}阶段查询: {} ---", stage, query);
            
            List<Document> results = pgVectorVectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(query)
                    .topK(4)
                    .similarityThreshold(0.3)
                    .build()
            );
            
            if (!results.isEmpty()) {
                // 显示该阶段最相关的建议
                Document topResult = results.get(0);
                String status = extractStatus((String) topResult.getMetadata().get("filename"));
                log.info("{}阶段最佳建议来源: {}, 内容: {}", 
                    stage, 
                    status,
                    topResult.getText().substring(0, Math.min(150, topResult.getText().length())) + "...");
                
                // 统计状态分布
                Map<String, Long> statusDistribution = results.stream()
                    .collect(Collectors.groupingBy(
                        doc -> extractStatus((String) doc.getMetadata().get("filename")),
                        Collectors.counting()
                    ));
                log.info("{}阶段相关状态分布: {}", stage, statusDistribution);
            }
        });
    }

    /**
     * 测试问题解决方案的关联性
     * 验证相似问题在不同状态下的解决方案差异
     */
    @Test
    void testProblemSolutionCorrelation() {
        log.info("=== 测试问题解决方案的关联性 ===");
        
        // 测试共同问题在不同状态下的解决方案
        List<String> commonIssues = Arrays.asList(
            "缺乏安全感怎么办",
            "对方不够关心我",
            "价值观不合适怎么办",
            "如何表达自己的需求"
        );
        
        commonIssues.forEach(issue -> {
            log.info("\n--- 问题: {} ---", issue);
            
            List<Document> solutions = pgVectorVectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(issue)
                    .topK(4)
                    .similarityThreshold(0.3)
                    .build()
            );
            
            if (!solutions.isEmpty()) {
                // 按状态分组显示解决方案
                Map<String, List<Document>> solutionsByStatus = solutions.stream()
                    .collect(Collectors.groupingBy(
                        doc -> extractStatus((String) doc.getMetadata().get("filename"))
                    ));
                
                solutionsByStatus.forEach((status, docs) -> {
                    log.info("{}状态的解决方案:", status);
                    docs.forEach(doc -> {
                        log.info("  - {}", 
                            doc.getText().substring(0, Math.min(100, doc.getText().length())) + "...");
                    });
                });
            }
        });
    }

    /**
     * 测试文档间的内容相似度分析
     */
    @Test
    void testDocumentContentSimilarity() {
        log.info("=== 测试文档间的内容相似度分析 ===");
        
        // 使用每个状态的典型内容作为查询，看看能否找到其他状态的相关内容
        Map<String, String> statusRepresentativeContent = Map.of(
            "单身", "社交圈扩展和脱单技巧",
            "恋爱", "情侣沟通和关系维护", 
            "已婚", "婚姻经营和家庭和谐"
        );
        
        statusRepresentativeContent.forEach((sourceStatus, content) -> {
            log.info("\n--- 使用{}状态内容查找其他关联内容 ---", sourceStatus);
            
            List<Document> crossReferences = pgVectorVectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(content)
                    .topK(6)
                    .similarityThreshold(0.2)
                    .build()
            );
            
            // 统计找到的其他状态内容
            Map<String, Long> crossStatusCounts = crossReferences.stream()
                .collect(Collectors.groupingBy(
                    doc -> extractStatus((String) doc.getMetadata().get("filename")),
                    Collectors.counting()
                ));
            
            log.info("从{}状态内容找到的关联分布: {}", sourceStatus, crossStatusCounts);
            
            // 显示跨状态的关联内容
            crossReferences.stream()
                .filter(doc -> !extractStatus((String) doc.getMetadata().get("filename")).equals(sourceStatus))
                .limit(2)
                .forEach(doc -> {
                    String targetStatus = extractStatus((String) doc.getMetadata().get("filename"));
                    log.info("关联到{}状态: {}", 
                        targetStatus,
                        doc.getText().substring(0, Math.min(120, doc.getText().length())) + "...");
                });
        });
    }

    /**
     * 从文件名提取状态信息
     */
    private String extractStatus(String filename) {
        if (filename == null) return "未知";
        
        if (filename.contains("单身篇")) return "单身";
        if (filename.contains("恋爱篇")) return "恋爱";
        if (filename.contains("已婚篇")) return "已婚";
        
        return "其他";
    }

    /**
     * 综合测试：模拟完整的恋爱咨询场景
     */
    @Test
    void testCompleteConsultationScenario() {
        log.info("=== 综合测试：模拟完整的恋爱咨询场景 ===");
        
        // 模拟一个用户从单身到已婚的完整咨询过程
        List<String> consultationFlow = Arrays.asList(
            "我是单身，想要找到合适的伴侣，应该怎么做？",
            "我正在恋爱，但经常和对方吵架，如何改善？",
            "我们准备结婚了，如何为婚姻做好准备？",
            "我们已经结婚，但感觉感情变淡了，怎么办？"
        );
        
        consultationFlow.forEach(question -> {
            log.info("\n咨询问题: {}", question);
            
            List<Document> advice = pgVectorVectorStore.similaritySearch(
                SearchRequest.builder()
                    .query(question)
                    .topK(2)
                    .similarityThreshold(0.4)
                    .build()
            );
            
            if (!advice.isEmpty()) {
                Document bestAdvice = advice.get(0);
                String advisorStatus = extractStatus((String) bestAdvice.getMetadata().get("filename"));
                
                log.info("推荐建议来源: {}状态文档", advisorStatus);
                log.info("具体建议: {}", 
                    bestAdvice.getText().substring(0, Math.min(200, bestAdvice.getText().length())) + "...");
            } else {
                log.warn("未找到相关建议");
            }
        });
        
        log.info("\n=== 综合测试完成，成功模拟了完整的恋爱咨询流程 ===");
    }
}