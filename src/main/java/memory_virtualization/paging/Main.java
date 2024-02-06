package memory_virtualization.paging;

import java.util.ArrayList;
import java.util.Arrays;

// 프로세스가 추가되면 모든 페이지들을 바로 조회한다고 가정
public class Main {
	static OperatingSystem os = new OperatingSystem(new PagedMemoryManager(), 4);

	public static void main(String[] args) {
		os.initialize();

		System.out.println("----------- t = 5 일 떄 --------------");

		os.readAddress(2, 2, 5);
		os.readAddress(3, 4, 5);
		os.readAddress(3, 7, 5);
		os.readAddress(4, 2, 5);
		os.readAddress(4, 3, 5);

		Process p5 = new Process(5, 17, new ArrayList<>(Arrays.asList(1, 1, 0, 1, 1)),
			new ArrayList<>(Arrays.asList(4, 4, 4, 4, 1)));

		os.addProcess(p5.pid, p5.pageLocationStatus, p5.pageSize, 5);
		os.showMemoryStatus();

		// 결과
		// 1: 1-1
		// 2: 5-1
		// 3: 1-5
		// 4: 1-7
		// 5: 5-2
		// 6: 5-4
		// 7: 2-2
		// 8: 5-5
		// 9: 3-2
		// 10: 3-4
		// 11: 3-6
		// 12: 3-7
		// 13: 4-1
		// 14: 4-2
		// 15: 4-3
		// 16: 4-6

		System.out.println("----------- t = 6 일 떄 --------------");

		os.readAddress(1, 5, 6);
		os.readAddress(1, 7, 6);
		os.readAddress(3, 2, 6);
		os.readAddress(4, 1, 6);

		Process p6 = new Process(6, 10, new ArrayList<>(Arrays.asList(1, 1, 1)),
			new ArrayList<>(Arrays.asList(4, 4, 2)));

		os.addProcess(p6.pid, p6.pageLocationStatus, p6.pageSize, 6);
		os.showMemoryStatus();

		// 결과
		// 1: 6-1
		// 2: 5-1
		// 3: 1-5
		// 4: 1-7
		// 5: 5-2
		// 6: 5-4
		// 7: 2-2
		// 8: 5-5
		// 9: 3-2
		// 10: 3-4
		// 11: 6-2
		// 12: 3-7
		// 13: 4-1
		// 14: 4-2
		// 15: 4-3
		// 16: 6-3

		// test case 2 : page fault에 의한 교체

		System.out.println("----------- t = 7 일 떄 --------------");

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

		os.showMemoryStatus();

		// 결과
		// 1: 6-1
		// 2: 5-1
		// 3: 5-3
		// 4: 3-5
		// 5: 4-5
		// 6: 5-4
		// 7: 3-1
		// 8: 2-1
		// 9: 3-2
		// 10: 3-4
		// 11: 6-2
		// 12: 3-7
		// 13: 4-1
		// 14: 4-6
		// 15: 4-3
		// 16: 6-3
	}
}
