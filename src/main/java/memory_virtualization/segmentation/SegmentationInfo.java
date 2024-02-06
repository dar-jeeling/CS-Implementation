package memory_virtualization.segmentation;

public class SegmentationInfo {
	int segId;
	int base;
	int limit;

	boolean isReadable; // 읽기 권한
	boolean isWritable; // 쓰기 권한
	boolean isExecutable; // 실행 권한

	boolean growPositive; // 낮은 주소에서 높은 주소로 자라는가?

	public SegmentationInfo(int segId, int base, int limit) {
		this.segId = segId;
		this.base = base;
		this.limit = limit;

		switch (segId) {
			case SegmentConstants.STACK -> {
				isReadable = true;
				isWritable = true;
				isExecutable = false;
				growPositive = false;
			}
			case SegmentConstants.HEAP -> {
				isReadable = true;
				isWritable = true;
				isExecutable = false;
				growPositive = true;
			}
			case SegmentConstants.CODE -> {
				isReadable = true;
				isWritable = false;
				isExecutable = true;
				growPositive = true;
			}
		}
	}

}
