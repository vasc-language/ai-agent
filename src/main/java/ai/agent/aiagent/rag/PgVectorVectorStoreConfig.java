package ai.agent.aiagent.rag;


import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;


import java.util.List;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 姚东名
 * Date: 2025-05-25
 * Time: 16:10
 */
//@Configuration
public class PgVectorVectorStoreConfig {
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;
    @Bean("pgVectorVectorStore")
    public VectorStore pgVectorVectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel dashscopeEmbeddingModel) {
        // 配置向量数据库信息
        VectorStore vectorStore = PgVectorStore.builder(jdbcTemplate, dashscopeEmbeddingModel)
                .dimensions(1536)                    // Optional: defaults to model dimensions or 1536（默认为模型维度或1536）
                .distanceType(COSINE_DISTANCE)       // Optional: defaults to COSINE_DISTANCE（默认为余弦距离）
                .indexType(HNSW)                     // Optional: defaults to HNSW（默认为 HNSW 索引）
                .initializeSchema(true)              // Optional: defaults to false（是否自动初始化数据库模式，默认为 false）
                .schemaName("public")                // Optional: defaults to "public"（向量库模式名，默认为 "public"）
                .vectorTableName("vector_store")     // Optional: defaults to "vector_store"（向量表名，默认为 "vector_store"）
                .maxDocumentBatchSize(10000)         // Optional: defaults to 10000（最大文档批处理量，默认为10000）
                .build();

        // 加载文档
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        vectorStore.add(documents);
        return vectorStore;
    }
}

