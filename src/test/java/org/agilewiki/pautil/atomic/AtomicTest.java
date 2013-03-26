package org.agilewiki.pautil.atomic;

import junit.framework.TestCase;
import org.agilewiki.pactor.*;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;
import org.agilewiki.pautil.Delay;

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
                atomicProcessor.atomicReq(atom1).reply(_mailbox, new ResponseProcessor() {
                    @Override
                    public void processResponse(Object response) throws Exception {
                        System.out.println(response);
                        _rp.processResponse(null);
                    }
                });
                /*
                atomicProcessor.atomicReq(atom2).reply(_mailbox, new ResponseProcessor() {
                    @Override
                    public void processResponse(Object response) throws Exception {
                        System.out.println(response);
                    }
                });
                atomicProcessor.atomicReq(atom3).reply(_mailbox, new ResponseProcessor() {
                    @Override
                    public void processResponse(Object response) throws Exception {
                        System.out.println(response);
                    }
                });
                atomicProcessor.atomicReq(atom4).reply(_mailbox, new ResponseProcessor() {
                    @Override
                    public void processResponse(Object response) throws Exception {
                        System.out.println(response);
                    }
                });
                atomicProcessor.atomicReq(atom5).reply(_mailbox, new ResponseProcessor() {
                    @Override
                    public void processResponse(Object response) throws Exception {
                        System.out.println(response);
                    }
                });
                */
            }
        };
    }
}

class AtomA implements Atomic<Integer> {
    public int msg;

    @Override
    public void process(final AtomicProcessorBase _processor, final ResponseProcessor<Integer> _rp)
            throws Exception {
        Mailbox mailbox = _processor.getMailbox();
        Delay delay = new Delay(mailbox.getMailboxFactory());
        delay.sleepReq(100 - (msg * 20)).reply(mailbox, new ResponseProcessor<Void>() {
            @Override
            public void processResponse(Void response) throws Exception {
                _rp.processResponse(msg);
            }
        });
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}