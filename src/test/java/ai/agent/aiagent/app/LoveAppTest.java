package ai.agent.aiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;


/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 姚东名
 * Date: 2025-05-24
 * Time: 20:33
 */
@SpringBootTest
class LoveAppTest {
    @Resource
    private LoveApp loveApp;

    /**
     * 测试多轮对话
     */
    @Test
    void doChat() {
        // 第一次对话
        String chatId = UUID.randomUUID().toString(); // 随机生成对话ID
        String message = "我是Join";
        String answer = loveApp.doChat(message, chatId);
        System.out.println("answer:" + answer);
        // 第二次对话
        String message2 = "我叫什么名字，刚刚和把你说过，帮我回忆一下？";
        String answer2 = loveApp.doChat(message2, chatId);
        System.out.println("answer2: " + answer2);

    }

    /**
     * 测试结构化输出
     */
    @Test
    void doChatWithReport() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好，我是Join，我想让另一半（女朋友）更爱我，但我不知道该怎么做";
        LoveApp.LoveReport loveReport = loveApp.doChatWithReport(message, chatId);
        System.out.println("loveReport: " + loveReport);
    }

    /**
     * 测试是否从向量数据库中查询
     */
    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "我已经结婚了，但是婚后关系不太亲密，怎么办？";
        String answer = loveApp.doChatWithRag(message, chatId);
        System.out.println("answer: " + answer);
    }
}