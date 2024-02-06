package memory_virtualization.paging;

public class Process {
    private int pid;
    private int addressSpaceSize;

    public Process(int pid, int addressSpaceSize) {
        this.pid = pid;
        this.addressSpaceSize = addressSpaceSize;
    }
}
