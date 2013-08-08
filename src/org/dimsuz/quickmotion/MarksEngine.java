package org.dimsuz.quickmotion;

import java.util.ArrayList;
import java.util.List;

public class MarksEngine {

    public List<Integer> getMarkPositions(String string) {
        ArrayList<Integer> result = new ArrayList<>();

        result.add(0);

        int startPos = 0;
        int spacePos;
        while((spacePos = string.indexOf(' ', startPos)) != -1) {
            startPos = spacePos+1;
            result.add(spacePos+1);
        }
        result.add(string.length());

        return result;
    }

}
