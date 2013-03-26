package org.agilewiki.pautil;

public class NamedBase extends AncestorBase implements Named {
    /**
     * The actor name, or null.
     */
    private String actorName;

    @Override
    public String getActorName() throws Exception {
        return actorName;
    }

    /**
     * Assigns an actor name, unless already assigned.
     *
     * @param _actorName The actor name.
     */
    public void configure(final String _actorName) throws Exception {
        if (actorName != null)
            throw new UnsupportedOperationException("Already named: " + actorName);
        actorName = _actorName;
    }
}
