package cpu_scheduling;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;

public class Process {
    private static final AtomicInteger idGenerator = new AtomicInteger();
    private int pid; // Process ID
    private int arrivalTime; // 프로세스가 시스템에 도착한 시간

    private int responseTime; // 프로세스가 처음 수행될 때 걸리는 시간
    private int turnaroundTime; // 프로세스가 완료 될 때까지 걸리는 총 시간
    private int completionTime; // 프로세스가 완료된 시간

    private int cpuTime; // 프로세스가 요구하는 총 CPU 시간
    private int remainingCpuTime; // 남은 CPU 시간
    private ProcessStatus status;
    private boolean isExecutedFirst; // 처음으로 시작된 프로세스 인가?
    private int exectuedCount; // 몇 번 실행되었는가?
    private List<Integer> interruptTime; // 인터럽트 걸리는 시점

    public Process(int arrivalTime, int cpuTime, List<Integer> interruptTime) {
        this.pid = idGenerator.incrementAndGet();
        this.arrivalTime = arrivalTime;
        this.cpuTime = cpuTime;
        this.interruptTime = interruptTime;

        this.isExecutedFirst = true;
        this.remainingCpuTime = cpuTime;
        this.status = ProcessStatus.READY;
        this.exectuedCount = 0;
    }

    public int getPid() {
        return pid;
    }

    public ProcessStatus getStatus() {
        return status;
    }

    // 프로세스를 time 만큼 실행시킨다. (CPU 작업) NOTE: 우선 1초씩 실행시킨다고 가정되있다!
    public void execute(int time, int currentTime) {
        // 실행이 완료된 경우 그냥 리턴
        if (status == ProcessStatus.DONE) return;

        // 처음으로 시작되었나?
        if (isExecutedFirst) {
            responseTime = currentTime - arrivalTime - 1;
            isExecutedFirst = false;
        }

        this.remainingCpuTime -= time;

        // 인터럽트 걸린 시간인 경우
        if (interruptTime.contains(currentTime)) {
            status = ProcessStatus.BLOCKED;
            return;
        }

        status = ProcessStatus.RUNNING;

        // 실행이 완료되었을 때 처리
        if (remainingCpuTime <= 0) {
            remainingCpuTime = 0; // 값 보정
            completionTime = currentTime;
            turnaroundTime = completionTime - arrivalTime;
            status = ProcessStatus.DONE;
        } else {
            status = ProcessStatus.RUNNING;
        }
    }

    @Override
    public String toString() {
        return "(pid: " + pid + ", status: " + status + ", 남은 시간: " + remainingCpuTime + ")";
    }

    public void setStatus(ProcessStatus status) {
        this.status = status;
    }

    public int getExecutionCount(){
        return exectuedCount;
    }

    public void setExecutionCount(int cnt){
        exectuedCount = cnt;
    }

    public void increaseExecutionCount(){
        exectuedCount++;
    }
    public void printResult(){
        System.out.println("< Process " + pid + " 실행 결과 >");
        System.out.println("Turnaround time : " + turnaroundTime);
        System.out.println("Response time : " + responseTime);
        System.out.println("실행 횟수 : " + exectuedCount);
        System.out.println();
    }
}
