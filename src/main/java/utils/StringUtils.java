package utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static List<Integer> convertIntegerListString(String integerList) {
        List<Integer> res = new ArrayList<>();
        for (String sInteger : integerList.split(",")) res.add(Integer.parseInt(sInteger));
        return res;
    }
}
