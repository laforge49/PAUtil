package org.agilewiki.pautil.atomic;

import java.util.PriorityQueue;
import java.util.Queue;

public class PriorityRequestProcessor extends AtomicRequestProcessorBase {
    protected Queue<AtomicEntry> createQueue() {
        return  new PriorityQueue<AtomicEntry>();
    }
}
