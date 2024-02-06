package memory_virtualization;

public interface MemoryManagementUnit {
	void allocMemory(int start, int end, int pid);

	void freeMemory(int start, int end);
}
