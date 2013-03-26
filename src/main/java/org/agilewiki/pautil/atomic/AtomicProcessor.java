package org.agilewiki.pautil.atomic;

import org.agilewiki.pactor.Request;
import org.agilewiki.pautil.Ancestor;

public interface AtomicProcessor extends Ancestor {
    public Request<?> atomicReq(final Atomic _atom);
}
