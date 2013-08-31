package org.dimsuz.quickmotion;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarksEngine {

    private static Pattern pattern = Pattern.compile("(?:\\s+|\\(|\\))");

    public static List<Integer> getMarkPositions(String string) {
        ArrayList<Integer> result = new ArrayList<>();

        result.add(0);

        Matcher m = pattern.matcher(string);
        while(m.find()) {
            result.add(m.end());
        }

        result.add(string.length());

        return result;
    }

}
