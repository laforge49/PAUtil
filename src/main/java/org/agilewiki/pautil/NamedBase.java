package org.agilewiki.pautil;

import org.agilewiki.pactor.ActorBase;

/**
 * Implements Named with an effectively final value.
 */
public class NamedBase extends ActorBase implements Named {
    /**
     * The actor name, or null.
     */
    private String actorName;

    /**
     * Returns the immutable actor name.
     *
     * @return The actor name, or null.
     */
    @Override
    public String getActorName() {
        return actorName;
    }

    /**
     * Assigns an actor name, unless already assigned.
     *
     * @param _actorName The actor name.
     */
    public void setActorName(final String _actorName) throws Exception {
        if (actorName != null)
            throw new UnsupportedOperationException("Already named: " + actorName);
        actorName = _actorName;
    }
}
