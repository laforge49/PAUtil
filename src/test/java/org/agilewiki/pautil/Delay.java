package org.agilewiki.pautil;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.MailboxFactory;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

public class Delay {
    private final Mailbox mailbox;

    public Delay(final MailboxFactory mailboxFactory) {
        mailbox = mailboxFactory.createMailbox(true);
    }

    public Request<Void> sleepReq(final long _delay) {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                Thread.sleep(_delay);
                responseProcessor.processResponse(null);
            }
        };
    }
}
