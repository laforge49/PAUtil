package org.agilewiki.pautil;

import junit.framework.TestCase;
import org.agilewiki.pactor.ActorBase;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

public class NamedTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            NamedBase a = new NamedBase();
            a.initialize(mailboxFactory.createMailbox());
            a.setActorName("foo");
            String nm = a.getActorName();
            assertEquals("foo", nm);
        } finally {
            mailboxFactory.close();
        }
    }
}
