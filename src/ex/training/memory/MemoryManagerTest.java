package ex.training.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemoryManagerTest {

    private MemoryManager memoryManager;

    @BeforeEach
    public void setUp() {
        memoryManager = new MemoryManager(1000);
    }

    @Test
    public void mallocTest() {
        var first = memoryManager.malloc(50);
        var second = memoryManager.malloc(150);
        var third = memoryManager.malloc(300);
        var four = memoryManager.malloc(7001);

        assertEquals(first, 0); // successfully allocated
        assertEquals(second, 50);
        assertEquals(third, 200);
        assertEquals(four, -1); // malloc size greater than free memory
        assertEquals(1, memoryManager.getFreeSegments().size());
        assertEquals(4, memoryManager.getSegments().size());
    }

    @Test
    public void freeTest() {
        var startIndex = memoryManager.malloc(500);
        memoryManager.free(250);

        assertEquals(1,memoryManager.getSegments().size());
        assertEquals(1, memoryManager.getFreeSegments().size());
    }

}