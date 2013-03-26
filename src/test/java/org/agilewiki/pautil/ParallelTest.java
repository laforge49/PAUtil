package org.agilewiki.pautil;

import junit.framework.TestCase;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class ParallelTest extends TestCase {
    private static final int LOADS = 10;
    private static final long DELAY = 200;

    private Mailbox mailbox;
    private MailboxFactory mailboxFactory;
    private Request<Void> start;

    public void test() throws Exception {
        mailboxFactory = new DefaultMailboxFactoryImpl();
        mailbox = mailboxFactory.createMailbox();

        start = new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                final ResponseCounter<Void> responseCounter = new ResponseCounter<Void>(
                        LOADS, null, responseProcessor);
                int i = 0;
                while (i < LOADS) {
                    final Delay dly = new Delay(mailboxFactory);
                    dly.sleepReq(ParallelTest.DELAY).reply(mailbox,
                            responseCounter);
                    i += 1;
                }
            }
        };

        final long t0 = System.currentTimeMillis();
        start.pend();
        final long t1 = System.currentTimeMillis();
        assertTrue((t1 - t0) < DELAY + DELAY / 2);
        mailboxFactory.close();
    }
}
