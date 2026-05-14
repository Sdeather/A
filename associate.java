import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * GitHub 活跃度自动刷分工具
 * 原理：每天定时向指定仓库提交一个带有时间戳的空文件，触发 GitHub 的活跃度记录。
 * 
 * 使用前请确保：
 * 1. 本地已安装 Git 并配置好环境变量
 * 2. 目标仓库已经 clone 到本地，且配置好免密推送（SSH 或 Credential Helper）
 */
public class GitHubActivityBooster {

    // ================= 配置区域 =================
    // 本地仓库的绝对路径
    private static final String REPO_PATH = "/Users/yourname/Documents/your-github-repo";
    // 提交信息的模板
    private static final String COMMIT_MESSAGE_PREFIX = "chore: auto commit to boost activity - ";
    // 每天打卡的时间（24小时制，例如每天凌晨 00:01 打卡）
    private static final int TARGET_HOUR = 0;
    private static final int TARGET_MINUTE = 1;
    // ===========================================

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        GitHubActivityBooster booster = new GitHubActivityBooster();
        System.out.println("🚀 GitHub 活跃度自动打卡机已启动...");
        System.out.println("🎯 目标仓库: " + REPO_PATH);
        System.out.println("⏰ 每日打卡时间: " + TARGET_HOUR + ":" + String.format("%02d", TARGET_MINUTE));
        
        booster.scheduleDailyTask();
    }

    /**
     * 使用 ScheduledExecutorService 计算并安排每日定时任务
     */
    public void scheduleDailyTask() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // 计算距离下一次目标时间还有多久
        Runnable taskWrapper = () -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime nextRun = now.withHour(TARGET_HOUR).withMinute(TARGET_MINUTE).withSecond(0);
            if (now.compareTo(nextRun) > 0) {
                nextRun = nextRun.plusDays(1);
            }
            long delay = java.time.Duration.between(now, nextRun).toMillis();
            
            // 安排执行
            scheduler.schedule(() -> {
                performGitCommit();
                // 执行完一次后，继续安排下一次（每24小时循环）
                scheduleDailyTask(); 
            }, delay, TimeUnit.MILLISECONDS);
            
            System.out.println("⏳ 下一次打卡时间预定为: " + nextRun.format(timeFormatter));
        };

        taskWrapper.run();
    }

    /**
     * 执行核心的 Git 提交操作
     */
    private void performGitCommit() {
        String todayStr = LocalDate.now().format(dateFormatter);
        String fileName = "activity_" + todayStr + ".md";
        File targetFile = new File(REPO_PATH, fileName);

        // 如果今天已经打过卡了，就跳过
        if (targetFile.exists()) {
            System.out.println("✅ 今天 (" + todayStr + ") 已经打过卡了，无需重复提交。");
            return;
        }

        System.out.println("🔨 正在生成今日活跃度文件: " + fileName);
        
        // 1. 创建一个带内容的 Markdown 文件（GitHub 不记录完全空的文件提交）
        try (FileWriter writer = new FileWriter(targetFile)) {
            writer.write("# Daily Activity Log\n");
            writer.write("Auto generated commit on: " + LocalDateTime.now().format(timeFormatter) + "\n");
            writer.write("Keep the contribution graph green! 🟩");
        } catch (IOException e) {
            System.err.println("❌ 文件创建失败: " + e.getMessage());
            return;
        }

        // 2. 依次执行 Git 命令
        executeGitCommand("git add " + fileName);
        executeGitCommand("git commit -m \"" + COMMIT_MESSAGE_PREFIX + todayStr + "\"");
        executeGitCommand("git push");
        
        System.out.println("🎉 今日活跃度打卡成功！GitHub 小绿点 +1");
    }

    /**
     * 调用系统命令行执行 Git 指令
     */
    private void executeGitCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            // 根据操作系统适配命令执行器
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                processBuilder.command("cmd.exe", "/c", "cd /d " + REPO_PATH + " && " + command);
            } else {
                processBuilder.command("bash", "-c", "cd " + REPO_PATH + " && " + command);
            }
            
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            // 读取命令执行的输出（可选，调试用）
            // new Thread(() -> {
            //     try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            //         String line;
            //         while ((line = reader.readLine()) != null) {
            //             System.out.println("[Git Output] " + line);
            //         }
            //     } catch (IOException e) { e.printStackTrace(); }
            // }).start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.prin
