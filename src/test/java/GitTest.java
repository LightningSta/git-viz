import org.example.Config;
import org.example.GitDependencyVisualizer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class GitTest {
    @Test
    public void test() throws Exception {
        Config config = Config.loadConfig("C:\\Users\\nazar\\OneDrive\\Desktop\\study\\SecondConfig\\src\\main\\resources\\config.json");
        GitDependencyVisualizer visualizer = new GitDependencyVisualizer(config.repositoryPath, config.fileName);

        System.out.println("Получение коммитов...");
        List<Map<String, String>> commitData = visualizer.getCommitsForFile();

        if (commitData.isEmpty()) {
            System.out.println("Коммитов для файла " + config.fileName + " не найдено.");
            return;
        }

        System.out.println("Генерация PlantUML...");
        String umlContent = visualizer.generatePlantUML(commitData);
        assert(umlContent.contains("README"));

    }
    @Test
    public void test2() throws Exception {
        Config config = Config.loadConfig("C:\\Users\\nazar\\OneDrive\\Desktop\\study\\SecondConfig\\src\\main\\resources\\config.json");
        config.fileName="fsdfaf.dfads";
        GitDependencyVisualizer visualizer = new GitDependencyVisualizer(config.repositoryPath, config.fileName);

        System.out.println("Получение коммитов...");
        List<Map<String, String>> commitData = visualizer.getCommitsForFile();

        assert(commitData.isEmpty());

    }
    @Test
    public void test3() throws Exception {
        Config config = Config.loadConfig("C:\\Users\\nazar\\OneDrive\\Desktop\\study\\SecondConfig\\src\\main\\resources\\config.json");
        config.repositoryPath="fsdfaf.dfads";
        GitDependencyVisualizer visualizer = new GitDependencyVisualizer(config.repositoryPath, config.fileName);

        System.out.println("Получение коммитов...");
        List<Map<String, String>> commitData = visualizer.getCommitsForFile();

        assert(commitData.isEmpty());

    }
}
