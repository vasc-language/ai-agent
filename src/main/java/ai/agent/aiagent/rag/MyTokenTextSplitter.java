package ai.agent.aiagent.rag;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 姚东名
 * Date: 2025-05-28
 * Time: 15:48
 */
@Component
public class MyTokenTextSplitter {
    public List<Document> splitterDocument(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(documents);
    }
    /**
     * 自定义token分词器
     */
    public List<Document> splitterCustomized(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter(
                200, // 单个文本块的目标大小
                100, // 文本块之间的重叠 token数量
                10, // 最小块阈值
                5000, // 最大块阈值
                true // 是否启动智能切分模式
        );
        return splitter.apply(documents);
    }
}
