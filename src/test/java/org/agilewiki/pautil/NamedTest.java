package org.agilewiki.pautil;

import junit.framework.TestCase;

public class NamedTest extends TestCase {
    public void test() throws Exception {
        NamedBase a = new NamedBase();
        a.setName("foo");
        String nm = a.getName();
        assertEquals("foo", nm);
    }
}
