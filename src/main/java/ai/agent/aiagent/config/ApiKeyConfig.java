package ai.agent.aiagent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyConfig {
    
    @Value("${AI_DASHSCOPE_API_KEY:}")
    private String dashscopeApiKey;
    
    public String getDashscopeApiKey() {
        return dashscopeApiKey;
    }
} 