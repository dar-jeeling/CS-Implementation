package memory_virtualization.paging;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OperatingSystemTest {
    static OperatingSystem os = new OperatingSystem(new PagedMemoryManager(), 4);

    @BeforeAll
    public static void setUp(){
        os.initialize();
    }

    @Test
    void test(){
        System.out.println("yes");
    }

    // 페이지 교체에 대한 테스트 작성하기 -> 올바른가?


}