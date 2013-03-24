package org.agilewiki.pautil;

import junit.framework.TestCase;

import org.agilewiki.pactor.ActorBase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;
import org.agilewiki.pactor.impl.DefaultMailboxFactoryImpl;

public class ContinuationTest extends TestCase {
    public void test() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            final Driver driver = new Driver();
            driver.initialize(mailboxFactory.createMailbox());
            System.out.println(">>> " + driver.doitReq().pend() + " <<<");
        } finally {
            mailboxFactory.close();
        }
    }
}

class Driver extends ActorBase {
    private Request<String> doitReq;

    public Request<String> doitReq() {
        return doitReq;
    }

    @Override
    public void initialize(final Mailbox _mailbox) {
        super.initialize(_mailbox);

        doitReq = new RequestBase<String>(_mailbox) {
            @Override
            public void processRequest(final ResponseProcessor<String> rp)
                    throws Exception {
                final Continuation<String> continuation = new Continuation<String>(
                        _mailbox, rp);
                final Application application = new Application(continuation);
                application.start();
            }
        };
    }
}

class Application extends Thread {
    private final Continuation<String> continuation;

    public Application(final Continuation<String> _continuation) {
        continuation = _continuation;
    }

    @Override
    public void run() {
        try {
            continuation.processResponse("Hello world!");
        } catch (final Throwable ex) {
            ex.printStackTrace();
        }
    }
}
