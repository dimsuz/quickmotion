package org.dimsuz.quickmotion;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dimsuz.quickmotion.MarksEngine.JumpPosition;
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

    private List<String> labellist(String... labels) {
        return Arrays.asList(labels);
    }

    private List<Integer> to_poslist(List<JumpPosition> pos) {
        ArrayList<Integer> res = new ArrayList<Integer>(pos.size());
        for (JumpPosition p : pos) {
            res.add(p.pos);
        }
        return res;
    }

    private List<String> to_labellist(List<JumpPosition> pos) {
        ArrayList<String> res = new ArrayList<String>(pos.size());
        for (JumpPosition p : pos) {
            res.add(p.label);
        }
        return res;
    }

    @Test
    public void testSimpleSpacePostions() {
        List<JumpPosition> pp;

        pp = MarksEngine.getMarkPositions("mama mila ramu");
        assertNotNull(pp);
        assertEquals(poslist(0, 5, 10, 14), to_poslist(pp));

        pp = MarksEngine.getMarkPositions("mama");
        assertNotNull(pp);
        assertEquals(poslist(0, 4), to_poslist(pp));
    }

    @Test
    public void testSkipsIndentationSpaces() {
        List<JumpPosition> pp;
        pp = MarksEngine.getMarkPositions("    public    func");
        assertEquals(poslist(0, 4, 14, 18), to_poslist(pp));
    }

    @Test
    public void testPairedSpecialSymbolPositions() {
        List<JumpPosition> pp;
        pp = MarksEngine.getMarkPositions("help(i)some");
        assertEquals(poslist(0, 5, 7, 11), to_poslist(pp));
    }

    @Test
    public void testSimpleLabelAssignment() {
        List<JumpPosition> pp;

        pp = MarksEngine.getMarkPositions("mama mila ramu");

        assertEquals(labellist("a", "s", "d", "f"), to_labellist(pp));
    }
}
