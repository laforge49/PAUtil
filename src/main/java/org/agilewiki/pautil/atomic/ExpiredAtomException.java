package org.agilewiki.pautil.atomic;

public class ExpiredAtomException extends Exception {
    public final Atomic<?> atom;

    public ExpiredAtomException(final Atomic _atom) {
        super(_atom.getClass().getName());
        atom = _atom;
    }

    /**
     * Speeds things up by not filling in the stack trace.
     *
     * @return this
     */
    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
