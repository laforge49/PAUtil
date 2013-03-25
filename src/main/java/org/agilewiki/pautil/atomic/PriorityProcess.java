package org.agilewiki.pautil.atomic;

import java.util.PriorityQueue;
import java.util.Queue;

public class PriorityProcess extends AtomicProcessor {
    protected Queue<AtomicEntry> createQueue() {
        return  new PriorityQueue<AtomicEntry>();
    }
}
