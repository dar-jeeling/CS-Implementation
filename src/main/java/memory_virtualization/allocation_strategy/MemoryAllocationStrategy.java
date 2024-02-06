package memory_virtualization.allocation_strategy;

import java.util.List;
import java.util.Optional;

import memory_virtualization.contiguous_memory_allocation.MemoryFragment;

public interface MemoryAllocationStrategy {
	Optional<MemoryFragment> allocate(List<MemoryFragment> emptySpaces, int size);
}
