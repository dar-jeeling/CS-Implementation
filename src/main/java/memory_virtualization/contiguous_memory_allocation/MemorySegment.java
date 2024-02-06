package memory_virtualization.contiguous_memory_allocation;

public class MemorySegment {
	private int startAddress;
	private int size;

	public MemorySegment(int startAddress, int size) {
		this.startAddress = startAddress;
		this.size = size;
	}

	public int getStartAddress() {
		return startAddress;
	}

	public void setStartAddress(int startAddress) {
		this.startAddress = startAddress;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
}
