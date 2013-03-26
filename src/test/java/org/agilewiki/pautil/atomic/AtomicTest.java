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
            startReq1(mailboxFactory.createAsyncMailbox()).pend();
        } finally {
            mailboxFactory.close();
        }
    }

    Request<Void> startReq1(final Mailbox _mailbox) {
        return new RequestBase<Void>(_mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<Void> _rp) throws Exception {
                AtomicProcessorBase atomicProcessor = new FifoProcessor();
                atomicProcessor.initialize(getMailbox().createAsyncMailbox());
                ResponseProcessor rc = new ResponseCounter(5, _rp, null);
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
                atomicProcessor.atomicReq(atom1).reply(_mailbox, rc);
                atomicProcessor.atomicReq(atom2).reply(_mailbox, rc);
                atomicProcessor.atomicReq(atom3).reply(_mailbox, rc);
                atomicProcessor.atomicReq(atom4).reply(_mailbox, rc);
                atomicProcessor.atomicReq(atom5).reply(_mailbox, rc);
            }
        };
    }
}

class AtomA implements Atomic<Void> {
    public int msg;

    @Override
    public void process(final AtomicProcessorBase _processor, final ResponseProcessor<Void> _rp)
            throws Exception {
        Mailbox mailbox = _processor.getMailbox();
        Delay delay = new Delay(mailbox.getMailboxFactory());
        delay.sleepReq(100 - (msg * 20)).reply(mailbox, new ResponseProcessor<Void>() {
            @Override
            public void processResponse(Void response) throws Exception {
                System.out.println(msg);
                _rp.processResponse(null);
            }
        });
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}