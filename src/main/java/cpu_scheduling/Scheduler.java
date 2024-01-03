package cpu_scheduling;

public interface Scheduler {
    // 스케줄러에 프로세스를 추가한다.
    void addProcess(Process p);

    // 스케줄링을 실행한다.
    void schedule();

    // 스케줄링이 모두 완료되었는가?
    boolean isAllDone();
}
