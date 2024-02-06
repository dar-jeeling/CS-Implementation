package memory_virtualization.segmentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatingSystem {
	// 현재 사용중인 프로세스 정보, -1인 경우 프로세스 없음
	int currentPid;

	// 현재 실행중인 프로세스의 세그먼트 테이블
	List<SegmentationInfo> segmentTable;

	// 메모리에 있는 processs 정보들 (pcb)
	Map<Integer, List<SegmentationInfo>> pcb;

	int[] memoryStatus;
	int[] segmentStatus;

	public OperatingSystem() {
		// 초기 메모리 상태
		currentPid = -1;
		segmentTable = new ArrayList<>();
		pcb = new HashMap<>();
		memoryStatus = new int[64];
		Arrays.fill(memoryStatus, -1);

		segmentStatus = new int[64];
		Arrays.fill(segmentStatus, -1);

	}

	public void init() {
		currentPid = 0;

		for (int i = 19; i <= 27; i++) {
			memoryStatus[i] = 0;
		}

		for (int i = 46; i <= 50; i++) {
			memoryStatus[i] = 0;
		}

		for (int i = 60; i <= 63; i++) {
			memoryStatus[i] = 0;
		}
	}

	public boolean addProcess(Process process) { // 실패 시 false 반환
		int stackSize = process.stackSize;
		int codeSize = process.codeSize;
		int heapSize = process.heapSize;
		int pid = process.pid;

		List<SegmentationInfo> segmentTable = new ArrayList<>();

		int[] temp = new int[64];
		int[] segTemp = new int[64];

		for (int i = 0; i < 64; i++) {
			temp[i] = memoryStatus[i];
			segTemp[i] = segmentStatus[i];
		}

		int[] codeIndex = allocateMemory(codeSize, temp, pid, 0, segTemp);

		if (codeIndex[0] == -1 && codeIndex[1] == -1) {
			return false;
		}

		int[] stackIndex = allocateMemory(stackSize, temp, pid, 1, segTemp);

		if (stackIndex[0] == -1 && stackIndex[1] == -1) {
			return false;
		}

		int[] heapIndex = allocateMemory(heapSize, temp, pid, 2, segTemp);

		if (heapIndex[0] == -1 && heapIndex[1] == -1) {
			return false;
		}

		segmentTable.add(new SegmentationInfo(0, codeIndex[0], codeIndex[1]));
		segmentTable.add(new SegmentationInfo(1, stackIndex[0], stackIndex[1]));
		segmentTable.add(new SegmentationInfo(2, heapIndex[0], heapIndex[1]));

		this.segmentTable = segmentTable;
		this.memoryStatus = temp;
		this.segmentStatus = segTemp;
		pcb.put(pid, segmentTable);

		return true;
	}

	public int[] allocateMemory(int size, int[] temp, int pid, int segId, int[] segTemp) {
		// 들어갈 공간을 찾는다.
		System.out.println("segID " + segId + "할당 시도");
		int[] hole = findMemoryHole(size, temp);

		if (hole[0] == -1 && hole[1] == -1) { // 할당 실패
			System.out.println("프로세스 " + pid + " 메모리 할당 실패");
			return hole;
		}

		for (int i = hole[0]; i <= hole[1]; i++) {
			temp[i] = pid;
			segTemp[i] = segId;
		}

		return hole;
	}

	public int[] findMemoryHole(int size, int[] temp) {
		int bestStart = -1;
		int bestEnd = -1;
		int bestSize = Integer.MAX_VALUE; // 최소 메모리 블록 크기를 추적하기 위한 변수

		// 현재 메모리 상태 확인
		System.out.println("현재 메모리");
		System.out.println(Arrays.toString(temp));

		int currentStart = -1;

		// best fit
		for (int i = 0; i < temp.length; i++) {
			if (temp[i] == -1) { // unallocated 공간 발견
				if (currentStart == -1) {
					currentStart = i; // 연속된 공간의 시작점 기록
				}

				if (i == temp.length - 1 || temp[i + 1] != -1) {
					// 연속된 공간의 끝점 찾기
					int currentSize = i - currentStart + 1;
					if (currentSize >= size && currentSize < bestSize) {
						// 현재 공간이 요청된 크기 이상이고, 이전에 찾은 공간보다 작다면 업데이트
						bestSize = currentSize;
						bestStart = currentStart;
						bestEnd = i;
					}
					currentStart = -1; // 다음 연속된 공간을 위해 리셋
				}
			}
		}

		System.out.println(bestStart + ", " + bestEnd + ", size:" + bestSize);
		return new int[] {bestStart, bestEnd};
	}

	public void contextSwitch(int pid) { // pid 번 프로세스로 컨텍스트 스위칭
		this.currentPid = pid;
		this.segmentTable = pcb.get(pid);
	}

	public boolean removeProcess(int pid) {
		if (!pcb.containsKey(pid)) {
			return false;
		}

		for (int i = 0; i < 64; i++) {
			if (memoryStatus[i] == pid) {
				memoryStatus[i] = -1;
				segmentStatus[i] = -1;
			}
		}

		pcb.remove(pid);
		return true;
	}

	// TODO: 이거 주소공간에 어떤 세그먼트인지 표시되어야 함
	public boolean accessAddress(int address, int cmd) {
		// 우선은 순차적으로 속하나 안속하나판단하는 걸로 함
		for (int i = 0; i < 3; i++) {
			if (segmentTable.get(i).base <= address && address <= segmentTable.get(i).limit) {
				if (cmd == 0) { // read
					return true;
				} else if (cmd == 1) { // write
					if (i == 0) {
						System.out.println("쓰기 권한 없음");
						return false;
					}
				} else if (cmd == 2) { // execute
					if (i != 0) {
						System.out.println("실행 권한 없음");
						return false;
					}
				}
				return true;
			}
		}

		System.out.println("Segmentation Fault!");
		return false;
	}
}
