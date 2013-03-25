package org.agilewiki.pautil;

/**
 * Manages a property set.
 */
public interface Properties extends Ancestor {
    public Object getProperty(final String propertyName)
            throws Exception;

    /**
     * Assign a value to a property.
     *
     * @param propertyName  The name of the property.
     * @param propertyValue The value to be assigned.
     */
    public void setProperty(final String propertyName, final Object propertyValue)
            throws Exception;
}
