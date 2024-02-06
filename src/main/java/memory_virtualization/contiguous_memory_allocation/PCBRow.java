package memory_virtualization.contiguous_memory_allocation;

public class PCBRow {
	// int pid;
	int base;
	int bound;
	int size; // 차지하고 있는 총 사이즈를 의미
	int virtualAddress; // 프로세스의 가상 주소 시작 위치

	public PCBRow(int base, int bound, int size, int virtualAddress) {
		// this.pid = pid;
		this.base = base;
		this.bound = bound;
		this.size = size;
		this.virtualAddress = virtualAddress;
	}
}

