package ai.agent.aiagent.demo.invoke;

import ai.agent.aiagent.config.ApiKeyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyExample {

    private final ApiKeyConfig apiKeyConfig;
    
    @Autowired
    public ApiKeyExample(ApiKeyConfig apiKeyConfig) {
        this.apiKeyConfig = apiKeyConfig;
    }
    
    public void doSomethingWithApiKey() {
        // 使用从配置中获取的API Key
        String apiKey = apiKeyConfig.getDashscopeApiKey();
        System.out.println("Using API Key from config: " + apiKey);
        
        // 业务逻辑...
    }
} 