package org.agilewiki.general.messaging;

import org.agilewiki.pactor.*;

/**
 * Test code.
 */
public class Actor2 {
    private final Mailbox mailbox;

    public Actor2(final Mailbox mbox) {
        this.mailbox = mbox;
    }

    public Request<String> hi2(final Actor1 actor1) {
        return new RequestBase<String>(mailbox) {
            @Override
            public void processRequest(
                    final Transport<String> responseProcessor)
                    throws Exception {
                actor1.hi1.send(mailbox, responseProcessor);
            }
        };
    }
}
