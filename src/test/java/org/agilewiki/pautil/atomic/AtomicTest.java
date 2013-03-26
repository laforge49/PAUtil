package org.agilewiki.pautil.atomic;

import junit.framework.TestCase;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

public class AtomicTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            AtomicProcessorBase atomicProcessor = new FifoProcessor();
            atomicProcessor.initialize(mailboxFactory.createAsyncMailbox());
        } finally {
            mailboxFactory.close();
        }
    }
}
