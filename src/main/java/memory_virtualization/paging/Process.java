package memory_virtualization.paging;

import java.util.List;

public class Process {
	int pid;
	int totalSize;
	List<Integer> pageLocationStatus;
	List<Integer> pageSize;

	public Process(int pid, int totalSize, List<Integer> pageLocationStatus, List<Integer> pageSize) {
		this.pid = pid;
		this.totalSize = totalSize;
		this.pageLocationStatus = pageLocationStatus;
		this.pageSize = pageSize;
	}
}
