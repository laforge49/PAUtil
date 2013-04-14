package org.agilewiki.pautil;

import org.agilewiki.pactor.ActorBase;
import org.agilewiki.pactor.Mailbox;

/**
 * Implements immutable dependency stack injection.
 */
public class AncestorBase extends ActorBase implements Ancestor {
    /**
     * Returns an ancestor, excluding the child, which is an instance of the target class.
     *
     * @param child       The actor who's dependency stack is searched.
     * @param targetClass The class used to select the ancestor.
     * @return The ancestor that is an instance of the target class, or null.
     */
    public static Ancestor getAncestor(final Ancestor child, final Class targetClass) {
        if (child == null)
            return null;
        return getMatch(child.getParent(), targetClass);
    }

    /**
     * Returns the child, or an ancestor, which is an instance of the target class.
     *
     * @param child       The actor who's dependency stack is searched.
     * @param targetClass The class used to select the child or one of its ancestors.
     * @return An actor that implements the target class, or null.
     */
    public static Ancestor getMatch(Ancestor child, final Class targetClass) {
        while (child != null) {
            if (targetClass.isInstance(child))
                return child;
            child = child.getParent();
        }
        return null;
    }

    /**
     * The top of the immutable dependency stack.
     */
    private Ancestor parent;

    /**
     * Initialize the actor with an ancestor stack, but no mailbox.
     *
     * @param _parent The top of the immutable dependency stack.
     */
    final public void initialize(final Ancestor _parent) throws Exception {
        initialize(null, _parent);
    }

    /**
     * Initialize the actor with a mailbox, but no dependency stack.
     *
     * @param _mailbox The mailbox used by the actor for message passing.
     */
    final public void initialize(final Mailbox _mailbox) throws Exception {
        initialize(_mailbox, null);
    }

    /**
     * Initialize the actor with both a mailbox and an immutable dependency stack.
     *
     * @param _mailbox The mailbox used by the actor for message passing.
     * @param _parent  The top of the immutable dependency stack.
     */
    public void initialize(final Mailbox _mailbox, final Ancestor _parent) throws Exception {
        super.initialize(_mailbox);
        parent = _parent;
    }

    @Override
    public Ancestor getParent() {
        return parent;
    }
}
