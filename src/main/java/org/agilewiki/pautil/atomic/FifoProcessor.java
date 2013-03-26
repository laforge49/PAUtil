package org.agilewiki.pautil.atomic;

import java.util.ArrayDeque;
import java.util.Queue;

public class FifoProcessor extends AtomicProcessorBase {
    protected Queue<AtomicEntry> createQueue() {
        return  new ArrayDeque<AtomicEntry>();
    }
}
