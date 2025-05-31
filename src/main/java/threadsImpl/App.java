package threadsImpl;

import lombok.extern.slf4j.Slf4j;

import java.text.DecimalFormat;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class App {

    private static final int TASK_COUNT = 500;

    public static void main(String[] args) {
        MultiQueueExecutor executor = new MultiQueueExecutor(
                20, 40, 25, 5, TimeUnit.SECONDS, 4
        );

        int rejected = 0;
        long startTime = System.currentTimeMillis();

        for (int i = 1; i <= TASK_COUNT; i++) {
            final int taskId = i;
            try {
                executor.execute(() -> {
                    log.info("Задача #{} начата", taskId);
                    try {
                        Thread.sleep(100); // имитация работы
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    log.info("Задача #{} завершена", taskId);
                });
            } catch (RejectedExecutionException e) {
                log.warn("Задача #{} отклонена", taskId);
                rejected++;
            }
        }

        long endTime = System.currentTimeMillis();
        executor.shutdown();

        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logStats(startTime, endTime, rejected);
    }

    private static void logStats(long startTime, long endTime, int rejectedTasks) {
        long totalTime = endTime - startTime;
        int executedTasks = TASK_COUNT - rejectedTasks;
        double avgTime = executedTasks > 0 ? (double) totalTime / executedTasks : 0.0;
        DecimalFormat df = new DecimalFormat("#.##");
        log.info("============================================================");
        log.info("Результаты выполнения");
        log.info("Общее время: {} мс", totalTime);
        log.info("Выполнено задач: {}", executedTasks);
        log.info("Отклонено задач: {}", rejectedTasks);
        log.info("Среднее время на задачу: {} мс", df.format(avgTime));
        log.info("============================================================");
    }
}

