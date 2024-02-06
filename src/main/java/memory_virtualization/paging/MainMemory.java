package memory_virtualization.paging;

import java.util.Optional;

public interface MainMemory {
    int getTotalSize();
    int getInternalFragmentSize();
    int getExternalFragmentSize();

    void assign(int physicalAddress, int pid, int pageNumber);

    boolean release(int physicalAddress);

    String getMemoryStatus();

    Optional<Integer> findNextLocation();
}
