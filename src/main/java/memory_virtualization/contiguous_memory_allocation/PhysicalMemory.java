package memory_virtualization.contiguous_memory_allocation;

import java.util.Arrays;

public class PhysicalMemory {
	static final public int SLOT_SIZE = 1; // 한 슬롯의 용량
	static final public int SLOT_COUNTS = 64; // 슬롯 갯수
	static final public int TOTAL_SIZE = SLOT_COUNTS * SLOT_SIZE;
	static final int UNALLOCATED = -1;
	public int[] slotStatuses; // 메모리 할당 상태
	public int emptySpace; // 현재 비어있는 공간의 크기

	public PhysicalMemory() {
		slotStatuses = new int[SLOT_COUNTS];
		emptySpace = SLOT_COUNTS * SLOT_SIZE;
		Arrays.fill(slotStatuses, UNALLOCATED);
	}

	public void allocMemory(int st, int en, int pid) {
		for (int i = st; i <= en; i++) {
			slotStatuses[i] = pid;
		}

		emptySpace -= ((en - st + 1) * SLOT_SIZE);
	}

	public void freeMemory(int st, int en) {
		if (st == -1 || en == -1) { // 메모리가 할당되지 않은 경우
			return;
		}

		for (int i = st; i <= en; i++) {
			slotStatuses[i] = UNALLOCATED;
		}

		emptySpace += ((en - st + 1) * SLOT_SIZE);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		int previousPid = -1; //a 이전 슬롯의 프로세스 ID 초기화

		builder.append("|");

		for (int i = 0; i < SLOT_COUNTS; i++) {
			int currentPid = slotStatuses[i];

			// 프로세스 ID가 바뀌면 띄어쓰기로 그룹을 구분
			if (i > 0 && currentPid != previousPid) {
				builder.append("|");
			}

			if (currentPid != UNALLOCATED) {
				// builder.append("█"); // 할당된 슬롯
				builder.append(currentPid);
			} else {
				builder.append("░"); // 빈 슬롯
			}

			previousPid = currentPid; // 이전 슬롯의 프로세스 ID 업데이트
		}

		builder.append("|");

		builder.append(" (emptySpace: " + emptySpace + ")");

		return builder.toString();
	}
}
