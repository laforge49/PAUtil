package org.agilewiki.pautil.atomic;

import org.agilewiki.pactor.MailboxFactory;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Create an atomic request processor which processes requests from least to greatest.
 * (Requests must be Comparable.)
 */
public class PriorityRequestProcessor extends AtomicRequestProcessor {
    /**
     * Creates a PriorityRequestProcessor.
     *
     * @param _mailboxFactory The mailbox factory.
     * @return A new PriorityRequestProcessor.
     */
    public static PriorityRequestProcessor create(final MailboxFactory _mailboxFactory)
            throws Exception {
        PriorityRequestProcessor arp = new PriorityRequestProcessor();
        arp.initialize(_mailboxFactory.createMailbox(false, arp));
        return arp;
    }

    @Override
    protected Queue<AtomicEntry> createQueue() {
        return new PriorityQueue<AtomicEntry>();
    }
}
