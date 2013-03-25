package org.agilewiki.pautil.atomic;

import java.util.ArrayDeque;
import java.util.Queue;

public class FifoProcessor extends AtomicProcessor {
    protected Queue<AtomicEntry> createQueue() {
        return  new ArrayDeque<AtomicEntry>();
    }
}
