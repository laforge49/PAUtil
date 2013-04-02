package org.agilewiki.pautil;

import junit.framework.TestCase;
import org.agilewiki.pactor.*;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

public class PAIteratorTest extends TestCase {
    private Mailbox mailbox;
    private long runs;

    public void test() throws Exception {
        runs = 100000000;

//                shared mailbox test
//                [java-shared] Number of runs: 100000000
//                [java-shared] Count: 100000000
//                [java-shared] Test time in milliseconds: 4080
//                [java-shared] Messages per second: 24,509,803


        System.out.println("shared mailbox test");
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            mailbox = mailboxFactory.createMailbox();
            runReq().call();
        } finally {
            mailboxFactory.close();
        }
    }

    Request<Void> runReq() {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<Void> _rp) throws Exception {
                final CounterActor counterActor = new CounterActor();
                counterActor.initialize(mailbox);
                final UnboundAddReq uar = new UnboundAddReq(1);
                final UnboundResetReq urr = new UnboundResetReq();
                PAIterator pait = new PAIterator() {
                    long i = 0;

                    @Override
                    protected void process(ResponseProcessor rp1) throws Exception {
                        if (i == runs) rp1.processResponse(this);
                        else {
                            i += 1;
                            uar.send(mailbox, counterActor, rp1);
                        }
                    }
                };
                final long start = System.currentTimeMillis();
                pait.iterate(new ResponseProcessor() {
                    @Override
                    public void processResponse(Object response) throws Exception {
                        urr.send(mailbox, counterActor, new ResponseProcessor<Long>() {
                            @Override
                            public void processResponse(final Long count) throws Exception {
                                long finish = System.currentTimeMillis();
                                long elapsedTime = finish - start;
                                System.out.println("[java-shared] Number of runs: " + runs);
                                System.out.println("[java-shared] Count: " + count);
                                System.out.println("[java-shared] Test time in milliseconds: " + elapsedTime);
                                if (elapsedTime > 0)
                                    System.out.println("[java-shared] Messages per second: " + (runs * 1000 / elapsedTime));
                                _rp.processResponse(null);
                            }
                        });
                    }
                });
            }
        };
    }
}

class UnboundAddReq extends UnboundRequestBase<Void, CounterActor> {
    private final long inc;

    UnboundAddReq(final long _inc) {
        inc = _inc;
    }

    @Override
    public void processRequest(final CounterActor _targetActor, final ResponseProcessor<Void> _rp) throws Exception {
        _targetActor.add(inc);
        _rp.processResponse(null);
    }
}

class UnboundResetReq extends UnboundRequestBase<Long, CounterActor> {

    @Override
    public void processRequest(final CounterActor _targetActor, final ResponseProcessor<Long> _rp)
            throws Exception {
        _rp.processResponse(_targetActor.reset());
    }
}

class CounterActor extends ActorBase {
    private long count = 0L;

    public void add(long inc) {
        count += inc;
    }

    public long reset()
            throws Exception {
        long rv = count;
        count = 0;
        return rv;
    }
}
