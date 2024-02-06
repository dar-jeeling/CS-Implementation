package memory_virtualization.segmentation;

public class Main {
	public static void main(String[] args) {
		OperatingSystem os = new OperatingSystem();
		os.init();

		// process1
		os.addProcess(new Process(1, 10, 12, 18));
		os.removeProcess(0);
		os.addProcess(new Process(2, 6, 5, 7));
		os.addProcess(new Process(3, 1, 2, 3));
		os.addProcess(new Process(4, 2, 2, 2));

		// 메모리 접근
		os.contextSwitch(1);
		os.accessAddress(49, 0);
		os.accessAddress(64, 0);

		os.contextSwitch(2);
		os.accessAddress(64, 0);

		os.contextSwitch(4);
		os.accessAddress(47, 1);
		os.accessAddress(47, 2);
	}
}
