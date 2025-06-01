package ai.agent.aiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 基于 AI 的文档元信息增强器
 */
<<<<<<< HEAD
@Component
=======
//@Component
>>>>>>> f265d1d9e25af6dc4736c36073e4d6b61f429059
public class MyKeywordEnricher {
    @Resource
    private ChatModel dashscopeChatModel;
    public List<Document> enricherDocument(List<Document> documents) {
        KeywordMetadataEnricher keywordMetadataEnricher = new KeywordMetadataEnricher(dashscopeChatModel, 5);
        return keywordMetadataEnricher.apply(documents);
    }
}
