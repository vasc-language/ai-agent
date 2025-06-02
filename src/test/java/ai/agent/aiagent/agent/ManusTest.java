package ai.agent.aiagent.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 姚东名
 * Date: 2025-06-02
 * Time: 9:15
 */
@SpringBootTest
public class ManusTest {
    @Resource
    private Manus manus;

    @Test
    public void run() {
        String userPrompt = """
                我需要为与另一半的约会制定一份详细计划，具体要求如下：

                **基本信息：**
                - 地点范围：上海静安区为中心，半径5公里内
                - 约会对象：我的另一半
                - 输出格式：PDF文档

                **详细需求：**
                1. **地点推荐**（请提供3-5个选项）：
                   - 包含具体地址、营业时间、人均消费
                   - 涵盖不同类型：浪漫餐厅、咖啡厅、文化场所、休闲娱乐等
                   - 考虑交通便利性和环境氛围

                2. **时间安排**：
                   - 提供半天、全天两种时长的方案
                   - 包含具体的时间节点和行程安排
                   - 考虑用餐时间和休息间隔

                3. **详细计划内容**：
                   - 每个地点的推荐理由和特色
                   - 预计停留时间和活动内容
                   - 交通路线和方式建议
                   - 天气备选方案

                4. **视觉呈现**：
                   - 搜集并整合相关地点的高质量图片
                   - 制作美观的PDF布局设计
                   - 包含地图导航信息

                5. **实用信息**：
                   - 总预算估算（分不同消费水平）
                   - 预订建议和联系方式
                   - 注意事项和小贴士

                请确保计划既浪漫温馨又实用可行，符合上海本地的实际情况。
                """;
        String answer = manus.run(userPrompt);
        Assertions.assertNotNull(answer);
    }
}
