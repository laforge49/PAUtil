package org.agilewiki.pautil;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

public class Continuation<RESPONSE_TYPE> implements
        ResponseProcessor<RESPONSE_TYPE> {
    private final Mailbox targetMailbox;
    private final ResponseProcessor<RESPONSE_TYPE> rp;

    public Continuation(final Mailbox _targetMailbox,
            final ResponseProcessor<RESPONSE_TYPE> _rp) {
        targetMailbox = _targetMailbox;
        rp = _rp;
    }

    @Override
    public void processResponse(final RESPONSE_TYPE rsp) throws Exception {
        new ContinuationRequest<RESPONSE_TYPE>(targetMailbox, rp, rsp).send();
    }

    public void processResponse(final Mailbox source, final RESPONSE_TYPE rsp)
            throws Exception {
        new ContinuationRequest<RESPONSE_TYPE>(targetMailbox, rp, rsp)
                .send(source);
    }
}

class ContinuationRequest<RESPONSE_TYPE> extends RequestBase<Void> {
    private final ResponseProcessor<RESPONSE_TYPE> rp;
    private final RESPONSE_TYPE rsp;

    public ContinuationRequest(final Mailbox targetMailbox,
            final ResponseProcessor<RESPONSE_TYPE> _rp, final RESPONSE_TYPE _rsp) {
        super(targetMailbox);
        rp = _rp;
        rsp = _rsp;
    }

    @Override
    public void processRequest(final ResponseProcessor<Void> _rp)
            throws Exception {
        rp.processResponse(rsp);
        _rp.processResponse(null);
    }
}