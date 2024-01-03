package cpu_scheduling;

import java.util.*;

public class MLFQScheduler implements Scheduler {
    // 큐는 인덱스가 작으면 우선순위가 높다고 가정한다.
    private int queueCount; // 사용할 큐의 갯수
    private List<Queue<Process>> queues; // 가지고 있는 큐들
    private List<Integer> timeSlice; // timeSlice[i] := i번 큐의 time slice
    private Map<Integer, Integer> processCpuTime;
    private int boostTime; // 우선 순위 boost에 대한 시간 주기

    public MLFQScheduler(int queueCount, int boostTime) {
        this.queueCount = queueCount;
        this.boostTime = boostTime;

        this.queues = new ArrayList<>(queueCount);
        this.timeSlice = new ArrayList<>(queueCount);

        for (int i = 0; i < queueCount; i++) {
            queues.add(new LinkedList<>());

            // 각 큐의 타임 슬라이스 조정 TODO: 사용자에게 값 받도록 변경하기
            // 우선 전부 같은 값으로 설정한다.
            timeSlice.add(10);
        }

        this.processCpuTime = new HashMap<>();
    }

    @Override
    public void addProcess(Process p) {
        // 프로세스를 스케줄러에 추가 -> 우선순위 0번에 배정함
        this.queues.getFirst().offer(p);
        this.processCpuTime.put(p.getPid(), 0);
    }

    @Override
    public void schedule() {
        int currentTime = 0; // 총 수행 시간
        int lastBoostTime = 0;

        while (!isAllDone()) {
            // 우선 순위가 높은 큐 부터, 큐 내부의 작업을 round-robin 방식으로 작동시킨다.
            for (int priority = 0; priority < queueCount; priority++) {
                Queue<Process> currentQueue = queues.get(priority);

                // 해당 큐가 빌 때 까지 라운드 로빈으로 실행
                while (!currentQueue.isEmpty()) {
                    System.out.println(priority + "번 큐 : " + currentQueue);

                    Process currentProcess = currentQueue.peek();
                    int pid = currentProcess.getPid();

                    // 현재 프로세스에 대한 타임 슬라이스
                    int remainTimeSlice = timeSlice.get(priority) - processCpuTime.get(pid);

                    // time-slice 동안 process 해당 프로세스를 실행하기 (1초씩)
                    currentProcess.increaseExecutionCount();
                    System.out.println("time: " +  currentTime + " / " + pid + "번 프로세스가 실행됩니다.");
                    
                    while (remainTimeSlice > 0) {
                        // interrupt 가다리는 시간은 고려하지 않는다.
                        if (currentProcess.getStatus() == ProcessStatus.BLOCKED) {
                            currentProcess.setStatus(ProcessStatus.RUNNING);
                        }

                        currentTime++;
                        // System.out.println("time: " +  currentTime + " / " + pid + "번 프로세스가 실행됩니다.");
                        currentProcess.execute(1, currentTime);
                        remainTimeSlice--;

                        // 인터럽트 걸렸을 경우
                        if (currentProcess.getStatus() == ProcessStatus.BLOCKED) {
                            processCpuTime.put(pid, processCpuTime.get(pid) + 1);
                            processPriority(priority); // 우선순위 조정 후 큐에 넣는다.
                            break;
                        }

                        // 끝났을 경우, 큐에서 그 프로세스를 빼고 break;
                        if (currentProcess.getStatus() == ProcessStatus.DONE) {
                            // 바로 나온다.
                            currentQueue.poll();
                            break;
                        }

                        processCpuTime.put(pid, processCpuTime.get(pid) + 1);
                    }

                    if (currentProcess.getStatus() == ProcessStatus.RUNNING) {
                        processPriority(priority);
                        currentProcess.setStatus(ProcessStatus.READY);
                    }

                    // boost 작업
                    lastBoostTime = performPriorityBoost(currentTime, lastBoostTime);
                }
            }
        }

        System.out.println("스케줄 완료, 총 걸린 시간: " + currentTime);
    }

    @Override
    public boolean isAllDone() {
        return queues.stream().allMatch(Queue::isEmpty);
    }

    public void processPriority(int priority){ // 해당 작업의 우선순위를 조정한다.
        Queue<Process> currentQueue = queues.get(priority);
        Process currentProcess = queues.get(priority).poll();
        int pid = currentProcess.getPid();

        // 우선순위 조정 작업
        if (isInteractiveJob(pid, priority)) { // interactive-job : 우선 순위 유지
            currentQueue.offer(currentProcess);
        } else { // cpu-burst : 우선순위 강등
            int nextPriority = Math.min(priority + 1, queueCount - 1);
            queues.get(nextPriority).offer(currentProcess);
            processCpuTime.put(pid, 0); // 우선순위 변경 시 CPU 시간 초기화
        }
    }

    public boolean isInteractiveJob(int pid, int queueId) {
        int accumulatedTime = this.processCpuTime.get(pid);
        return accumulatedTime < this.timeSlice.get(queueId);
    }

    // 모든 작업의 priority를 boost한다.
    public int performPriorityBoost(int currentTime, int lastBoostTime) {
        if (currentTime - lastBoostTime >= boostTime) {
            System.out.println("priority boost");
            boost();
            return currentTime;
        }
        return lastBoostTime;
    }

    public void boost() {
        // 0번 큐는 제외하고 나머지 큐의 작업을 최상위 큐(0번 큐)로 이동
        if (queueCount == 0) return;

        for (int i = 1; i < queueCount; i++) {
            Queue<Process> currentQueue = queues.get(i);

            while (!currentQueue.isEmpty()) {
                queues.getFirst().offer(currentQueue.poll());
            }
        }
    }
}
