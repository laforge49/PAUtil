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
            int count = startReq1(mailboxFactory.createMailbox()).call();
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
                ap.initialize(getMailbox().createMailbox(true, true));
                ResponseProcessor rc = new ResponseCounter(5, null, new ResponseProcessor() {
                    @Override
                    public void processResponse(Object response) throws Exception {
                        _rp.processResponse(ap.count);
                    }
                });
                ap.atomicReq(aReq(ap, 1)).send(_mailbox, rc);
                ap.atomicReq(aReq(ap, 2)).send(_mailbox, rc);
                ap.atomicReq(aReq(ap, 3)).send(_mailbox, rc);
                ap.atomicReq(aReq(ap, 4)).send(_mailbox, rc);
                ap.atomicReq(aReq(ap, 5)).send(_mailbox, rc);
            }
        };
    }

    public void test2() throws Exception {
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            final FifoRequestProcessor fp = new FifoRequestProcessor();
            fp.initialize(mailboxFactory.createMailbox(true, true));
            fp.atomicReq(bReq(fp.getMailbox())).call();
        } catch (UnsupportedOperationException uoe) {
            mailboxFactory.close();
            return;
        }
        throw new IllegalStateException();
    }

    Request<Void> aReq(final AP ap, final int msg) {
        final Mailbox mailbox = ap.getMailbox();
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<Void> _rp) throws Exception {
                Delay delay = new Delay(mailbox.getMailboxFactory());
                delay.sleepReq(100 - (msg * 20)).send(mailbox, new ResponseProcessor<Void>() {
                    @Override
                    public void processResponse(Void response) throws Exception {
                        if (ap.count != msg -1)
                            throw new IllegalStateException();
                        ap.count = msg;
                        _rp.processResponse(null);
                    }
                });
            }
        };
    }

    Request<Void> bReq(final Mailbox _mailbox) {
        return new RequestBase<Void>(_mailbox) {
            @Override
            public void processRequest(ResponseProcessor<Void> responseProcessor) throws Exception {
                throw new UnsupportedOperationException("it happen");
            }
        };
    }
}

class AP extends FifoRequestProcessor {
    int count = 0;
}
