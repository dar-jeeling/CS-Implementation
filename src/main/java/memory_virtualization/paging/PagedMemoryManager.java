package memory_virtualization.paging;

import java.util.Optional;

public class PagedMemoryManager implements MainMemory {
	private static final int UNUSED = 0;
	int[] frames = new int[17]; // 1-indexed
	int totalSize = 64; // TODO: FIX MAGIC NUMBER
	int[] tempPageNumber = new int[17]; // 확인을 위한 값
	int totalUsedPageCount = 0;

	public int getTotalUsedFrameCount() {
		return totalUsedPageCount;
	}

	public int getEmptyFrameCount() {
		return 16 - totalUsedPageCount;
	}
	
	public void assign(int physicalAddress, int pid, int pageNumber) {
		frames[physicalAddress] = pid;
		tempPageNumber[physicalAddress] = pageNumber;
		totalUsedPageCount++;
	}

	@Override
	public void release(int physicalAddress) {
		frames[physicalAddress] = UNUSED;
		tempPageNumber[physicalAddress] = -1;
		totalUsedPageCount--;
	}

	@Override
	public String getMemoryStatus() {
		StringBuilder sb = new StringBuilder();

		for (int i = 1; i <= 16; i++) {
			if (frames[i] == UNUSED) {
				sb.append(i).append(": ").append("EMPTY").append('\n');
			} else {
				sb.append(i).append(": ").append(frames[i]).append("-").append(tempPageNumber[i]).append('\n');
			}
		}

		return sb.toString();
	}

	public Optional<Integer> findNextLocation() {
		for (int i = 1; i <= 16; i++) {
			if (frames[i] == UNUSED) {
				return Optional.of(i);
			}
		}

		return Optional.empty();
	}
}
