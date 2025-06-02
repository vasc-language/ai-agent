# Manus 人机交互功能指南

## 概述

Manus AI智能体现在具备了完整的人机交互能力，能够在执行任务过程中与用户进行实时交互，获取用户输入、确认和选择。

## 功能特性

### 1. 基本问答（askQuestion）
- **功能**：向用户询问开放性问题并获取文本回答
- **适用场景**：需要获取用户意见、偏好、需求描述等信息
- **示例**：
  ```
  用户：我想找一个好地方旅游
  Manus：我需要了解您的偏好来为您推荐。请告诉我您喜欢什么类型的旅游？比如海滩、山区、城市还是乡村？
  ```

### 2. 多选题（askChoice）
- **功能**：向用户提供多个选项供选择
- **适用场景**：当有多个可选方案时，让用户做出选择
- **示例**：
  ```
  Manus：我为您找到几个餐厅选项：
  1. 意大利餐厅 - Pizza House
  2. 中餐厅 - 老北京
  3. 日料 - 寿司之家
  请选择您想了解哪一个？
  ```

### 3. 确认询问（askConfirmation）
- **功能**：请求用户确认或拒绝某项操作
- **适用场景**：执行重要操作前获取用户授权
- **示例**：
  ```
  Manus：我即将删除文件 'old_data.txt'，这个操作不可撤销。您确认要继续吗？
  ```

### 4. 数字输入（askNumber）
- **功能**：要求用户输入指定范围内的数字
- **适用场景**：需要数量、评分、排序等数值输入
- **示例**：
  ```
  Manus：请为这次服务打分（1-10分，10分为最满意）
  ```

### 5. 邮箱输入（askEmail）
- **功能**：要求用户输入有效的邮箱地址
- **适用场景**：需要联系信息、账户注册等
- **示例**：
  ```
  Manus：请提供您的邮箱地址，我将发送确认信息给您
  ```

## 技术架构

### 核心组件

1. **AskHumanTool** (`src/main/java/ai/agent/aiagent/tools/AskHumanTool.java`)
   - 合并了原HumanInteractionTool和AskHuman的功能
   - 既提供Spring AI工具接口，又包含完整的人机交互逻辑
   - 提供5个主要的交互方法，处理异常和错误情况
   - 支持输入验证、重试机制和异步交互

2. **ToolConfig** (`src/main/java/ai/agent/aiagent/config/ToolConfig.java`)
   - 统一管理所有工具的配置
   - 将AskHumanTool集成到Manus的工具集中

3. **Manus** (`src/main/java/ai/agent/aiagent/agent/Manus.java`)
   - 更新了系统提示，明确了人机交互能力
   - 智能决策何时使用交互工具

### 交互流程

```
用户请求 → Manus分析 → 判断是否需要交互 → 调用AskHumanTool → 用户输入 → 继续执行任务
```

## 使用方法

### 1. 启动应用
```bash
mvn spring-boot:run
```

### 2. 运行演示
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--demo=human-interaction"
```

### 3. 编程方式使用
```java
@Autowired
private Manus manus;

public void example() {
    String result = manus.run("请帮我制定一个旅行计划，但我需要你了解我的偏好");
    System.out.println(result);
}
```

## 最佳实践

### 1. 何时使用人机交互
- ✅ 需要澄清模糊需求时
- ✅ 执行重要操作前确认时  
- ✅ 需要用户偏好输入时
- ✅ 提供多个选项让用户选择时
- ❌ 能够通过上下文推断的情况
- ❌ 简单的信息查询任务

### 2. 交互设计原则
- **清晰性**：问题描述要清楚明确
- **必要性**：只在真正需要时才与用户交互
- **友好性**：使用礼貌和友好的语气
- **效率性**：尽量减少交互次数

### 3. 错误处理
- 设置合理的重试次数
- 提供清晰的错误提示
- 有备选方案处理交互失败

## 示例场景

### 场景1：餐厅推荐
```
用户：我想找个好餐厅
Manus：为了给您最好的推荐，我需要了解一些信息：
- 您想吃什么菜系？[中餐/西餐/日料/其他]
- 您的预算范围是多少？[经济型/中档/高档]
- 距离您现在位置多远是可以接受的？
```

### 场景2：文件整理
```
用户：帮我整理桌面文件
Manus：我发现您的桌面有120个文件。我建议：
- 创建文档、图片、软件三个文件夹
- 按类型自动分类文件
- 删除重复文件
您确认这样操作吗？
```

### 场景3：项目规划
```
用户：我想开始一个新项目
Manus：请选择项目类型：
1. Web应用开发
2. 移动应用开发  
3. 数据分析项目
4. 机器学习项目
5. 其他

然后我会为您生成相应的项目模板和开发计划。
```

## 故障排除

### 常见问题

1. **工具无法调用**
   - 检查ToolConfig是否正确注册
   - 确认AskHumanTool已标记为@Component

2. **用户输入无响应**
   - 检查控制台输入流是否被其他进程占用
   - 确认Scanner正常工作

3. **交互超时**
   - 调整AskHumanTool的超时设置
   - 检查异步交互配置

### 调试方法

1. 启用详细日志：
```properties
logging.level.ai.agent.aiagent.tools=DEBUG
logging.level.ai.agent.aiagent.agent=DEBUG
```

2. 测试单个工具：
```java
@Autowired
private AskHumanTool tool;

public void test() {
    String response = tool.askQuestion("这是一个测试问题");
    System.out.println("用户回答: " + response);
}
```

## 版本更新说明

### v2.0 更新内容
- 合并了原HumanInteractionTool和AskHuman两个类的功能
- 创建了新的AskHumanTool类，既有工具接口又有交互实现
- 简化了架构，减少了类之间的依赖关系
- 提升了代码的可维护性和可读性

## 总结

通过集成人机交互功能，Manus现在能够：
- 在任务执行过程中主动与用户沟通
- 根据用户输入调整执行策略
- 提供更个性化和精准的服务
- 处理复杂和模糊的用户需求

新的AskHumanTool类简化了架构设计，使得Manus从一个工具调用智能体升级为具备真正交互能力的AI助手。 