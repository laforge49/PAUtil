package org.agilewiki.pautil;

import org.agilewiki.pactor.ResponseProcessor;

public interface Atomic<RESPONSE_TYPE> {
    public void process(Ancestor processor, ResponseProcessor<RESPONSE_TYPE> rp) throws Exception;
}
