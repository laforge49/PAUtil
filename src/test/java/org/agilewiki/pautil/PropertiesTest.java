package org.agilewiki.pautil;

import junit.framework.TestCase;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

public class PropertiesTest extends TestCase {
    public void test() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            PAProperties p1 = new PAProperties();
            p1.initialize(mailboxFactory.createMailbox());
            PAProperties p2 = new PAProperties();
            p2.initialize(p1.getMailbox(), p1);
            mailboxFactory.setProperties(p2);
            PAProperties.putProperty(p1, "a", "foo");
            PAProperties.putProperty(p2, "b", "bar");
            AncestorBase z = new AncestorBase();
            z.initialize(mailboxFactory.createMailbox());
            String a = (String) PAProperties.getProperty(z, "a");
            assertEquals("foo", a);
            String b = (String) PAProperties.getProperty(z, "b");
            assertEquals("bar", b);
            String c = (String) PAProperties.getProperty(z, "c");
            assertNull(c);
        } finally {
            mailboxFactory.close();
        }
    }
}
