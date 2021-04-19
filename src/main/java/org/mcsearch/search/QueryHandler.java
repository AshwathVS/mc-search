package org.mcsearch.search;

import org.mcsearch.utils.DateUtils;
import org.springframework.util.CollectionUtils;
import org.mcsearch.utils.BinarySearchUtils;
import org.mcsearch.utils.StopWords;

import java.util.*;
import java.util.logging.Logger;

public class QueryHandler {
    private static final Logger logger = Logger.getLogger(QueryHandler.class.getName());

    private static Map<String, Integer> countOccurrences(List<IndexedWordData> indexedWordDataList) {
        Map<String, Integer> occurrences = new HashMap<>();
        for(IndexedWordData indexedWordData : indexedWordDataList) {
            for (String documentHash : indexedWordData.getIndexedDocumentData().keySet()) {
                if(occurrences.containsKey(documentHash)) {
                    occurrences.put(documentHash, occurrences.get(documentHash) + 1);
                } else {
                    occurrences.put(documentHash, 1);
                }
            }
        }

        return occurrences;
    }

    private static boolean performStrictPositionalIndexCheck(List<IndexedWordData.IndexedDocumentData> indexedDocumentDataList) {
        boolean overallResult = false;
        for(int i=0; i<indexedDocumentDataList.get(0).getOccurrenceIndexes().size() && !overallResult; i++) {
            Integer baseValue = indexedDocumentDataList.get(0).getOccurrenceIndexes().get(i);
            boolean intermediateResult = true;

            for(int j=1; j<indexedDocumentDataList.size(); j++) {
                baseValue = BinarySearchUtils.findNextConsecutiveNumber(indexedDocumentDataList.get(j).getOccurrenceIndexes(), baseValue);

                if(baseValue == null) {
                    intermediateResult = false;
                    break;
                }
            }

            overallResult |= intermediateResult;

        }
        return overallResult;
    }

    private static boolean performPositionalIndexCheck(List<IndexedWordData.IndexedDocumentData> indexedDocumentDataList) {
        Integer baseValue = indexedDocumentDataList.get(0).getOccurrenceIndexes().get(0);

        for(int i=1; i<indexedDocumentDataList.size(); i++) {
            baseValue = BinarySearchUtils.findMinimumNumberGreaterThan(indexedDocumentDataList.get(i).getOccurrenceIndexes(), baseValue);

            if(baseValue == null) return false;
        }

        return true;
    }

    private static boolean checkIndexOccurrences(List<IndexedWordData.IndexedDocumentData> indexedWordDataList, boolean isStrict) {
        if (indexedWordDataList.size() == 1) return true;
        else {
            if (isStrict) return performStrictPositionalIndexCheck(indexedWordDataList);
            else return performPositionalIndexCheck(indexedWordDataList);
        }
    }

    public static IndexedWordData.QueryResult fetchQueryResults(String query, int offset, int fetch) {
        List<IndexedWordData.DocumentResult> queryResultList = fetchQueryResults(query);

        if(queryResultList.size() < offset) return null;
        else {
            List<IndexedWordData.DocumentResult> subQueryList = queryResultList.subList(offset, Math.min(offset + fetch, queryResultList.size()));
            return new IndexedWordData.QueryResult(subQueryList, queryResultList.size());
        }
    }

    public static List<IndexedWordData.DocumentResult> fetchQueryResults(String query) {
        query = query.toLowerCase();

        QueryTokenizer.QueryTokenizedResult tokenizedResult = QueryTokenizer.tokenizeQuery(query);
        boolean isStrict = tokenizedResult.isStrict();

        if(CollectionUtils.isEmpty(tokenizedResult.getTokens())) {
            logger.info("No results found for <" + query + ">");
            return List.of();
        } else {
            List<IndexedWordData.DocumentResult> queryResult = new ArrayList<>();
            List<IndexedWordData> indexedWordDataList = new ArrayList<>(tokenizedResult.getTokenCount());

            long parseTimeTaken, calculationsTimeTaken;

            long start = DateUtils.getCurrentTime();
            for(String token : tokenizedResult.getTokens()) {
                if (!StopWords.isStopWord(token)) {
                    IndexedWordData indexedWordData = IndexedDataParser.parseInvertedIndexForWord(token);
                    if(null != indexedWordData) {
                        indexedWordDataList.add(indexedWordData);
                    }
                }
            }

            parseTimeTaken = DateUtils.getTimeDiffFromNow(start);
            start = DateUtils.getCurrentTime();

            Map<String, Integer> documentVsOccurrenceCount = countOccurrences(indexedWordDataList);
            int requiredDocumentOccurrenceCount = indexedWordDataList.size();

            for(Map.Entry<String, Integer> docVsOccCountEntry : documentVsOccurrenceCount.entrySet()) {
                if(requiredDocumentOccurrenceCount == docVsOccCountEntry.getValue()) {
                    String docHash = docVsOccCountEntry.getKey();
                    List<IndexedWordData.IndexedDocumentData> documentSpecificIndexedWordData = new ArrayList<>();

                    for(IndexedWordData iwd : indexedWordDataList) {
                        documentSpecificIndexedWordData.add(iwd.getIndexedDocumentData().get(docHash));
                    }

                    if(checkIndexOccurrences(documentSpecificIndexedWordData, isStrict)) {
                        queryResult.add(new IndexedWordData.DocumentResult(documentSpecificIndexedWordData.get(0)));
                    }
                }
            }

            calculationsTimeTaken = DateUtils.getTimeDiffFromNow(start);

            logTimeStats(query, parseTimeTaken, calculationsTimeTaken);

            Collections.sort(queryResult);

            return queryResult;
        }
    }

    private static void logTimeStats(String query, long parseTimeTaken, long calculationsTimeTaken) {
        logger.info(
                "\n" +
                        "Stats for <" + query + ">" +
                        "\n" +
                        "Reading and parsing inverted index file took " + parseTimeTaken + "ms" +
                        "\n" +
                        "Post parse calculations took " + calculationsTimeTaken + "ms"
        );
    }
}
