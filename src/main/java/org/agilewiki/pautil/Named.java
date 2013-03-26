package org.agilewiki.pautil;

/**
 * Immutable actor names.
 */
public interface Named extends Ancestor {
    /**
     * Returns the actor name, or null.
     *
     * @return The actor name, or null.
     */
    public String getActorName()
            throws Exception;
}
