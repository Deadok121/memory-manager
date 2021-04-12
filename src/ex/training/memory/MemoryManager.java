package ex.training.memory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Stack;
import lombok.Getter;

public class MemoryManager {
    @Getter
    private MemorySegment[] cells;
    @Getter
    private Stack<MemorySegment> freeSegments;
    @Getter
    private LinkedList<MemorySegment> segments;

    private final int n;

    public MemoryManager(int n) {
        cells = new MemorySegment[n];
        MemorySegment ms = new MemorySegment(n);
        segments = new LinkedList<>();
        freeSegments = new Stack<>();
        segments.add(ms);
        freeSegments.push(ms);
        this.n = n;
    }

    public int malloc(int mallocCount) {
        if (mallocCount > cells.length || mallocCount < 1) {
            return -1;
        }

        MemorySegment recentFree = freeSegments.peek();
        int freeLength = recentFree.length;
        if (freeLength < mallocCount) {
            return -1;
        }
        freeSegments.pop();

        MemorySegment newSegment = new MemorySegment(mallocCount);
        newSegment.free = false;
        newSegment.memoryStartIndex = n - freeLength;
        int result = allocateInRecentFree(newSegment, recentFree);
        Arrays.fill(cells, newSegment.memoryStartIndex, mallocCount, newSegment);
        return result;
    }

    private int allocateInRecentFree(MemorySegment newSegment, MemorySegment recentFree) {
        int index = segments.indexOf(recentFree);
        var iterator = segments.listIterator(index);
        iterator.next();
        iterator.remove();

        boolean newSegmentSmaller = newSegment.length <= recentFree.length;
        if (newSegmentSmaller) {
            var difference = recentFree.length - newSegment.length;
            var free = new MemorySegment(difference);
            iterator.add(newSegment);
            iterator.add(free);
            freeSegments.push(free);
        } else {
            iterator.add(newSegment);
        }
        return 0;
    }

    public int free(int i) {
        return 0;
    }

}

class MemorySegment {
    int length;
    int memoryStartIndex;
    boolean free = true;

    public MemorySegment(int length) {
        this.length = length;
    }
}

