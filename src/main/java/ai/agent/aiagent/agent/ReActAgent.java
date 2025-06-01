package ai.agent.aiagent.agent;

import lombok.EqualsAndHashCode;

/**
 * ReActAgent 继承于 BaseAgent 并将 step 步骤拆成 think 和 act 两个抽象办法
 * 实现了思考-行动的循环模式
 */
@EqualsAndHashCode(callSuper = true)
public abstract class ReActAgent extends BaseAgent {
    /**
     * 处理当前状态是否需要执行下一步
     * @return true 表示执行下一步 false 则相反
     */
    public abstract boolean think();

    /**
     * 执行决定的行为
     * @return 行为执行结果
     */
    public abstract String act();

    /**
     * 执行单个步骤
     * @return
     */
    @Override
    public String step() {
        try {
            boolean shouldAct = think();
            if (!shouldAct) {
                return "思考完成-无需执行";
            }
            return act();
        } catch (Exception e) {
            // 记录异常日志
            e.printStackTrace();
            return "步骤执行失败" + e.getMessage();
        }
    }
}
