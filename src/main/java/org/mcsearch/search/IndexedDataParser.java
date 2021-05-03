package org.mcsearch.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mcsearch.mapper.WordToByteMap;
import org.mcsearch.utils.DateUtils;
import org.mcsearch.utils.FileUtils;
import org.mcsearch.utils.RedisUtils;
import org.mcsearch.utils.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.logging.Logger;


public class IndexedDataParser {
    private static final String DOCUMENT_DATA_DELIMITER = "@";
    private static final String INDEX_DELIMITER = ",";
    private static final String TAB_DELIMITER = "\t";
    private static final String PIPE_DELIMITER = "\\|";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static final Logger logger = Logger.getLogger(IndexedDataParser.class.toString());
    private static final String CACHE_WORD_PREFIX = "CACHE_";

    private static void iterateAndAddWordDataToList(String[] documentDataList, HashMap<String, IndexedWordData.IndexedDocumentData> indexedDocumentDataMap) {
        for (String documentData : documentDataList) {
            String[] sDocumentData = documentData.split(DOCUMENT_DATA_DELIMITER);
            if (!RedisUtils.isDocInvalidated(sDocumentData[0])) {
                indexedDocumentDataMap.put(sDocumentData[0],
                        new IndexedWordData.IndexedDocumentData(
                                sDocumentData[0],
                                Integer.parseInt(sDocumentData[1]),
                                DateUtils.parseDate(sDocumentData[2], DATE_FORMAT),
                                StringUtils.convertIntegerListString(sDocumentData[3], PIPE_DELIMITER)
                        )
                );
            }
        }
    }

    private static IndexedWordData parseInvertedIndexString(String invertedIndex, String cachedInvertedIndex, String word) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(invertedIndex)) return null;
        HashMap<String, IndexedWordData.IndexedDocumentData> indexedDocumentDataMap = new HashMap<>();

        String[] documentDataList = invertedIndex.split(TAB_DELIMITER)[1].split(INDEX_DELIMITER);
        iterateAndAddWordDataToList(documentDataList, indexedDocumentDataMap);

        if(!org.apache.commons.lang3.StringUtils.isEmpty(cachedInvertedIndex)) {
            String[] cachedDataList = cachedInvertedIndex.split(INDEX_DELIMITER);
            iterateAndAddWordDataToList(cachedDataList, indexedDocumentDataMap);
        }

        return new IndexedWordData(word, indexedDocumentDataMap);
    }

    public static IndexedWordData getParsedInvertedIndex(String word) {
        long start = DateUtils.getCurrentTime();
        String invertedIndexLine = WordToByteMap.readInvertedIndex(word);
        String cachedInvertedIndexLine = RedisUtils.get(CACHE_WORD_PREFIX + word);
        long readTime = DateUtils.getTimeDiffFromNow(start);

        start = DateUtils.getCurrentTime();
        IndexedWordData indexedWordData = parseInvertedIndexString(invertedIndexLine, cachedInvertedIndexLine, word);
        long parseTime = DateUtils.getTimeDiffFromNow(start);

        logger.info("Stats for <" + word + ">: Read (" + readTime + "ms), Parse (" + parseTime + "ms)");

        return indexedWordData;
    }

    private static void convertToJSON(String jsonString) throws JsonProcessingException {
        int sum = 0;
        int iter = 1;
        ObjectMapper objectMapper = new ObjectMapper();
        for(int i=0; i<iter; i++) {
            long start = DateUtils.getCurrentTime();
            IndexedWordData indexedWordData = objectMapper.readValue(jsonString, IndexedWordData.class);
            sum += DateUtils.getTimeDiffFromNow(start);
        }
        System.out.println("Json conversion: " + sum / iter + "ms");
    }

    public static void main(String[] args) throws Exception {
        WordToByteMap.loadMap();
        IndexedWordData german = getParsedInvertedIndex("Germany");
        IndexedWordData hello = getParsedInvertedIndex("news");
        IndexedWordData Index = getParsedInvertedIndex("Index");

        FileUtils.writeObjectToFile("./cache/tmp", hello);

        for(int i=0; i<10; i++) {
            convertToJSON(new ObjectMapper().writeValueAsString(hello));
        }
    }
}
