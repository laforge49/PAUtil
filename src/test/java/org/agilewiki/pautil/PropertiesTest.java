package org.agilewiki.pautil;

import junit.framework.TestCase;
import org.agilewiki.pactor.ActorBase;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

public class PropertiesTest extends TestCase {
    public void test() throws Exception {
        final MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            PAProperties p1 = new PAProperties();
            p1.initialize();
            PAProperties p2 = new PAProperties();
            p2.initialize(p1);
            mailboxFactory.setProperties(p2);
            p1.putProperty("a", "foo");
            p2.putProperty("b", "bar");
            ActorBase z = new ActorBase();
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
