package memory_virtualization.paging;

public class PCBentry {
    PageTable pageTable;

    public PCBentry(PageTable pageTable) {
        this.pageTable = pageTable;
    }

    public PageTable getPageTable() {
        return pageTable;
    }
}
