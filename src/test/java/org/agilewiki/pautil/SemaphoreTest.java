package org.agilewiki.pautil;

import junit.framework.TestCase;

import org.agilewiki.pactor.ExceptionHandler;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class SemaphoreTest extends TestCase {
    public void testI() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Semaphore semaphore = new Semaphore(
                mailboxFactory.createMailbox(), 1);
        semaphore.acquire.call();
        mailboxFactory.close();
    }

    public void testII() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Semaphore semaphore = new Semaphore(
                mailboxFactory.createMailbox(), 0);
        semaphore.release.signal();
        semaphore.acquire.call();
        mailboxFactory.close();
    }

    private Request<Void> delayedRelease(final Semaphore semaphore,
            final long delay, final MailboxFactory mailboxFactory) {
        return new RequestBase<Void>(mailboxFactory.createMailbox()) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                new Delay(mailboxFactory).sleepReq(delay).reply(getMailbox(),
                        new ResponseProcessor<Void>() {
                            @Override
                            public void processResponse(final Void response)
                                    throws Exception {
                                semaphore.release.signal();
                                responseProcessor.processResponse(null);
                            }
                        });
            }
        };
    }

    public void testIII() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Semaphore semaphore = new Semaphore(
                mailboxFactory.createMailbox(), 0);
        final long d = 100;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, mailboxFactory).signal();
        semaphore.acquire.call();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        mailboxFactory.close();
    }

    private Request<Boolean> acquireException(final Semaphore semaphore,
            final Mailbox mailbox) {
        return new RequestBase<Boolean>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Boolean> responseProcessor)
                    throws Exception {
                mailbox.setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        System.out.println(throwable);
                        responseProcessor.processResponse(true);
                    }
                });
                semaphore.acquire.reply(mailbox, new ResponseProcessor<Void>() {
                    @Override
                    public void processResponse(final Void response)
                            throws Exception {
                        throw new SecurityException("thrown after acquire");
                    }
                });
            }
        };
    }

    public void testIV() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Semaphore semaphore = new Semaphore(
                mailboxFactory.createMailbox(), 0);
        final long d = 100;
        final long t0 = System.currentTimeMillis();
        delayedRelease(semaphore, d, mailboxFactory).signal();
        final boolean result = acquireException(semaphore,
                mailboxFactory.createMailbox()).call();
        final long t1 = System.currentTimeMillis();
        assertTrue(t1 - t0 >= d);
        assertTrue(result);
        mailboxFactory.close();
    }
}
