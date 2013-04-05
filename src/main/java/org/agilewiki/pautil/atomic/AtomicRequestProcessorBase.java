package org.agilewiki.pautil.atomic;

import org.agilewiki.pactor.*;
import org.agilewiki.pautil.Ancestor;
import org.agilewiki.pautil.AncestorBase;

import java.util.Queue;

public abstract class AtomicRequestProcessorBase
        extends AncestorBase implements AtomicRequestProcessor {
    private Queue<AtomicEntry> entries;
    private Mailbox mailbox;
    private boolean busy;

    protected abstract Queue<AtomicEntry> createQueue();

    @Override
    public void initialize(final Mailbox _mailbox, final Ancestor _parent) throws Exception {
        super.initialize(_mailbox, _parent);
        entries = createQueue();
        mailbox = _mailbox;
    }


    public Request<?> atomicReq(final Request _request) {
        return new RequestBase<Object> (mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<Object> _rp) throws Exception {
                entries.offer(new AtomicEntry(_request, _rp));
            }
        };
    }

    public void run() {
        if (!busy && !entries.isEmpty()) {
            final AtomicEntry entry = entries.remove();
            final Request request = entry.request;
            final ResponseProcessor<Object> _rp = new ResponseProcessor<Object>() {
                @Override
                public void processResponse(Object response) throws Exception {
                    busy = false;
                    entry.rp.processResponse(response);
                }
            };
            mailbox.setExceptionHandler(new ExceptionHandler() {
                @Override
                public void processException(Throwable throwable) throws Exception {
                    busy = false;
                    _rp.processResponse(throwable);
                }
            });
            busy = true;
            try {
                request.send(mailbox, _rp);
            } catch (Exception ex) {
                try {
                    busy = false;
                    _rp.processResponse(ex);
                } catch (Exception ex2) {}
            }
        }
    }
}

class AtomicEntry {
    public Request request;
    public ResponseProcessor<Object> rp;

    public AtomicEntry(final Request _request, final ResponseProcessor<Object> _rp) {
        request = _request;
        rp = _rp;
    }
}
