package org.agilewiki.pautil.atomic;

import junit.framework.TestCase;
import org.agilewiki.pactor.*;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;
import org.agilewiki.pautil.Delay;
import org.agilewiki.pautil.ResponseCounter;

public class AtomicTest extends TestCase {
    public void test1() throws Exception {
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            int count = startReq1(mailboxFactory.createAsyncMailbox()).pend();
            assertEquals(5, count);
        } finally {
            mailboxFactory.close();
        }
    }

    Request<Integer> startReq1(final Mailbox _mailbox) {
        return new RequestBase<Integer>(_mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<Integer> _rp) throws Exception {
                final AP ap = new AP();
                ap.initialize(getMailbox().createAsyncMailbox());
                ResponseProcessor rc = new ResponseCounter(5, new ResponseProcessor() {
                    @Override
                    public void processResponse(Object response) throws Exception {
                        _rp.processResponse(ap.count);
                    }
                }, null);
                ap.atomicReq(new AtomA(1)).reply(_mailbox, rc);
                ap.atomicReq(new AtomA(2)).reply(_mailbox, rc);
                ap.atomicReq(new AtomA(3)).reply(_mailbox, rc);
                ap.atomicReq(new AtomA(4)).reply(_mailbox, rc);
                ap.atomicReq(new AtomA(5)).reply(_mailbox, rc);
            }
        };
    }
}
class AP extends FifoProcessor {
    int count = 0;
}

class AtomA implements Atomic<Void> {
    final private int msg;

    public AtomA(final int _msg) {
        this.msg = _msg;
    }

    @Override
    public void process(final AtomicProcessorBase _processor, final ResponseProcessor<Void> _rp)
            throws Exception {
        final AP ap = (AP) _processor;
        Mailbox mailbox = ap.getMailbox();
        Delay delay = new Delay(mailbox.getMailboxFactory());
        delay.sleepReq(100 - (msg * 20)).reply(mailbox, new ResponseProcessor<Void>() {
            @Override
            public void processResponse(Void response) throws Exception {
                if (ap.count != msg -1)
                    throw new IllegalStateException();
                ap.count = msg;
                _rp.processResponse(null);
            }
        });
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}