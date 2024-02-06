package memory_virtualization.paging;

import java.util.ArrayList;
import java.util.List;

public class PageTable {
	List<PageTableEntry> table = new ArrayList<>();
	int totalSize = 0; // 페이지 테이블의 총 크기

	public List<PageTableEntry> getTable() {
		return table;
	}

	public int getEntrySize() {
		return table.size();
	}

	public void add(int pageFrameNumber, boolean isPresent, int currentTime, int size) {
		PageTableEntry entry = new PageTableEntry(pageFrameNumber, isPresent, currentTime, size);
		table.add(entry);
		totalSize++;
		// System.out.println("페이지 테이블에 저장 완료");
	}

	// 해당 페이지 엔트리를 조회함
	public void use(int vpn, int currentTime) {
		table.get(vpn).usedTime = currentTime;
		System.out.println("페이지 조회");
	}

	@Override
	public String toString() {
		return table.toString();
	}
}
