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

        pp = MarksEngine.getMarkPositions("mama mila ramu");
        assertNotNull(pp);
        assertEquals(poslist(0, 5, 10, 14), pp);

        pp = MarksEngine.getMarkPositions("mama");
        assertNotNull(pp);
        assertEquals(poslist(0, 4), pp);
    }

    @Test
    public void testSkipsIndentationSpaces() {
        List<Integer> pp;
        pp = MarksEngine.getMarkPositions("    public    func");
        assertEquals(poslist(0, 4, 14, 18), pp);
    }

    @Test
    public void testPairedSpecialSymbolPositions() {
        List<Integer> pp;
        pp = MarksEngine.getMarkPositions("help(i)some");
        assertEquals(poslist(0, 5, 7, 11), pp);

    }

}
