package org.dimsuz.quickmotion;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("static-method")
public class MarksEngineTest {

    private MarksEngine engine;

    @Before
    public void setUp() throws Exception {
        engine = new MarksEngine();
    }

    private List<Integer> poslist(Integer... pos) {
        return Arrays.asList(pos);
    }

    @Test
    public void testSimpleSpacePostions() {
        List<Integer> pp;

        pp = engine.getMarkPositions("mama mila ramu");
        assertNotNull(pp);
        assertEquals(poslist(0, 5, 10, 14), pp);

        pp = engine.getMarkPositions("mama");
        assertNotNull(pp);
        assertEquals(poslist(0, 4), pp);
    }

}
