package org.agilewiki.pautil;

import org.agilewiki.pactor.Actor;
import org.agilewiki.pactor.Request;
import org.agilewiki.pactor.UnboundRequest;

public interface Publisher<TARGET_ACTOR_TYPE extends Actor> extends Ancestor {
    Request<Boolean> subscribeRequest(final TARGET_ACTOR_TYPE _subscriber);
    Request<Boolean> unsubscribeRequest(final TARGET_ACTOR_TYPE _subscriber);
    Request<Void> publish(final UnboundRequest<Void, TARGET_ACTOR_TYPE> event);
}
