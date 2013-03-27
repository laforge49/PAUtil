package org.agilewiki.pautil.atomic;

import org.agilewiki.pactor.*;
import org.agilewiki.pautil.Ancestor;
import org.agilewiki.pautil.AncestorBase;

import java.util.Queue;

public abstract class AtomicRequestProcessorBase extends AncestorBase implements AtomicRequestProcessor {
    private Queue<AtomicEntry> entries;
    private Mailbox mailbox;

    protected abstract Queue<AtomicEntry> createQueue();

    @Override
    public void initialize(final Mailbox _mailbox, final Ancestor _parent) {
        super.initialize(_mailbox, _parent);
        entries = createQueue();
        mailbox = _mailbox;
    }


    public Request<?> atomicReq(final Request _request) {
        return new RequestBase<Object> (mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<Object> _rp) throws Exception {
                entries.offer(new AtomicEntry(_request, _rp));
                process();
            }
        };
    }

    private void process() throws Exception {
        if (mailbox.isEmpty() && !entries.isEmpty()) {
            final AtomicEntry entry = entries.remove();
            final Request request = entry.request;
            final ResponseProcessor<Object> _rp = new ResponseProcessor<Object>() {
                @Override
                public void processResponse(Object response) throws Exception {
                    entry.rp.processResponse(response);
                    process();
                }
            };
            mailbox.setExceptionHandler(new ExceptionHandler() {
                @Override
                public void processException(Throwable throwable) throws Exception {
                    _rp.processResponse(throwable);
                }
            });
            try {
                request.reply(mailbox, _rp);
                mailbox.flush();
            } catch (Exception ex) {
                _rp.processResponse(ex);
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
