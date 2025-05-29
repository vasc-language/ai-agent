package ai.agent.aiagent.rag;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

/**
 * 工厂类 LoveAppCustomAdvisorFactory 根据用户查询需求生成对应的 Advisor
 * 创建一个和配置一个特定的Advisor对象
 */
@Slf4j
public class LoveAppRagCustomAdvisorFactory {
    public static Advisor createLoveAppRagCustomAdvisor(VectorStore vectorStore, String status) {
        // 过滤特定状态的文档
        Filter.Expression expression = new FilterExpressionBuilder()
                .eq("status", status)
                .build();
        // 创建文档检索器
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(expression) // 过滤表达式
                .similarityThreshold(0.5) // 相似度阈值
                .topK(5) // 返回文档数量
                .build();
        /**
         * 检索增强生成的统一管理器：将RAG流程中每个组件组合在一起，提供完整的检索增强对话能力
         * 文档检索继承-绑定 DocumentRetriever，从向量数据与库等数据源检索相关文档
         * 查询处理-支持查询转换器（QueryTransformer）对用户查询进行重写、翻译等预处理
         * 上下文增强-支持查询增强器（QueryAumenter）,将检索到的文档内容整合到用户查询中
         * 模块化架构-提供可插拔的RAG组件架构，支持自定义各个处理环节
         */
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }
}
