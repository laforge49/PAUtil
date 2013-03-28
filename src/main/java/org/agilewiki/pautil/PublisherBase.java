package org.agilewiki.pautil;

import org.agilewiki.pactor.*;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PublisherBase<TARGET_ACTOR_TYPE extends Actor>
        extends AncestorBase implements Publisher<TARGET_ACTOR_TYPE> {
    private Set<TARGET_ACTOR_TYPE> subscribers = new HashSet<TARGET_ACTOR_TYPE>();

    @Override
    public Request<Boolean> subscribeReq(final TARGET_ACTOR_TYPE _subscriber){
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(final ResponseProcessor<Boolean> _rp) throws Exception {
                _rp.processResponse(subscribers.add(_subscriber));
            }
        };
    }

    @Override
    public Request<Boolean> unsubscribeReq(final TARGET_ACTOR_TYPE _subscriber) {
        return new RequestBase<Boolean>(getMailbox()) {
            @Override
            public void processRequest(final ResponseProcessor<Boolean> _rp) throws Exception {
                _rp.processResponse(subscribers.remove(_subscriber));
            }
        };
    }

    @Override
    public Request<Void> publishReq(final UnboundRequest<Void, TARGET_ACTOR_TYPE> event) {
        return new RequestBase<Void>(getMailbox()) {
            @Override
            public void processRequest(final ResponseProcessor<Void> _rp) throws Exception {
                Set<TARGET_ACTOR_TYPE> subs = new HashSet<TARGET_ACTOR_TYPE>(subscribers);
                Iterator<TARGET_ACTOR_TYPE> it = subs.iterator();
                final ResponseCounter<Void> rc = new ResponseCounter<Void>(subs.size(), null, _rp);
                getMailbox().setExceptionHandler(new ExceptionHandler() {
                    @Override
                    public void processException(Throwable throwable) throws Exception {
                        rc.decrementCount();
                    }
                });
                while (it.hasNext()) {
                    TARGET_ACTOR_TYPE subscriber = it.next();
                    event.send(getMailbox(), subscriber, rc);
                }
            }
        };
    }
}
