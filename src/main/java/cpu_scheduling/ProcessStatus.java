package cpu_scheduling;

public enum ProcessStatus {
    READY,
    RUNNING, // CPU를 할당받고 Scheduled 된 상태
    BLOCKED, // IO나 Event를 기다리는 상태
    DONE, // 작업이 완료된 경우 (편의를 위함)
}
