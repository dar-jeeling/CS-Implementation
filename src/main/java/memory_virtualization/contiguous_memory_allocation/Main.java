package memory_virtualization.contiguous_memory_allocation;

import memory_virtualization.allocation_strategy.ALLOC_OPTION;

public class Main {
	public static void main(String[] args) {
		StringBuilder res1 = new StringBuilder();
		StringBuilder res2 = new StringBuilder();

		OperatingSystem firstFit = new OperatingSystem(ALLOC_OPTION.FIRST);
		res1.append("First Fit :\t");
		res2.append("First Fit :\t");

		test1(firstFit, res1);
		test2(firstFit, res2);

		OperatingSystem bestFit = new OperatingSystem(ALLOC_OPTION.BEST);
		res1.append("Best Fit :\t");
		res2.append("Best Fit :\t");

		test1(bestFit, res1);
		test2(bestFit, res2);
		// bestFit.showSimulationResult();

		OperatingSystem worstFit = new OperatingSystem(ALLOC_OPTION.WORST);
		res1.append("Worst Fit :\t");
		res2.append("Worst Fit :\t");

		test1(worstFit, res1);
		test2(worstFit, res2);

		System.out.println("======= 최종 결과 출력 =======");
		System.out.println(res1);
		System.out.println(res2);
	}

	// best-fit 만 메모리에 전부 올릴 수 있는 상황
	public static void test1(OperatingSystem os, StringBuilder sb) {
		os.initOperatingSystem();

		os.addAndAllocateProcess(1, 15);
		os.addAndAllocateProcess(2, 33);
		os.addAndAllocateProcess(3, 8);

		os.removeProcess(1);
		os.addAndAllocateProcess(4, 11);

		os.removeProcess(2);
		os.addAndAllocateProcess(5, 7);

		os.addAndAllocateProcess(6, 31);

		sb.append(os.physicalMemoryStatus.toString());
		sb.append("\n");

		// os.showSimulationResult();
	}

	// 다 안되는 상황
	public static void test2(OperatingSystem os, StringBuilder sb) {
		os.initOperatingSystem();

		os.addAndAllocateProcess(1, 15);
		os.addAndAllocateProcess(2, 8);
		os.addAndAllocateProcess(3, 26);

		os.removeProcess(1);
		os.addAndAllocateProcess(4, 13);

		os.removeProcess(2);
		os.addAndAllocateProcess(5, 9);

		os.removeProcess(3);
		os.addAndAllocateProcess(6, 31);

		os.removeProcess(4);

		os.addAndAllocateProcess(7, 10);

		os.addAndAllocateProcess(8, 14);

		sb.append(os.physicalMemoryStatus.toString());

		sb.append("\n");
		// os.showSimulationResult();
	}
}


