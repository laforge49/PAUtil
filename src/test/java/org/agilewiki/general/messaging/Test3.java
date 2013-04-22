package org.agilewiki.general.messaging;

import junit.framework.TestCase;
import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

/**
 * Test code.
 */
public class Test3 extends TestCase {
    public void testb() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        final Mailbox mailbox = mailboxFactory.createMailbox(true);
        final Actor3 actor3 = new Actor3(mailbox);
        actor3.hi3.call();
        mailboxFactory.close();
    }
}
