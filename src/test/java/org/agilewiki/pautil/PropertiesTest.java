package org.agilewiki.pautil;

import junit.framework.TestCase;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.impl.DefaultMailboxFactoryImpl;

public class PropertiesTest extends TestCase {
    public void test() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            PropertiesImpl p1 = new PropertiesImpl();
            p1.initialize(mailboxFactory.createMailbox());
            PropertiesImpl p2 = new PropertiesImpl();
            p2.initialize(p1.getMailbox(), p1);
            PropertiesImpl.setProperty(p1, "a", "foo");
            PropertiesImpl.setProperty(p2, "b", "bar");
            String a = (String) PropertiesImpl.getProperty(p2, "a");
            assertEquals("foo", a);
            String b = (String) PropertiesImpl.getProperty(p2, "b");
            assertEquals("bar", b);
            String c = (String) PropertiesImpl.getProperty(p2, "c");
            assertNull(c);
        } finally {
            mailboxFactory.close();
        }
    }
}
