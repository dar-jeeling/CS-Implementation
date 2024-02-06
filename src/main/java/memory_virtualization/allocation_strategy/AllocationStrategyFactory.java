package memory_virtualization.allocation_strategy;

public class AllocationStrategyFactory {
	public static MemoryAllocationStrategy getStrategy(ALLOC_OPTION opt) {
		return switch (opt) {
			case FIRST -> new FirstFit();
			case BEST -> new BestFit();
			case WORST -> new WorstFit();
		};
	}
}
