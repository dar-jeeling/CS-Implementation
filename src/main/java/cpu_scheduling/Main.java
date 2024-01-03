package cpu_scheduling;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        MLFQScheduler scheduler = new MLFQScheduler(3, 20); // 3개의 큐와 20의 부스트 시간으로 설정

        List<Process> processes = Arrays.asList(
                new Process(0, 20, Arrays.asList(5, 10)), // 프로세스 1
                new Process(0, 15, Arrays.asList(7, 14))  // 프로세스 2
        );

        processes.stream().forEach(scheduler::addProcess);
        scheduler.schedule();

        // 스케줄 결과 확인
        System.out.println("---------- 스케줄링 결과 확인 ----------");

        processes.stream().forEach(Process::printResult);
    }
}
