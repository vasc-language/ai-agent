package ai.agent.aiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 姚东名
 * Date: 2025-05-29
 * Time: 18:20
 */

@SpringBootTest
class FileOperationToolTest {

    @Test
    void readFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "编程导航.txt";
        String result = fileOperationTool.readFile(fileName);
        Assertions.assertNotNull(result);
    }

    @Test
    void writeFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "编程导航.txt";
        String content = "https://www.codefather.cn 程序员编程学习交流社区";
        String result = fileOperationTool.writeFile(fileName, content);
        Assertions.assertNotNull(result);
    }
}