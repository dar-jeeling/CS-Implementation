package memory_virtualization.paging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatingSystem {
	// swap을 시작할 기준점 : 메모리 공간이 LW 이하가 되면 swap을 시작한다.
	private final int LOW_WATERMARK = 0;
	private int currentPid = -1;
	private Map<Integer, PCBentry> pcb = new HashMap<>();
	private PagedMemoryManager memory;
	private StringBuilder log = new StringBuilder();
	private int pageSize;
	private int time;
	// HighWaterMark

	public OperatingSystem(PagedMemoryManager memory, int pageSize) {
		this.memory = memory;
		this.pageSize = pageSize;
	}

	public void deleteProcess(int pid) {
		// 관련 pte 지우기
		List<PageTableEntry> currentTable = pcb.get(pid).getPageTable().table;

		// 메모리 할당해제하기
		for (PageTableEntry page : currentTable) {
			if (page.isPresent) {
				memory.release(page.getPFN());
			}
		}

		pcb.remove(pid);
	}

	// 할당
	public void addProcess(int pid, List<Integer> pageLocationStatus, List<Integer> pageSizes, int currentTime) {
		PageTable pageTable = new PageTable();
		int pageTableSize = pageLocationStatus.size();

		// 차례대로 할당하는데, 자리가 없다면
		for (int i = 0; i < pageTableSize; i++) {
			if (pageLocationStatus.get(i) != 0) {
				// swap
				if (memory.getEmptyFrameCount() <= LOW_WATERMARK) {
					// HighWaterMark의 경우, 정의한 값이 아니라 프로세스가 요구로하는 페이지 갯수로 가정한다.
					int HighWaterMark = pageLocationStatus.stream().mapToInt(Integer::intValue).sum();
					for (int cnt = 0; cnt < HighWaterMark; cnt++) {
						swapOut();
					}
				}

				int pfn = memory.findNextLocation()
					.orElseThrow(() -> new IllegalStateException("No unused PFN available"));

				memory.assign(pfn, pid, i + 1);
				pageTable.add(pfn, true, currentTime, pageSizes.get(i));

			} else { // secondary store에 존재
				pageTable.add(-1, false, currentTime, pageSizes.get(i));
			}
		}

		PCBentry entry = new PCBentry(pageTable);
		pcb.put(pid, entry);

		currentPid = pid;
	}

	// TODO: 조회 구현
	public int readAddress(int pid, int pageIndex, int currentTime) { // pid 의 주소공간의 pageIndex 번 조회
		System.out.println(pid + "-" + pageIndex);

		var currentTable = pcb.get(pid).getPageTable().table;
		System.out.println(currentTable);

		var currentTableEntry = currentTable.get(pageIndex - 1); // 인덱스 보정

		// pageFault
		if (!currentTableEntry.isPresent) {
			System.out.println("Page fault 가 발생");
			handlePageFault(pid, pageIndex - 1);
		}

		System.out.println("조회 발생, PFN: " + currentTableEntry.getPFN());
		currentTableEntry.usedTime = currentTime;

		return currentTableEntry.getPFN();
	}

	public int handlePageFault(int pid, int vpn) {
		if (memory.getEmptyFrameCount() <= LOW_WATERMARK) {
			int HighWaterMark = 4; // TODO: 좀 모호함

			for (int cnt = 0; cnt < HighWaterMark; cnt++) {
				swapOut();
			}
		}

		// 새로 주소를 할당 받은 후
		int newPFN = memory.findNextLocation().orElseThrow(() -> new IllegalStateException("No unused PFN available"));

		// 다시 어사인
		memory.assign(newPFN, pid, vpn + 1);

		// pte 업데이트
		var entry = pcb.get(pid).getPageTable().table.get(vpn);
		entry.isPresent = true;
		entry.PFN = newPFN;

		return newPFN;
	}

	public void swapOut() { // memory -> secondary store
		PageTableEntry victim = getLeastRecentlyUsedEntryFromPCB();
		victim.isPresent = false; // 디스크로 내림
		memory.release(victim.PFN);
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
		initEachProcess(1, new ArrayList<>(Arrays.asList(1, 2, -1, -1, 3, -1, 4, 5)),
			new ArrayList<>(Arrays.asList(1, 1, 0, 0, 1, 0, 1, 1)),
			new ArrayList<>(Arrays.asList(4, 4, 4, 4, 4, 4, 4, 2)),
			new ArrayList<>(Arrays.asList(4, 3, -1, -1, 4, -1, 4, 1)));
		// 프로세스 2
		initEachProcess(2, new ArrayList<>(Arrays.asList(6, 7, -1)), new ArrayList<>(Arrays.asList(1, 1, 0)),
			new ArrayList<>(Arrays.asList(4, 4, 1)), new ArrayList<>(Arrays.asList(2, 3, -1)));
		// 프로세스 3
		initEachProcess(3, new ArrayList<>(Arrays.asList(8, 9, -1, 10, -1, 11, 12)),
			new ArrayList<>(Arrays.asList(1, 1, 0, 1, 0, 1, 1)),
			new ArrayList<>(Arrays.asList(4, 4, 4, 4, 4, 4, 2)), new ArrayList<>(Arrays.asList(3, 4, -1, 3, -1, 4, 3)));
		// 프로세스 4
		initEachProcess(4, new ArrayList<>(Arrays.asList(13, 14, 15, -1, -1, 16)),
			new ArrayList<>(Arrays.asList(1, 1, 1, 0, 0, 1)),
			new ArrayList<>(Arrays.asList(4, 4, 4, 4, 4, 1)), new ArrayList<>(Arrays.asList(4, 4, 4, -1, -1, 4)));

		time = 4;
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

	void showMemoryStatus() {
		System.out.println(memory.getMemoryStatus());
	}
}
