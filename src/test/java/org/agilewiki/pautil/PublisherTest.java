package org.agilewiki.pautil;

import junit.framework.TestCase;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.ResponseProcessor;
import org.agilewiki.pactor.UnboundRequestBase;
import org.agilewiki.pamailbox.DefaultMailboxFactoryImpl;

public class PublisherTest extends TestCase {
    public void test() throws Exception {
        MailboxFactory mailboxFactory = new DefaultMailboxFactoryImpl();
        try {
            PublisherBase p = new PublisherBase();
            p.initialize(mailboxFactory.createMailbox());
            Printer a = new Printer();
            a.initialize(mailboxFactory.createMailbox());
            a.configure("a");
            p.subscribeReq(a).call();
            Printer b = new Printer();
            b.initialize(mailboxFactory.createMailbox());
            b.configure("b");
            p.subscribeReq(b).call();
            Printer c = new Printer();
            c.initialize(mailboxFactory.createMailbox());
            c.configure("c");
            p.subscribeReq(c).call();
            p.publishReq(new Print("42")).call();
            p.publishReq(new Print("24")).call();
            p.publishReq(new Print("Hello world!")).call();
        } finally {
            mailboxFactory.close();
        }
    }
}

class Printer extends NamedBase {
    public void print(String s) throws Exception {
        System.out.println(getActorName() + " received " + s);
    }
}

class Print extends UnboundRequestBase<Void, Printer> {
    final String msg;

    Print(final String _msg) {
        msg = _msg;
    }

    @Override
    public void processRequest(final Printer _targetActor, final ResponseProcessor<Void> _rp) throws Exception {
        _targetActor.print(msg);
        _rp.processResponse(null);
    }
}