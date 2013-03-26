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

    public void test2() throws Exception {
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            final FifoProcessor fp = new FifoProcessor();
            fp.initialize(mailboxFactory.createAsyncMailbox());
            fp.atomicReq(new AtomB()).pend();
        } catch (ExpiredAtomException eae) {
            mailboxFactory.close();
            return;
        }
        throw new IllegalStateException();
    }

    public void test3() throws Exception {
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            final FifoProcessor fp = new FifoProcessor();
            fp.initialize(mailboxFactory.createAsyncMailbox());
            fp.atomicReq(new AtomC()).pend();
        } catch (UnsupportedOperationException uoe) {
            mailboxFactory.close();
            return;
        }
        throw new IllegalStateException();
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

class AtomB implements Atomic<Void> {
    @Override
    public void process(AtomicProcessorBase processor, ResponseProcessor<Void> rp) throws Exception {
        throw new UnsupportedOperationException("shouldn't happen");
    }

    @Override
    public boolean isExpired() {
        return true;
    }
}

class AtomC implements Atomic<Void> {
    @Override
    public void process(AtomicProcessorBase processor, ResponseProcessor<Void> rp) throws Exception {
        throw new UnsupportedOperationException("it happen");
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}
