package ai.agent.aiagent.advisor;

import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.Map;

/**
 * 自定义 Re2 Advisor
 * 格式如下：
 * {Input_Query}
 * Read the question again: {Input_Query}
 */
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private AdvisedRequest before(AdvisedRequest advisedRequest) {

        Map<String, Object> advisedUserParams = new HashMap<>(advisedRequest.userParams());
        advisedUserParams.put("re2_input_query", advisedRequest.userText());

        return AdvisedRequest.from(advisedRequest)
                // 用户文本内容
                .userText("""
                        {re2_input_query}
                        Read the question again: {re2_input_query}
                        """)
                // 用于存储模板变量和其他配置参数
                .userParams(advisedUserParams)
                .build();
    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
        // 对于更复杂处理流式场景，使用 Reactor 的操作符
        /*return Mono.just(advisedRequest)
                .publishOn(Schedulers.boundedElastic())
                .map(request -> {
                    // 请求前处理逻辑
                    return modifyRequest(request);
                })
                .flatMapMany(request -> chain.nextAroundStream(request))
                .map(response -> {
                    // 响应处理逻辑
                    return modifyResponse(response);
                });*/
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}

