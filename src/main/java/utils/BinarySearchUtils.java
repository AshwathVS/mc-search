package utils;

import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public class BinarySearchUtils {
    public static Integer findMinimumNumberGreaterThan(List<Integer> array, int baseNumber) {
        if (!CollectionUtils.isEmpty(array)) {
            int left = 0, right = array.size() - 1;

            while (left <= right) {
                int mid = (left + right) / 2;
                int value = array.get(mid);

                if (value > baseNumber) {
                    if (mid > 0 && array.get(mid - 1) > baseNumber)  right = mid - 1;
                    else return value;
                } else {
                    left = mid + 1;
                }
            }
        }

        return null; // base result
    }

    public static Integer findNextConsecutiveNumber(List<Integer> array, int baseNumber) {
        if (!CollectionUtils.isEmpty(array)) {
            int left = 0, right = array.size() - 1, toFind = baseNumber + 1;

            while(left <= right) {
                int mid = (left + right) / 2;
                int value = array.get(mid);

                if (toFind == value) {
                  return value;
                } else if (toFind > value) left = mid + 1;
                else right = mid - 1;
            }
        }

        return null; // base result
    }

    public static void main(String[] args) {
        List<Integer> aa = Arrays.asList(1);
        System.out.println(findMinimumNumberGreaterThan(aa, 18));
    }
}
