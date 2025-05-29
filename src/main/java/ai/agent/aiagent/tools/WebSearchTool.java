package ai.agent.aiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网页搜索工具 - 基于SearchAPI的百度搜索
 */
public class WebSearchTool {

    private static final Logger logger = LoggerFactory.getLogger(WebSearchTool.class);
    
    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";
    
    // 默认配置
    private static final int DEFAULT_RESULT_COUNT = 5;
    private static final int HTTP_TIMEOUT = 10000; // 10秒超时

    private final String apiKey;
    private final boolean debugMode;
    private final int maxResults;

    public WebSearchTool(String apiKey) {
        this(apiKey, false, DEFAULT_RESULT_COUNT);
    }
    
    public WebSearchTool(String apiKey, boolean debugMode, int maxResults) {
        this.apiKey = apiKey;
        this.debugMode = debugMode;
        this.maxResults = maxResults;
    }

    @Tool(description = "从百度搜索引擎搜索信息")
    public String searchWeb(
            @ToolParam(description = "搜索关键词") String query) {
        
        if (query == null || query.trim().isEmpty()) {
            return "错误：搜索关键词不能为空";
        }
        
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query.trim());
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "baidu");
        paramMap.put("num", maxResults);
        
        try {
            logger.info("开始搜索：{}", query);
            
            // 设置超时时间
            String response = HttpUtil.createGet(SEARCH_API_URL)
                .form(paramMap)
                .timeout(HTTP_TIMEOUT)
                .execute()
                .body();
            
            if (debugMode) {
                logger.debug("API完整响应：{}", response);
            }
            
            // 检查响应是否为空
            if (response == null || response.trim().isEmpty()) {
                logger.error("API返回空响应");
                return "错误：搜索API返回空响应";
            }
            
            // 解析JSON响应
            JSONObject jsonObject = JSONUtil.parseObj(response);
            
            if (debugMode) {
                logger.debug("响应字段：{}", jsonObject.keySet());
            }
            
            // 检查API错误
            if (jsonObject.containsKey("error")) {
                String error = jsonObject.getStr("error");
                logger.error("API错误：{}", error);
                return "API错误：" + error;
            }
            
            // 记录搜索状态
            logSearchMetadata(jsonObject);
            
            // 优先处理有机搜索结果
            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            
            if (organicResults != null && !organicResults.isEmpty()) {
                logger.info("找到{}条搜索结果", organicResults.size());
                return processOrganicResults(organicResults);
            }
            
            // 处理备用搜索信息
            return processAlternativeResults(jsonObject, query);
            
        } catch (Exception e) {
            logger.error("搜索过程中发生错误：{}", e.getMessage(), e);
            return String.format("搜索错误：%s。请稍后重试或联系技术支持。", e.getMessage());
        }
    }
    
    /**
     * 记录搜索元数据
     */
    private void logSearchMetadata(JSONObject jsonObject) {
        if (jsonObject.containsKey("search_metadata")) {
            JSONObject metadata = jsonObject.getJSONObject("search_metadata");
            String status = metadata.getStr("status");
            String searchId = metadata.getStr("id");
            
            if (debugMode) {
                logger.debug("搜索状态：{}，搜索ID：{}", status, searchId);
            }
            
            if (!"Success".equalsIgnoreCase(status)) {
                logger.warn("搜索状态异常：{}", status);
            }
        }
    }
    
    /**
     * 处理有机搜索结果
     */
    private String processOrganicResults(JSONArray organicResults) {
        int endIndex = Math.min(organicResults.size(), maxResults);
        logger.info("处理{}条有机搜索结果", endIndex);
        
        List<Object> objects = organicResults.subList(0, endIndex);
        
        return objects.stream()
            .map(obj -> {
                JSONObject item = (JSONObject) obj;
                String title = item.getStr("title", "无标题");
                String link = item.getStr("link", "无链接");
                String snippet = item.getStr("snippet", "无摘要");
                
                return String.format("【标题】%s\n【链接】%s\n【摘要】%s", 
                    title, link, snippet);
            })
            .collect(Collectors.joining("\n" + "=".repeat(50) + "\n"));
    }
    
    /**
     * 处理备用搜索结果（相关搜索等）
     */
    private String processAlternativeResults(JSONObject jsonObject, String query) {
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("未找到直接搜索结果，但发现以下相关信息：\n\n");
        
        boolean hasContent = false;
        
        // 处理相关搜索
        if (jsonObject.containsKey("related_searches")) {
            JSONArray relatedSearches = jsonObject.getJSONArray("related_searches");
            if (relatedSearches != null && !relatedSearches.isEmpty()) {
                resultBuilder.append("【相关搜索建议】\n");
                int count = Math.min(relatedSearches.size(), 5);
                for (int i = 0; i < count; i++) {
                    JSONObject item = relatedSearches.getJSONObject(i);
                    String relatedQuery = item.getStr("query", "");
                    if (!relatedQuery.isEmpty()) {
                        resultBuilder.append("• ").append(relatedQuery).append("\n");
                    }
                }
                resultBuilder.append("\n");
                hasContent = true;
            }
        }
        
        // 处理热门搜索
        if (jsonObject.containsKey("top_searches")) {
            JSONArray topSearches = jsonObject.getJSONArray("top_searches");
            if (topSearches != null && !topSearches.isEmpty()) {
                resultBuilder.append("【热门搜索】\n");
                int count = Math.min(topSearches.size(), 3);
                for (int i = 0; i < count; i++) {
                    JSONObject item = topSearches.getJSONObject(i);
                    String topQuery = item.getStr("query", "");
                    if (!topQuery.isEmpty()) {
                        resultBuilder.append("• ").append(topQuery).append("\n");
                    }
                }
                resultBuilder.append("\n");
                hasContent = true;
            }
        }
        
        if (hasContent) {
            return resultBuilder.toString();
        }
        
        return String.format("很抱歉，没有找到相关的搜索结果");
    }
}
