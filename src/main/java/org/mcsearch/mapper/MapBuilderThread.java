package org.mcsearch.mapper;

import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.logging.Logger;

public class MapBuilderThread implements Runnable {
    private static final Logger logger = Logger.getLogger(MapBuilderThread.class.getName());

    private String filePath;
    private Map<String, Pair<String, Integer>> wordVsFileLineOccurrence;

    public MapBuilderThread(String filePath, Map<String, Pair<String, Integer>> wordVsFileLineOccurrence) {
        this.filePath = filePath;
        this.wordVsFileLineOccurrence = wordVsFileLineOccurrence;
    }

    @Override
    public void run() {
        File file = new File(filePath);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = null;
            int lineNumber = 1;

            while ((line = br.readLine()) != null) {
                String word = line.split("\t")[0];
                wordVsFileLineOccurrence.put(word, Pair.of(file.getName(), lineNumber));
                lineNumber++;
            }
            logger.info(lineNumber + " number of lines read from " + file.getAbsoluteFile());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
