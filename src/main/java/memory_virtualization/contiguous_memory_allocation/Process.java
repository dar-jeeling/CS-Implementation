package memory_virtualization.contiguous_memory_allocation;

public class Process {
	int pid;
	AddressSpace addressSpace;

	public Process(int pid, int totalSize, int virtualAddress) {
		this.pid = pid;
		this.addressSpace = new AddressSpace(totalSize, virtualAddress);
	}

	public AddressSpace getAddressSpace() {
		return addressSpace;
	}
}
