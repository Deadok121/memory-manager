package ex.training.memory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;
import lombok.Getter;

/**
 * Toy memory manager with memory cells array , and separated segments.
 * <p>
 * Free segments stored in LRU Cache using stack.
 * Memory is linked list alternating free and already allocated segments.
 * Segment is sequence of bytes(memory cells).
 */
public class MemoryManager {
    @Getter
    private MemorySegment[] cells;
    @Getter
    private Stack<MemorySegment> freeSegments;
    @Getter
    private LinkedList<MemorySegment> segments;

    private final int memorySize;

    public MemoryManager(int memorySize) {
        cells = new MemorySegment[memorySize];
        MemorySegment ms = new MemorySegment(memorySize);
        segments = new LinkedList<>();
        freeSegments = new Stack<>();
        segments.add(ms);
        freeSegments.push(ms);
        this.memorySize = memorySize;
    }

    public int malloc(int mallocCount) {
        if (mallocCount > memorySize || mallocCount < 1) {
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
        newSegment.memoryStartIndex = memorySize - freeLength;
        allocateInRecentFree(newSegment, recentFree);
        Arrays.fill(cells, newSegment.memoryStartIndex, mallocCount, newSegment);
        return newSegment.memoryStartIndex;
    }

    private void allocateInRecentFree(MemorySegment newSegment, MemorySegment recentFree) {
        int index = segments.indexOf(recentFree);
        var iterator = segments.listIterator(index);
        iterator.next();
        iterator.remove();

        boolean newSegmentSmaller = newSegment.length <= recentFree.length;
        if (newSegmentSmaller) {
            var difference = recentFree.length - newSegment.length;
            var free = new MemorySegment(difference);
            free.memoryStartIndex = newSegment.length + newSegment.memoryStartIndex;
            iterator.add(newSegment);
            iterator.add(free);
            freeSegments.push(free);
        } else {
            iterator.add(newSegment);
        }
    }

    public int free(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Value is lower than zero!");
        }

        var segment = cells[i];
        if (segment == null || segment.free) {
            return -1;
        }

        var index = segments.indexOf(segment);
        var iterator = segments.listIterator(index);
        var newSegmentLength = 0;
        boolean hasPrevious = iterator.hasPrevious();
        var prevStartIndex = 0;
        if (hasPrevious) {
            var prev = iterator.previous();
            if (prev.free) {
                prevStartIndex = prev.memoryStartIndex;
                newSegmentLength = prev.length;
                freeSegments.remove(prev);
                // delete from stack
            }
        }
        var current = iterator.next();
        newSegmentLength += current.length;
        iterator.remove();
        if (iterator.hasNext()) {
            var next = iterator.next();
            if (next.free) {
                newSegmentLength += next.length;
                freeSegments.remove(next);
                iterator.remove();
            }
        }
        var newFreeSegment = new MemorySegment(newSegmentLength);
        newFreeSegment.memoryStartIndex = hasPrevious ? prevStartIndex : current.memoryStartIndex;
        iterator.add(newFreeSegment);
        freeSegments.push(newFreeSegment);
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

