package ai.agent.imagesearchmcpserver.tools;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImageSearchTool {

    // 替换为你的 Pexels API 密钥（需从官网申请）
    private static final String API_KEY = "1ZFAdQHwVHQqpZHjT3Wy4nTEiDLh0roPE0d0cA9SAUXHLul48Yujj7xW";

    // Pexels 常规搜索接口（请以文档为准）
    private static final String API_URL = "https://api.pexels.com/v1/search";
    
    // HTTP请求超时配置（毫秒）
    private static final int HTTP_TIMEOUT = 30000; // 30秒超时

    @Tool(description = "search image from web")
    public String searchImage(@ToolParam(description = "Search query keyword") String query) {
        try {
            List<String> imageUrls = searchMediumImages(query);
            if (imageUrls.isEmpty()) {
                return "未找到相关图片，请尝试其他关键词";
            }
            return String.join(",", imageUrls);
        } catch (Exception e) {
            return "图片搜索出错: " + e.getMessage();
        }
    }

    /**
     * 搜索中等尺寸的图片列表
     */
    public List<String> searchMediumImages(String query) {
        try {
            // 设置请求头（包含API密钥）
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", API_KEY);

            // 设置请求参数（仅包含query，可根据文档补充page、per_page等参数）
            Map<String, Object> params = new HashMap<>();
            params.put("query", query);
            params.put("per_page", "10"); // 限制返回数量

            // 发送 GET 请求，增加超时配置
            String response = HttpUtil.createGet(API_URL)
                    .addHeaders(headers)
                    .form(params)
                    .timeout(HTTP_TIMEOUT) // 设置30秒超时
                    .execute()
                    .body();

            // 检查响应是否为空
            if (StrUtil.isBlank(response)) {
                throw new RuntimeException("API响应为空");
            }

            // 解析响应JSON
            JSONObject responseObj = JSONUtil.parseObj(response);
            if (!responseObj.containsKey("photos")) {
                throw new RuntimeException("API响应格式错误，缺少photos字段");
            }

            return responseObj.getJSONArray("photos")
                    .stream()
                    .map(photoObj -> (JSONObject) photoObj)
                    .map(photoObj -> photoObj.getJSONObject("src"))
                    .map(photo -> photo.getStr("medium"))
                    .filter(StrUtil::isNotBlank)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("搜索图片失败: " + e.getMessage(), e);
        }
    }
}

