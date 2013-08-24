package org.dimsuz.quickmotion;

import java.util.ArrayList;
import java.util.List;

public class MarksEngine {

    public static List<Integer> getMarkPositions(String string) {
        ArrayList<Integer> result = new ArrayList<>();

        result.add(0);

        int startPos = 0;
        int spacePos;
        while((spacePos = string.indexOf(' ', startPos)) != -1) {
            startPos = spacePos+1;
            result.add(spacePos+1);
        }
        result.add(string.length());

        // merge all successive positions into one:
        // out of successive spaces only last one will be used as pos
        ArrayList<Integer> posToRemove = new ArrayList<Integer>();
        int size = result.size();
        for(int i=0; i<size; i++) {
            int pos = result.get(i);
            if(i+1 < size && pos != 0 && result.get(i+1) == pos+1) {
                posToRemove.add(pos);
            }
        }
        result.removeAll(posToRemove);

        return result;
    }

}
