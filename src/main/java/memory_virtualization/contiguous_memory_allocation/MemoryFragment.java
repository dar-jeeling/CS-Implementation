package memory_virtualization.contiguous_memory_allocation;

import static memory_virtualization.contiguous_memory_allocation.PhysicalMemory.*;

public class MemoryFragment implements Comparable<MemoryFragment> {
	public int size; // 총 크기
	public int st; // 현재 메모리 조각의 시작 슬롯 번호
	public int en; // 현재 메모리 조각의 끝 슬롯 번호
	public int pid;

	public MemoryFragment(int st, int en, int pid) {
		this.st = st;
		this.en = en;
		this.pid = pid;
		this.size = SLOT_SIZE * (en - st + 1);
	}

	@Override
	public int compareTo(MemoryFragment other) {
		return Integer.compare(this.st, other.st);
	}

}
