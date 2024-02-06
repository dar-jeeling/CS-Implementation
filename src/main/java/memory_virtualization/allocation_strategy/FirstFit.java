package memory_virtualization.allocation_strategy;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import memory_virtualization.contiguous_memory_allocation.MemoryFragment;

public class FirstFit implements MemoryAllocationStrategy {
	@Override
	public Optional<MemoryFragment> allocate(List<MemoryFragment> emptySpaces, int size) {
		Iterator<MemoryFragment> iterator = emptySpaces.iterator();

		while (iterator.hasNext()) {
			MemoryFragment space = iterator.next();
			if (space.size >= size) {
				iterator.remove();
				return Optional.of(space);
			}
		}

		return Optional.empty();
	}

}
