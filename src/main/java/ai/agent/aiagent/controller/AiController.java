package ai.agent.aiagent.controller;

import ai.agent.aiagent.agent.Manus;
import ai.agent.aiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 姚东名
 * Date: 2025-06-03
 * Time: 11:42
 */
@RequestMapping("/ai")
@RestController
public class AiController {
    @Resource
    private LoveApp loveApp;

    @Resource
    private ToolCallback[] allTools;

    @Resource
    private ChatModel dashscopeChatModel;

    /**
     * 同步调用AI恋爱应用
     */
    @GetMapping(value = "/love_app/chat/sync")
    public String doChatWithLoveAppSync(String message, String chatId) {
        return loveApp.doChat(message, chatId);
    }

    /**
     * SSE 流式调用AI恋爱应用
     */
    @GetMapping(value = "/love_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) { // text_event_stream_value
        return loveApp.doChatByStream(message, chatId);
    }

    /**
     * SSE 流式调用 AI 恋爱应用方式二
     */
    @GetMapping("/love_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithLoveAppServerSentEvent(String message, String chatId) {
        return loveApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 流式调用 AI 恋爱应用方式三
     */
    @GetMapping("/love_app/chat/sse_emitter")
    public SseEmitter doChatWithLoveAppSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        // 服务器发送时间发送器
        SseEmitter sseEmitter = new SseEmitter(180000L); // 3分钟
        // 获取 Flux 响应式数据流并且直接通过订阅推送给 SseEmitter
        loveApp.doChatByStream(message, chatId)
                .subscribe(
                        // 触发数据流的执行，并将每个数据块实时推送给客户端
                        chunk -> {
                            try {
                                sseEmitter.send(chunk); // 处理每个数据块（onNext）
                            } catch (IOException e) {
                                sseEmitter.completeWithError(e);
                            }
                        },
                        sseEmitter::completeWithError, // 处理错误（onError）
                        sseEmitter::complete // 处理完成（onComplete）
                );
        return sseEmitter;
    }

    /**
     * 流式调用 AI 智能体 Manus
     */
    @GetMapping("/manus/chat")
    public SseEmitter doChatWithManus(String message) {
        Manus manus = new Manus(allTools, dashscopeChatModel);
        return manus.runStream(message);
    }

}
