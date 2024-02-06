package memory_virtualization.contiguous_memory_allocation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import memory_virtualization.allocation_strategy.ALLOC_OPTION;
import memory_virtualization.allocation_strategy.AllocationStrategyFactory;
import memory_virtualization.allocation_strategy.MemoryAllocationStrategy;

// 이게 OS 클래스

public class OperatingSystem {
	// 현재 실행 중인 프로세스에 관련한 정보
	int baseRegister;
	int boundRegister;
	int pid; // 현재 실행 중인 프로세스의 pid

	// 메모리에 올라와 있는 프로세스들을 관리하는 pcb
	Map<Integer, PCBRow> PCB;

	// 빈 공간의 정보를 저장하고 있는 리스트
	List<MemoryFragment> emptySpaces;
	ALLOC_OPTION opt;

	PhysicalMemory physicalMemoryStatus;

	StringBuilder simulationResult;
	private MemoryAllocationStrategy allocationStrategy;

	public OperatingSystem(ALLOC_OPTION opt) {
		// 맨 처음에는 아무것도 없다고 가정
		this.opt = opt;
		initOperatingSystem();
		setAllocationStrategy(opt);
	}

	public void initOperatingSystem() {
		PCB = new HashMap();
		emptySpaces = new LinkedList<>();
		MemoryFragment init = new MemoryFragment(0, PhysicalMemory.SLOT_COUNTS - 1, PhysicalMemory.TOTAL_SIZE);
		emptySpaces.add(init);
		this.physicalMemoryStatus = new PhysicalMemory();
		simulationResult = new StringBuilder();
	}

	private void setAllocationStrategy(ALLOC_OPTION opt) {
		this.allocationStrategy = AllocationStrategyFactory.getStrategy(opt);
	}

	// 새로운 프로세스를 등록하고, 메모리를 할당한다.
	// 여기가 프로세스 할당하는 부분인데
	public void addAndAllocateProcess(int pid, int size) {
		Process newProcess = new Process(pid, size, 0);
		addProcess(newProcess);
		simulationResult.append("Process " + pid + " added with size " + size).append(" ");
		simulationResult.append(physicalMemoryStatus).append("\n");
	}

	// 여기는 프로세스 추가하는 부분인데,
	public void addProcess(Process process) {
		// 메모리를 할당하고, 프로세스에 대한 정보들을 얻는다.
		int[] address = allocateMemory(process.addressSpace, process.pid);

		// 추가할 수 없는 경우
		if (address[0] == -1 && address[1] == -1) {
			simulationResult.append("메모리가 부족하여 프로세스 " + process.pid + "를 할당하지 못했습니다.\n");
			return;
		}

		// PCB에 해당 프로세스를 추가한다
		PCB.put(process.pid, new PCBRow(address[0], address[1], process.addressSpace.getTotalSize(),
			process.addressSpace.getVirtualAddress()));
	}

	public int[] allocateMemory(AddressSpace addressSpace, int pid) {
		int goal = addressSpace.getTotalSize(); // 필요한 총 공간
		Optional<MemoryFragment> hole = findSuitableMemoryFragment(goal); // 여기는 빈 공간 찾는 부분이에요

		return hole.map(h -> allocateMemoryFragment(h, goal, pid))
			.orElse(new int[] {-1, -1});
	}

	// 메모리 조각 찾기
	private Optional<MemoryFragment> findSuitableMemoryFragment(int size) {
		return allocationStrategy.allocate(emptySpaces, size);
	}

	// 메모리 할당
	private int[] allocateMemoryFragment(MemoryFragment fragment, int goal, int pid) {
		int st = fragment.st; // 우선 시작 주소는 그대로고
		int en = calculateEndIndex(fragment, goal); // 마지막 주소같은 경우는
		physicalMemoryStatus.allocMemory(st, en, pid);
		scanPhysicalMemory();
		return new int[] {st, en};
	}

	// 할당할 끝 인덱스 계산
	private int calculateEndIndex(MemoryFragment fragment, int goal) {
		int holeSize = 0;
		int end = fragment.st;

		for (int i = fragment.st; i <= fragment.en; i++) {
			holeSize += PhysicalMemory.SLOT_SIZE;
			if (holeSize >= goal) {
				end = i;
				break;
			}
		}
		return end;
	}

	// 프로세스가 종료되고 사용하고 있던 메모리를 해제한다.
	public void removeProcess(int pid) {
		deleteProcess(pid);
		simulationResult.append("Process " + pid + " removed").append(" ");
		simulationResult.append(physicalMemoryStatus).append("\n");

	}

	public void deleteProcess(int pid) {
		if (!PCB.containsKey(pid))
			return;

		PCBRow processInfo = PCB.get(pid); // 아이디 찾아서

		int base = processInfo.base;
		int bound = processInfo.bound;
		physicalMemoryStatus.freeMemory(base, bound);

		PCB.remove(pid);
		scanPhysicalMemory();
	}

	// 현재 사용하는 프로세스를 전환한다.
	public void contextSwitch(int pid) {
		PCBRow processInfo = PCB.get(pid);
		baseRegister = processInfo.base;
		boundRegister = processInfo.bound;
		this.pid = pid;
	}

	// physical memory의 상태를 확인하고, empty space info 를 업데이트

	public void scanPhysicalMemory() {
		emptySpaces.clear(); // 기존 빈 공간 정보를 초기화

		int start = -1; // 빈 공간의 시작 인덱스
		for (int i = 0; i < PhysicalMemory.SLOT_COUNTS; i++) {
			if (physicalMemoryStatus.slotStatuses[i] == -1) { // 빈 슬롯 발견
				if (start == -1) {
					start = i; // 빈 공간의 시작 인덱스 설정
				}
			} else {
				if (start != -1) {
					// 연속적인 빈 공간의 끝을 발견함
					addEmptySpace(start, i - 1);
					start = -1; // 다음 빈 공간을 찾기 위해 리셋
				}
			}
		}

		// 마지막 연속적인 빈 공간 처리
		if (start != -1) {
			addEmptySpace(start, PhysicalMemory.SLOT_COUNTS - 1);
		}
	}

	private void addEmptySpace(int start, int end) {
		int size = (end - start + 1) * PhysicalMemory.SLOT_SIZE;
		MemoryFragment newFragment = new MemoryFragment(start, end, size);
		emptySpaces.add(newFragment);
	}

	// 가상 -> 물리 메모리 주소 변환을 수행
	public int translateVA(int pid) {
		return PCB.get(pid).base + PCB.get(pid).virtualAddress;
	}

	public void showSimulationResult() {
		System.out.println(simulationResult);
	}

}
