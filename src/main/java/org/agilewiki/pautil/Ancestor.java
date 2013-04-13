package org.agilewiki.pautil;

import org.agilewiki.pactor.Actor;

/**
 * The Ancestor interface supports injection of an immutable dependency stack.
 */
public interface Ancestor extends Actor {
    /**
     * Returns the parent actor in the dependency stack.
     * @return The parent actor, or null.
     */
    public Ancestor getParent();
}
