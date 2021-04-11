package ex.training.memory;

import java.util.Stack;

public class MemoryManager {
    private MemorySegment[] cells;
    private Stack<MemorySegment> freeSegments;

    public MemoryManager(int n) {
        cells = new MemorySegment[n];
        MemorySegment ms = new MemorySegment();
        ms.length = n;
        freeSegments = new Stack<>();
        freeSegments.push(ms);
    }

    public int malloc(int mallocCount) {
        if (mallocCount > cells.length || mallocCount < 1) {
            return -1;
        }

        MemorySegment lastFree = popLastFree();
        int length = lastFree.length;
        if (length < mallocCount) {
            return -1;
        }

        MemorySegment newSegment = new MemorySegment();
        newSegment.length = mallocCount;
        newSegment.status = SegmentStatus.ALLOCATED;
        MemorySegment prev = lastFree.prev;
        MemorySegment next = lastFree.next;
        if (prev != null) {
            newSegment.prev = prev;
            prev.next = newSegment;
        }
        if (length != mallocCount) {
            MemorySegment free = new MemorySegment();
            free.length = length - mallocCount;
            newSegment.next = free;
            free.prev = newSegment;
            if (next != null) {
                next.prev = free;
                free.next = next;
            }
            freeSegments.push(free);
        }

        return 0;
    }

    public int free(int i) {
        MemorySegment memorySegment = cells[i];
        if (memorySegment == null) {
            return -1;
        }
        cells[i] = null;

        int length = memorySegment.length;
        memorySegment.status = SegmentStatus.DELETED;
        MemorySegment prev = memorySegment.prev;
        MemorySegment next = memorySegment.next;
        MemorySegment newSegment = new MemorySegment();
        freeSegments.push(newSegment);
        if (prev != null) {
            if (prev.status == SegmentStatus.FREE) {
                prev.status = SegmentStatus.DELETED;
                length += prev.length;
                MemorySegment previousAllocated = prev.prev;
                if (previousAllocated != null) {
                    previousAllocated.next = newSegment;
                    newSegment.prev = previousAllocated;
                }
                freeSegments.push(prev);
            } else {
                prev.next = newSegment;
                newSegment.prev = prev;
            }
        }
        if (next != null && next.status == SegmentStatus.FREE) {
            next.status = SegmentStatus.DELETED;
            length += next.length;
            MemorySegment nextAllocated = next.next;
            if (nextAllocated != null) {
                nextAllocated.prev = newSegment;
                newSegment.next = nextAllocated;
            }
            freeSegments.push(next);
        }
        newSegment.length = length;
        return 0;
    }

    private MemorySegment popLastFree() {
        if (freeSegments.isEmpty()) {
            return null;
        }
        MemorySegment peek = freeSegments.pop();
        while (peek.status != SegmentStatus.FREE) {
            peek = freeSegments.pop();
        }
        return peek;
    }
}

enum SegmentStatus {
    FREE,
    DELETED,
    ALLOCATED
}

class MemorySegment {
    int length;
    SegmentStatus status = SegmentStatus.FREE;
    MemorySegment prev;
    MemorySegment next;
}

