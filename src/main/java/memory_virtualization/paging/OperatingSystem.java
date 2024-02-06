package memory_virtualization.paging;

import java.util.*;

public class OperatingSystem {
    private int currentPid = -1;
    private int nextPid = 1;
    private Map<Integer, PCBentry> pcb = new HashMap<>();

    private MainMemory memory;

    private StringBuilder log = new StringBuilder();

    private int pageSize;
    private int time;

    public OperatingSystem(MainMemory memory, int pageSize) {
        this.memory = memory;
        this.pageSize = pageSize;
    }

    // 할당
    public boolean addProcess(int pid, List<Integer> pageLocationStatus, List<Integer> pageSizes, int currentTime) {
        PageTable pageTable = new PageTable();
        int pageTableSize = pageLocationStatus.size();

        // 차례대로 할당하는데, 자리가 없다면
        for (int i = 0; i < pageTableSize; i++){
            if (pageLocationStatus.get(i) != 0){
                Optional<Integer> maybePFN = memory.findNextLocation();

                if (maybePFN.isPresent()) {
                    int pfn = maybePFN.get();
                    pageTable.add(pfn, true, currentTime, pageSizes.get(i));
                    memory.assign(pfn, pid, i + 1);
                } else {
                    // PFN이 존재하지 않는 경우 처리
                    // swapping 알고리즘 구현
                }


            } else { // secondary store에 존재
                pageTable.add(-1, false, -1, pageSizes.get(i));
            }
        }

        PCBentry entry = new PCBentry(pageTable);
        pcb.put(pid, entry);

        return true;
    }


    // TODO: 조회 구현

    public void swap(){
        PageTableEntry victim = getLeastRecentlyUsedEntryFromPCB();



        // 해당 present bit를 -1로 만들기
    }

    public PageTableEntry getLeastRecentlyUsedEntryFromPCB() {
        PageTableEntry leastRecentlyUsedEntry = null;
        int leastUsedTime = Integer.MAX_VALUE;

        for (PCBentry pcbEntry : pcb.values()) {
            PageTable pageTable = pcbEntry.getPageTable();
            for (PageTableEntry entry : pageTable.getTable()) {
                if (entry.isPresent && entry.getUsedTime() < leastUsedTime) {
                    leastRecentlyUsedEntry = entry;
                    leastUsedTime = entry.getUsedTime();
                }
            }
        }

        return leastRecentlyUsedEntry;
    }

    // 초기상태!
    public void initialize() {
        // 프로세스 1
        initEachProcess(1, new ArrayList<>(Arrays.asList(1, 2, -1, -1, 3, -1, 4, 5)), new ArrayList<>(Arrays.asList(1, 1, 0, 0, 1, 0, 1, 1)),
                new ArrayList<>(Arrays.asList(4, 4, 4, 4, 4, 4, 4, 2)), new ArrayList<>(Arrays.asList(4, 3, -1, -1, 4, -1, 4, 1)));
        // 프로세스 2
        initEachProcess(2, new ArrayList<>(Arrays.asList(6, 7, -1)), new ArrayList<>(Arrays.asList(1, 1, 0)),
                new ArrayList<>(Arrays.asList(4, 4, 1)), new ArrayList<>(Arrays.asList(2, 3, -1)));
        // 프로세스 3
        initEachProcess(3, new ArrayList<>(Arrays.asList(8, 9, -1, 10, -1, 11, 12)), new ArrayList<>(Arrays.asList(1, 1, 0, 1, 0, 1, 1)),
                new ArrayList<>(Arrays.asList(4, 4, 4, 4, 4, 4, 2)), new ArrayList<>(Arrays.asList(3, 4, -1, 3, -1, 4, 3)));
        // 프로세스 4
        initEachProcess(4, new ArrayList<>(Arrays.asList(13, 14, 15, -1, -1, 16)), new ArrayList<>(Arrays.asList(1, 1, 1, 0, 0, 1)),
                new ArrayList<>(Arrays.asList(4, 4, 4, 4, 4, 1)), new ArrayList<>(Arrays.asList(4, 4, 4, -1, -1, 4)));


        time = 4;
        System.out.println(memory.getMemoryStatus());
    }

    private void initEachProcess(int pid, List<Integer> memoryStatus, List<Integer> pageLocationStatus,
                                 List<Integer> pageSizes, List<Integer> lastUsedTimes) {
        PageTable pageTable = new PageTable();

        int pageTableSize = pageLocationStatus.size();

        for (int i = 0; i < pageTableSize; i++) {
            if (pageLocationStatus.get(i) != 0) {
                int physicalAddress = memoryStatus.get(i);
                pageTable.add(physicalAddress, true, lastUsedTimes.get(i), pageSizes.get(i));
                memory.assign(physicalAddress, pid, i + 1);
            } else { // secondary store에 존재하는 경우
                pageTable.add(memoryStatus.get(i), false, -1, pageSizes.get(i));
            }
        }

        PCBentry entry = new PCBentry(pageTable);
        pcb.put(pid, entry);
    }


    PageTable getCurrentPageTable() {
        if (isNoProcess()) {
            log.append("현재 존재하는 프로세스가 없음'\n");
        }

        return pcb.get(currentPid).getPageTable();
    }

    boolean isNoProcess() {
        return currentPid == -1;
    }
}
