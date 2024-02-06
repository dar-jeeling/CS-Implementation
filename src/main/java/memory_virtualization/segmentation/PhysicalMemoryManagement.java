package memory_virtualization.segmentation;

import java.util.Arrays;

import memory_virtualization.contiguous_memory_allocation.PhysicalMemory;

public class PhysicalMemoryManagement extends PhysicalMemory {
	int[] segmentStatus;

	public PhysicalMemoryManagement() {
		super();
		segmentStatus = new int[super.SLOT_COUNTS];
		Arrays.fill(segmentStatus, -1);
	}
}
