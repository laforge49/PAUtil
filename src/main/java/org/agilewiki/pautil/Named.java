package org.agilewiki.pautil;

import org.agilewiki.pactor.Actor;

/**
 * Immutable actor names.
 */
public interface Named extends Actor {
    /**
     * Returns the actor name, or null.
     *
     * @return The actor name, or null.
     */
    public String getActorName();
}
