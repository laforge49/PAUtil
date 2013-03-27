package org.agilewiki.pautil;

import org.agilewiki.pactor.*;

public class Delay {
    private final Mailbox mailbox;

    public Delay(final MailboxFactory mailboxFactory) {
        mailbox = mailboxFactory.createMailbox();
        mailbox.disableCommandeering();
    }

    public Request<Void> sleepReq(final long delay) {
        return new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                Thread.sleep(delay);
                responseProcessor.processResponse(null);
            }
        };
    }
}
