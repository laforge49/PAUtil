package org.agilewiki.pautil;

import junit.framework.TestCase;
import org.agilewiki.pactor.*;

public class ContinuationTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = new MailboxFactory();
        try {
            Driver driver = new Driver();
            driver.initialize(mailboxFactory.createMailbox());
            System.out.println(
                    ">>> " +
                            driver.doitReq().pend() +
                            " <<<");
        } finally {
            mailboxFactory.shutdown();
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
            public void processRequest(ResponseProcessor<String> rp) throws Exception {
                Continuation<String> continuation = new Continuation<String>(_mailbox, rp);
                Application application = new Application(continuation);
                application.start();
            }
        };
    }
}

class Application extends Thread {
    private Continuation<String> continuation;

    public Application(final Continuation<String> _continuation) {
        continuation = _continuation;
    }

    @Override
    public void run() {
        try {
            continuation.processResponse("Hello world!");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
