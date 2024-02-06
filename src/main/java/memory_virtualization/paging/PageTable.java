package memory_virtualization.paging;

import java.util.List;

public class PageTable {
    public List<PageTableEntry> getTable() {
        return table;
    }

    List<PageTableEntry> table;
    int totalSize; // 페이지 테이블의 총 크기

    public int getEntrySize() {
        return table.size();
    }

    public void add(int pageFrameNumber, boolean isPresent, int currentTime, int size) {
        PageTableEntry entry = new PageTableEntry(pageFrameNumber, isPresent, currentTime, size);
        System.out.println("페이지 테이블에 저장 완료");
    }

    // 해당 페이지 엔트리를 조회함
    public void use(int vpn, int currentTime) {
        table.get(vpn).usedTime = currentTime;
        System.out.println("페이지 조회");
    }
}
