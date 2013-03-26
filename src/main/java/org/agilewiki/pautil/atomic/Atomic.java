package org.agilewiki.pautil.atomic;

import org.agilewiki.pactor.ResponseProcessor;

public interface Atomic<RESPONSE_TYPE> {
    public void process(final AtomicProcessorBase processor, final ResponseProcessor<RESPONSE_TYPE> rp)
            throws Exception;
}
