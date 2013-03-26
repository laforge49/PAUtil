package org.agilewiki.pautil.atomic;

import org.agilewiki.pactor.*;
import org.agilewiki.pautil.Ancestor;
import org.agilewiki.pautil.AncestorBase;

import java.util.Queue;

public abstract class AtomicProcessorBase extends AncestorBase implements AtomicProcessor {
    private Queue<AtomicEntry> entries;

    protected abstract Queue<AtomicEntry> createQueue();

    @Override
    public void initialize(final Mailbox _mailbox, final Ancestor _parent) {
        super.initialize(_mailbox, _parent);
        entries = createQueue();
        _mailbox.setNoBuffering();
    }


    public Request<?> atomicReq(final Atomic _atom) {
        return new RequestBase<Object>(getMailbox()) {
            @Override
            public void processRequest(final ResponseProcessor<Object> _rp) throws Exception {
                entries.offer(new AtomicEntry(_atom, _rp));
                process();
            }
        };
    }

    private void process() throws Exception {
        if (getMailbox().isEmpty() && !entries.isEmpty()) {
            final AtomicEntry entry = entries.remove();
            final Atomic atom = entry.atom;
            final ResponseProcessor<Object> _rp = new ResponseProcessor<Object>() {
                @Override
                public void processResponse(Object response) throws Exception {
                    entry.rp.processResponse(response);
                    process();
                }
            };
            getMailbox().setExceptionHandler(new ExceptionHandler() {
                @Override
                public void processException(Throwable throwable) throws Exception {
                    _rp.processResponse(throwable);
                }
            });
            try {
                if (atom.isExpired())
                    throw new ExpiredAtomException(atom);
                atom.process(AtomicProcessorBase.this, _rp);
            } catch (Exception ex) {
                _rp.processResponse(ex);
            }
        }
    }
}

class AtomicEntry {
    public Atomic atom;
    public ResponseProcessor<Object> rp;

    public AtomicEntry(final Atomic _atom, final ResponseProcessor<Object> _rp) {
        atom = _atom;
        rp = _rp;
    }
}
