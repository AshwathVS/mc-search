package org.mcsearch.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.mcsearch.search.IndexedWordData;
import org.mcsearch.search.QueryHandler;

import java.io.*;
import java.util.*;

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
        for(IndexedWordData.IndexedDocumentData documentResult : QueryHandler.fetchQueryResults("news")) {
            System.out.println(documentResult.getDocumentUrl());
        }
    }

    private static String read(Long offset, int limit) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream("./index/base"));
        while(offset > (long) Integer.MAX_VALUE) {
            dataInputStream.skipBytes(Integer.MAX_VALUE);
            offset -= Integer.MAX_VALUE;
        }

        dataInputStream.skipBytes(Math.toIntExact(offset));

        byte[] data = new byte[limit];

        int bytesRead = dataInputStream.read(data);
        return new String(data);
    }

    private static void performByteBasedReadTimeStats() throws IOException {
        Map<String, Pair<Long, Integer>> wordToByteOffsetAndLimitMapping = FileUtils.readObjectFromFile("./index/WORD_TO_BYTE_DATA");
        List<String> keysAsArray = new ArrayList(wordToByteOffsetAndLimitMapping.keySet());
        Collections.sort(keysAsArray);

        int max = keysAsArray.size() - 1;
        long avg = 0;
        int iterations = 100000;

        for (int i=0; i<iterations; i++) {
            String randomKey = keysAsArray.get(randomNumber(0, max));
            long start = DateUtils.getCurrentTime();
            Pair<Long, Integer> offsetAndLimit = wordToByteOffsetAndLimitMapping.get(randomKey);
            String line = read(offsetAndLimit.getKey(), offsetAndLimit.getValue());
//            System.out.println(line);
            long timeTaken = DateUtils.getTimeDiffFromNow(start);
            if(line.indexOf("\t") == -1) System.out.println("EXCEPTION!!!!!");
            avg += timeTaken;

            if(i != 0 && i % 10000 == 0) {
                System.out.println(i + "th iteration completed");
                System.out.println("Current avg: " + avg/i);
            }
//            System.out.println("Reading " + randomKey + " took " + timeTaken + "ms");
        }

        System.out.println("Average Time: " + avg / iterations + "ms");
    }

    public static void performSetReadInRedis() {
        long start = DateUtils.getCurrentTime();

        for (int i = 0; i < 1; i++) {
            RedisUtils.getInvalidatedDocuments();
        }
        System.out.println(DateUtils.getTimeDiffFromNow(start));
    }

    public static void main(String[] args) throws Exception {
//        WordToFileMap.buildMap();
//        performReadTimeStats();
//        performSearchChecks();
//        performByteBasedReadTimeStats();
//        performSetReadInRedis();
    }
}
