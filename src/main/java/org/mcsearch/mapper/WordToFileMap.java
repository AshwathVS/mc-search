package org.mcsearch.mapper;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;

public class WordToFileMap {
    private static Map<String, Pair<String, Integer>> WORD_FILE_LINE_MAP = null;

    public static boolean buildMap() {
        WORD_FILE_LINE_MAP = WordToFileMapBuilder.buildWordToFileMapping();
        return WORD_FILE_LINE_MAP != null;
    }

    public static Pair<String, Integer> getWordToFileLineInfo(String word) {
        return WORD_FILE_LINE_MAP.getOrDefault(word, null);
    }
}
