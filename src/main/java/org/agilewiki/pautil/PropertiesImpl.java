package org.agilewiki.pautil;

import org.agilewiki.pactor.Properties;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * GetProperties first checks the component's own table of name/value pairs. If the property is not
 * found and its parent also has a Properties component, then the request is passed up to
 * the parent.
 */
public class PropertiesImpl extends AncestorBase implements Properties {

    public static Properties getAncestor(final Ancestor child) {
        return (Properties) getAncestor(child, Properties.class);
    }

    public static Properties getMatch(final Ancestor child) {
        return (Properties) getMatch(child, Properties.class);
    }

    public static Object getProperty(final Ancestor child, final String propertyName)
            throws Exception {
        Properties properties = getMatch(child);
        if (properties == null) {
            properties = child.getMailbox().getMailboxFactory().getProperties();
            if (properties == null)
                throw new UnsupportedOperationException("no Properties ancestor");
        }
        return properties.getProperty(propertyName);
    }

    public static void putProperty(final Ancestor child, final String propertyName, final Object propertyValue)
            throws Exception {
        Properties properties = getMatch(child);
        if (properties == null) {
            properties = child.getMailbox().getMailboxFactory().getProperties();
            if (properties == null)
                throw new UnsupportedOperationException("no Properties ancestor");
        }
        properties.putProperty(propertyName, propertyValue);
    }

    /**
     * Table of registered actors.
     */
    private ConcurrentSkipListMap<String, Object> properties =
            new ConcurrentSkipListMap<String, Object>();

    @Override
    public Object getProperty(final String propertyName) throws Exception {
        if (properties.containsKey(propertyName))
            return properties.get(propertyName);
        Properties properties = getAncestor(this);
        if (properties == null)
            return null;
        return properties.getProperty(propertyName);
    }

    @Override
    public void putProperty(final String propertyName, final Object propertyValue) throws Exception {
        properties.put(propertyName, propertyValue);
    }

    @Override
    public Iterator<Map.Entry<String, Object>> iterator() {
        return properties.entrySet().iterator();
    }
}
