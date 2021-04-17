package org.mcsearch.search;

import org.apache.commons.lang3.tuple.Pair;
import org.mcsearch.mapper.WordToFileMap;
import org.mcsearch.mapper.WordToFileMapBuilder;
import utils.DateUtils;
import utils.FileUtils;
import utils.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

public class IndexedDataParser {
    private static final String DOCUMENT_DATA_DELIMITER = "@@@";
    private static final String INDEX_DELIMITER = ",";
    private static final String TAB_DELIMITER = "\t";
    private static final String PIPE_DELIMITER = "\\|";
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public static IndexedWordData parseInvertedIndexForWord(String word) {
        Pair<String, Integer> fileToLineInfo = WordToFileMap.getWordToFileLineInfo(word);

        if (null == fileToLineInfo) return null;
        else {
            String fileName = fileToLineInfo.getKey();
            int lineNumber = fileToLineInfo.getValue();

            String invertedIndex = FileUtils.readNthLine(WordToFileMapBuilder.INDEX_FOLDER + fileName, lineNumber);
            String[] documentDataList = invertedIndex.split(TAB_DELIMITER)[1].split(INDEX_DELIMITER);

            HashMap<String, IndexedWordData.IndexedDocumentData> indexedDocumentDataMap = new HashMap<>();

            for(String documentData : documentDataList) {
                String[] sDocumentData = documentData.split(DOCUMENT_DATA_DELIMITER);
                indexedDocumentDataMap.put(sDocumentData[0],
                        new IndexedWordData.IndexedDocumentData(
                                sDocumentData[0],
                                Integer.parseInt(sDocumentData[1]),
                                DateUtils.parseDate(sDocumentData[2], DATE_FORMAT),
                                StringUtils.convertIntegerListString(sDocumentData[3], PIPE_DELIMITER)
                        )
                );
            }

            return new IndexedWordData(word, indexedDocumentDataMap);
        }
    }

    public static void main(String[] args) {
        WordToFileMap.buildMap();
        IndexedWordData german = parseInvertedIndexForWord("Germany");
        IndexedWordData hello = parseInvertedIndexForWord("hello");
        IndexedWordData index = parseInvertedIndexForWord("index");
        IndexedWordData Index = parseInvertedIndexForWord("Index");
        System.out.println("BUBYEE");
    }
}
