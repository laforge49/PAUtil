package org.agilewiki.pautil;

import org.agilewiki.pactor.ResponseProcessor;

public interface Atomic<RESPONSE_TYPE> {
    public void process(final AtomicProcessor processor, final ResponseProcessor<RESPONSE_TYPE> rp)
            throws Exception;
}
