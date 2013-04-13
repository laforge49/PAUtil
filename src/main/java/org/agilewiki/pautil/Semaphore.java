package org.agilewiki.pautil;

import org.agilewiki.pactor.Mailbox;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.RequestBase;
import org.agilewiki.pactor.ResponseProcessor;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Blocks request processing, not threads.
 */
public class Semaphore {
    private final Mailbox mailbox;
    private int permits;
    private final Queue<ResponseProcessor<Void>> queue = new ArrayDeque<ResponseProcessor<Void>>();
    private final Request<Void> acquire;
    private final Request<Void> release;

    public Semaphore(final Mailbox mbox, final int permitCount) {
        this.mailbox = mbox;
        this.permits = permitCount;

        acquire = new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                if (permits > 0) {
                    permits -= 1;
                    responseProcessor.processResponse(null);
                } else {
                    queue.offer(responseProcessor);
                }
            }
        };

        release = new RequestBase<Void>(mailbox) {
            @Override
            public void processRequest(
                    final ResponseProcessor<Void> responseProcessor)
                    throws Exception {
                final ResponseProcessor<Void> rp = queue.poll();
                if (rp == null) {
                    permits += 1;
                } else {
                    rp.processResponse(null);
                }
                responseProcessor.processResponse(null);
            }
        };
    }

    public Request<Void> acquireReq() {
        return acquire;
    }

    public Request<Void> releaseReq() {
        return release;
    }
}
