package memory_virtualization.contiguous_memory_allocation;

// Process가 가지고 있는 가상 메모리 공간을 의미한다.
public class AddressSpace {
	private int totalSize; // 필요한 총 공간의 크기
	private MemorySegment codeSegment;
	private MemorySegment dataSegment;
	private MemorySegment stackSegment;
	private MemorySegment heapSegment;

	private int virtualAddress; // 가상 주소 시작 위치 (실제 물리 주소를 계산하기 위함)

	// 생성자
	public AddressSpace(int totalSize, int virtualAddress) {
		this.totalSize = totalSize;
		this.virtualAddress = virtualAddress;

		// 초기 세그먼트 설정
		this.codeSegment = new MemorySegment(0, 0); // 시작 주소와 크기
		this.dataSegment = new MemorySegment(0, 0);
		this.stackSegment = new MemorySegment(totalSize, 0); // 스택은 주로 높은 주소에서 시작
		this.heapSegment = new MemorySegment(0, 0);
	}

	public void setCodeSegment(MemorySegment codeSegment) {
		this.codeSegment = codeSegment;
	}

	public MemorySegment getDataSegment() {
		return dataSegment;
	}

	public MemorySegment getStackSegment() {
		return stackSegment;
	}

	public MemorySegment getHeapSegment() {
		return heapSegment;
	}

	public int getVirtualAddress() {
		return virtualAddress;
	}

	public void setVirtualAddress(int virtualAddress) {
		this.virtualAddress = virtualAddress;
	}

	public int getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
}
