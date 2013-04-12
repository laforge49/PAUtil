package org.agilewiki.pautil;

import junit.framework.TestCase;
import org.agilewiki.pactor.Actor;
import org.agilewiki.pactor.ActorBase;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

public class PropertiesTest extends TestCase {
    public void test() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            PropertiesImpl p1 = new PropertiesImpl();
            p1.initialize(mailboxFactory.createMailbox());
            PropertiesImpl p2 = new PropertiesImpl();
            p2.initialize(p1.getMailbox(), p1);
            mailboxFactory.setProperties(p2);
            PropertiesImpl.putProperty(p1, "a", "foo");
            PropertiesImpl.putProperty(p2, "b", "bar");
            AncestorBase z = new AncestorBase();
            z.initialize(mailboxFactory.createMailbox());
            String a = (String) PropertiesImpl.getProperty(z, "a");
            assertEquals("foo", a);
            String b = (String) PropertiesImpl.getProperty(z, "b");
            assertEquals("bar", b);
            String c = (String) PropertiesImpl.getProperty(z, "c");
            assertNull(c);
        } finally {
            mailboxFactory.close();
        }
    }
}
