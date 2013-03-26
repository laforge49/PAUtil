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
                AtomA atom1 = new AtomA();
                AtomA atom2 = new AtomA();
                AtomA atom3 = new AtomA();
                AtomA atom4 = new AtomA();
                AtomA atom5 = new AtomA();
                atom1.msg = 1;
                atom2.msg = 2;
                atom3.msg = 3;
                atom4.msg = 4;
                atom5.msg = 5;
                ap.atomicReq(atom1).reply(_mailbox, rc);
                ap.atomicReq(atom2).reply(_mailbox, rc);
                ap.atomicReq(atom3).reply(_mailbox, rc);
                ap.atomicReq(atom4).reply(_mailbox, rc);
                ap.atomicReq(atom5).reply(_mailbox, rc);
            }
        };
    }
}
class AP extends FifoProcessor {
    int count = 0;
}

class AtomA implements Atomic<Void> {
    public int msg;

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