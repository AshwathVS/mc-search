package utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static List<Integer> convertIntegerListString(String integerList, String delimiter) {
        List<Integer> res = new ArrayList<>();
        for (String sInteger : integerList.split(delimiter)) res.add(Integer.parseInt(sInteger));
        return res;
    }
}
