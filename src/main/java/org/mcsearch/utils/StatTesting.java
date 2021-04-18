package org.mcsearch.utils;

import org.mcsearch.mapper.WordToFileMap;
import org.mcsearch.search.IndexedWordData;
import org.mcsearch.search.QueryHandler;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StatTesting {
    private static int randomNumber(int min, int max) {
        return (int)(Math.random() * (max - min + 1) + min);
    }

    private static void performReadTimeStats() {
        String path = "/home/ashwath/Documents/MapReduce Project/code/mc-search/index/";

        File[] filesInPath = new File(path).listFiles();
        String[] filenames = new String[filesInPath.length];

        for(int i=0; i<filesInPath.length; i++) filenames[i] = filesInPath[i].getAbsolutePath();

        long totalAvg = 0;
        for(int m=0; m<100; m++) {
            int min = 0, max = 5000;
            long sum = 0;

            for(int i=min; i<=1000; i++) {
                int line = randomNumber(min, max);
                String file = filenames[randomNumber(0, filenames.length-1)];
                Date start = new Date();
                FileUtils.readNthLine(path + file, line);
                Date end = new Date();
                long timeDiff = (end.getTime() - start.getTime());
//                System.out.println("Time taken reading for " + line + "th line: " + timeDiff + "ms");
                sum += timeDiff;
            }

            System.out.println("Avg for run " + m + ": " + (sum / 1000));
            totalAvg += (sum / 1000);
        }
        System.out.println("Total Avg: " + totalAvg);
    }

    private static void performSearchChecks() {
        for(IndexedWordData.DocumentResult documentResult : QueryHandler.fetchQueryResults("news")) {
            System.out.println(documentResult.getDocumentLink());
        }
    }

    public static void main(String[] args) throws Exception {
        WordToFileMap.buildMap();
//        performReadTimeStats();
        performSearchChecks();
    }
}
