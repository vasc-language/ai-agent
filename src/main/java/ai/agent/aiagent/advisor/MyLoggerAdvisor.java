package ai.agent.aiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/**
 * 自定义Advisor日志拦截器
 * 打印info日志级别，只输出单次用户提示词和AI回复的文本
 */
@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    /**
     * 处理请求（前置处理）
     */
    private AdvisedRequest before(AdvisedRequest request) {
        log.info("AI request: {}", request.userText());
        return request;
    }
    /**
     * 处理响应（后置处理）
     */
    private void observerAfter(AdvisedResponse response) {
        log.info("AI response: {}", response
                .response()
                .getResult()
                .getOutput()
                .getText());
    }
    /**
     * 处理非流式请求和相应
     */
    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        // 处理请求
        AdvisedRequest request = this.before(advisedRequest);
        AdvisedResponse response = chain.nextAroundCall(request);
        this.observerAfter(response);
        return response;
    }

    /**
     * 处理流式请求和响应
     */
    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        AdvisedRequest request = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponseFlux = chain.nextAroundStream(request);
        // 通过 MessageAggregator 工具类，将Flux响应聚合成单个AdvisedResponse
        // 对于日志记录观察整个响应而非流中各个独立项的处理非常有用
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponseFlux, this::observerAfter);
    }

    /**
     * 自定义Advisor的唯一标识
     */
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }


    @Override
    public int getOrder() {
        // 值越小，优先级越高
        return 0;
    }
}
