package memory_virtualization.paging;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class OperatingSystemTest {
	static OperatingSystem os = new OperatingSystem(new PagedMemoryManager(), 4);

	@BeforeAll
	public static void setUp() {
		os.initialize();
	}

	@Test
	void getLeastRecentlyUsedEntryFromPCB() {
		var res = os.getLeastRecentlyUsedEntryFromPCB();
		int pfn = res.getPFN();
		assertEquals(5, pfn);
	}

	@Test
	void addProcess() {
	}

	// 페이지 교체에 대한 테스트 작성하기 -> 올바른가?
}
