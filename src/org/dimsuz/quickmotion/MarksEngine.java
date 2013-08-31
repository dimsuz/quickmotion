package org.dimsuz.quickmotion;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarksEngine {

    private static Pattern pattern = Pattern.compile("(?:\\s+|\\(|\\))");

    public static class JumpPosition {
        /**
         * Position within line
         */
        int pos;
        /**
         * Position label (hotkey)
         */
        String label;

        public static JumpPosition create(int pos, String label) {
            JumpPosition p = new JumpPosition();
            p.pos = pos;
            p.label = label;
            return p;
        }
    }

    public static List<JumpPosition> getMarkPositions(String line) {
        ArrayList<JumpPosition> result = new ArrayList<>();

        result.add(JumpPosition.create(0, "a"));

        Matcher m = pattern.matcher(line);
        while(m.find()) {
            result.add(JumpPosition.create(m.end(), "b"));
        }

        result.add(JumpPosition.create(line.length(), "c"));

        return result;
    }

}
