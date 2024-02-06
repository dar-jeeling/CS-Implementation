package memory_virtualization.paging;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OperatingSystemTest {
	static OperatingSystem os = new OperatingSystem(new PagedMemoryManager(), 4);
	// private PagedMemoryManager testMemoryManager;

	@BeforeEach
	public void setUp() {
		os.initialize();
	}

	@Test
	void getLeastRecentlyUsedEntryFromPCB() {
		var res = os.getLeastRecentlyUsedEntryFromPCB();
		int pfn = res.getPFN();
		assertEquals(5, pfn);
	}

	@Test
	public void testMemoryOperations() {
		PagedMemoryManager testMemoryManager = new PagedMemoryManager();

		// t = 5
		testMemoryManager.setFrames(new int[] {0, 1, 5, 1, 1, 5, 5, 2, 5, 3, 3, 3, 3, 4, 4, 4, 4});
		testMemoryManager.setTempPageNumber(new int[] {0, 1, 1, 5, 7, 2, 4, 2, 5, 2, 4, 6, 7, 1, 2, 3, 6});

		// TODO: 이거도 검증해야함...

		// assertAll("t = 5 reading operations",
		// 	() -> assertEquals()
		// );
		os.readAddress(2, 2, 5);
		os.readAddress(3, 4, 5);
		os.readAddress(3, 7, 5);
		os.readAddress(4, 2, 5);
		os.readAddress(4, 3, 5);

		Process p5 = new Process(5, 17, new ArrayList<>(Arrays.asList(1, 1, 0, 1, 1)),
			new ArrayList<>(Arrays.asList(4, 4, 4, 4, 1)));

		os.addProcess(p5.pid, p5.pageLocationStatus, p5.pageSize, 5);

		assertEquals(testMemoryManager.getMemoryStatus(), os.getMemoryStatus());

		// t = 6
		testMemoryManager.setFrames(new int[] {0, 6, 5, 1, 1, 5, 5, 2, 5, 3, 3, 6, 3, 4, 4, 4, 6});
		testMemoryManager.setTempPageNumber(new int[] {0, 1, 1, 5, 7, 2, 4, 2, 5, 2, 4, 2, 7, 1, 2, 3, 3});

		os.readAddress(1, 5, 6);
		os.readAddress(1, 7, 6);
		os.readAddress(3, 2, 6);
		os.readAddress(4, 1, 6);

		Process p6 = new Process(6, 10, new ArrayList<>(Arrays.asList(1, 1, 1)),
			new ArrayList<>(Arrays.asList(4, 4, 2)));

		os.addProcess(p6.pid, p6.pageLocationStatus, p6.pageSize, 6);
		assertEquals(testMemoryManager.getMemoryStatus(), os.getMemoryStatus());

		// t = 7
		testMemoryManager.setFrames(new int[] {0, 6, 5, 5, 3, 4, 5, 3, 2, 3, 3, 6, 3, 4, 4, 4, 6});
		testMemoryManager.setTempPageNumber(new int[] {0, 1, 1, 3, 5, 5, 4, 1, 1, 2, 4, 2, 7, 1, 6, 3, 3});

		os.readAddress(5, 1, 7);
		os.readAddress(5, 4, 7);
		os.readAddress(3, 4, 7);
		os.readAddress(3, 7, 7);
		os.readAddress(4, 3, 7);

		// 프로세스 1 종료
		os.deleteProcess(1);

		// TODO: 한 개씩 교체되는거랑 여러 개 교체되는 기준을 세워야 할 것 같다.
		// TODO: 우선 테스트 케이스 대로.. pageFault일어나면  6개 까지 한 번에 비움
		os.readAddress(5, 3, 7);
		os.readAddress(3, 5, 7);
		os.readAddress(4, 5, 7);
		os.readAddress(3, 1, 7);
		os.readAddress(2, 1, 7);
		os.readAddress(4, 6, 7);

		// os.showMemoryStatus();
		assertEquals(testMemoryManager.getMemoryStatus(), os.getMemoryStatus());

	}
}
