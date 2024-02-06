package memory_virtualization.segmentation;

public class MemorySegment {
	public int base;
	public int limit;

	public MemorySegment(int base, int limit) {
		this.base = base;
		this.limit = limit;
	}
}
