package org.mcsearch.mapper;

import org.apache.commons.lang3.tuple.Pair;
import org.mcsearch.utils.DateUtils;
import org.mcsearch.utils.FileUtils;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Map;
import java.util.logging.Logger;

public class WordToByteMap {
    private static final String INDEX_FOLDER_PATH = "./index/";
    private static final String BYTE_MAP_FILE_PATH = INDEX_FOLDER_PATH + "WORD_TO_BYTE_MAP";
    private static final String INVERTED_INDEX_FILE_PATH = INDEX_FOLDER_PATH + "INV_INDEX";
    private static final long INTEGER_MAX_AS_LONG = Integer.MAX_VALUE;
    private static final Pair<Long,Integer> DEFAULT_VALUE_FROM_BYTE_MAP = Pair.of(-1L, -1);
    private static final Logger logger = Logger.getLogger(WordToByteMap.class.toString());

    private static Map<String, Pair<Long, Integer>> BYTE_MAP;


    public static boolean loadMap() {
        boolean filesExist = FileUtils.fileExists(BYTE_MAP_FILE_PATH) && FileUtils.fileExists(INVERTED_INDEX_FILE_PATH);

        if(filesExist) {
            long start = DateUtils.getCurrentTime();
            BYTE_MAP = FileUtils.readObjectFromFile(BYTE_MAP_FILE_PATH);
            logger.info("Byte map load took " + (DateUtils.getTimeDiffFromNow(start)) + "ms");
        }

        return filesExist;
    }

    private static String read(Pair<Long, Integer> offsetLimit) throws IOException {
        if(DEFAULT_VALUE_FROM_BYTE_MAP.equals(offsetLimit)) return "";

        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(INVERTED_INDEX_FILE_PATH));

        long offset = offsetLimit.getKey();
        int limit = offsetLimit.getValue();

        while(offset > INTEGER_MAX_AS_LONG) {
            dataInputStream.skipBytes(Integer.MAX_VALUE);
            offset -= Integer.MAX_VALUE;
        }

        dataInputStream.skipBytes(Math.toIntExact(offset));

        byte[] data = new byte[limit];
        dataInputStream.read(data);

        dataInputStream.close();

        return new String(data);
    }

    public static String readInvertedIndex(String word) {
        try {
            return read(BYTE_MAP.getOrDefault(word, DEFAULT_VALUE_FROM_BYTE_MAP));
        } catch (IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }
}
