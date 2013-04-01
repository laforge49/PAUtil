package org.agilewiki.pautil.atomic;

import org.agilewiki.pactor.Request;
import org.agilewiki.pautil.Ancestor;

public interface AtomicRequestProcessor extends Ancestor, Runnable {
    public Request<?> atomicReq(final Request _expiringRequest);
}
