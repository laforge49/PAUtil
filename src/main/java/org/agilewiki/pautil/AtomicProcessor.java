package org.agilewiki.pautil;

import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

import java.util.ArrayDeque;
import java.util.Queue;

public class AtomicProcessor extends AncestorBase {
    private Queue<AtomicEntry> entries = new ArrayDeque<AtomicEntry>();

    public Request<?> atomicReq(final Atomic _atom) {
        return new RequestBase<Object>(mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<Object> _rp) throws Exception {
                entries.offer(new AtomicEntry(_atom, _rp));
                process();
            }
        };
    }

    private void process() throws Exception {
        if (mailbox.isEmpty() && !entries.isEmpty()) {
            final AtomicEntry entry = entries.remove();
            final Atomic atom = entry.atom;
            final ResponseProcessor<Object> _rp = new ResponseProcessor<Object>() {
                @Override
                public void processResponse(Object response) throws Exception {
                    entry.rp.processResponse(response);
                    mailbox.flush();
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
                atom.process(AtomicProcessor.this, _rp);
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
