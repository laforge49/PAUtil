package org.agilewiki.pautil.atomic;

import java.util.ArrayDeque;
import java.util.Queue;

public class FifoRequestProcessor extends AtomicRequestProcessorBase {
    protected Queue<AtomicEntry> createQueue() {
        return new ArrayDeque<AtomicEntry>();
    }
}
