package memory_virtualization.allocation_strategy;

import java.util.List;
import java.util.Optional;

import memory_virtualization.contiguous_memory_allocation.MemoryFragment;

// 요청된 크기보다 크거나 같은 빈 공간 중 가장 작은 것을 찾아 할당
public class BestFit implements MemoryAllocationStrategy {
	@Override
	public Optional<MemoryFragment> allocate(List<MemoryFragment> emptySpaces, int size) {
		MemoryFragment best = null;

		for (MemoryFragment space : emptySpaces) {
			if (space.size >= size && (best == null || space.size < best.size)) {
				best = space;
			}
		}

		if (best != null) {
			emptySpaces.remove(best);
		}

		return Optional.ofNullable(best);
	}
}
