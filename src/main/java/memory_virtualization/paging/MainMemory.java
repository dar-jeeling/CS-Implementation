package memory_virtualization.paging;

public interface MainMemory {
	void assign(int physicalAddress, int pid, int pageNumber);

	void release(int physicalAddress);

	String getMemoryStatus();
}
