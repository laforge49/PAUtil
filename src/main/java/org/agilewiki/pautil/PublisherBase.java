package org.agilewiki.pautil;

import org.agilewiki.pactor.*;

import java.util.HashSet;
import java.util.Set;

public class PublisherBase<TARGET_ACTOR_TYPE extends Actor> extends
        AncestorBase implements Publisher<TARGET_ACTOR_TYPE> {
    private final Set<TARGET_ACTOR_TYPE> subscribers = new HashSet<TARGET_ACTOR_TYPE>();

    @Override
    public Request<Boolean> subscribeReq(final TARGET_ACTOR_TYPE _subscriber) {
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(final ResponseProcessor<Boolean> _rp)
                    throws Exception {
                _rp.processResponse(subscribers.add(_subscriber));
            }
        };
    }

    @Override
    public Request<Boolean> unsubscribeReq(final TARGET_ACTOR_TYPE _subscriber) {
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(final ResponseProcessor<Boolean> _rp)
                    throws Exception {
                _rp.processResponse(subscribers.remove(_subscriber));
            }
        };
    }

    @Override
    public Request<Void> publishReq(
            final UnboundRequest<Void, TARGET_ACTOR_TYPE> event) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(final ResponseProcessor<Void> _rp)
                    throws Exception {
                final Object[] subs = subscribers.toArray();
                final ResponseCounter<Void> rc = new ResponseCounter<Void>(
                        subs.length, null, _rp);
                getMailbox().setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(final Throwable throwable)
                            throws Exception {
                        rc.decrementCount();
                    }
                });
                for (final Object object : subs) {
                    @SuppressWarnings("unchecked")
                    final TARGET_ACTOR_TYPE subscriber = (TARGET_ACTOR_TYPE) object;
                    event.send(getMailbox(), subscriber, rc);
                }
            }
        };
    }
}
