package memory_virtualization.paging;

public class PageTableEntry {
    int PFN;
    boolean isPresent; // 이 페이지가 현제 메모리에 존재하는지
    int usedTime; // 해당 페이지의 마지막 사용시간
    int size; // 해당 페이지에서 차지하는 큭

    public PageTableEntry(int PFN, boolean isPresent, int usedTime, int size) {
        this.PFN = PFN;
        this.isPresent = isPresent;
        this.usedTime = usedTime;
        this.size = size;
    }

    public int getPFN() {
        return PFN;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public int getUsedTime() {
        return usedTime;
    }
}
