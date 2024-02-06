package memory_virtualization.segmentation;

public class Process {
	public int pid;
	public int codeSize;
	public int stackSize;
	public int heapSize;

	public Process(int pid, int codeSize, int stackSize, int heapSize) {
		this.pid = pid;
		this.codeSize = codeSize;
		this.stackSize = stackSize;
		this.heapSize = heapSize;
	}
}
