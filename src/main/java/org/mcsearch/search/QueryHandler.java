package org.mcsearch.search;

import org.mcsearch.cache.PostingListCacheLayer;
import org.mcsearch.utils.DateUtils;
import org.springframework.util.CollectionUtils;
import org.mcsearch.utils.BinarySearchUtils;
import org.mcsearch.utils.StopWords;

import java.util.*;
import java.util.logging.Logger;

public class QueryHandler {
    private static final Logger logger = Logger.getLogger(QueryHandler.class.getName());

    private static List<IndexedWordData.IndexedDocumentData> checkDocumentIntersectionAndPositionalIndexCheck(List<IndexedWordData> indexedWordDataList, int requiredDocumentSize, boolean isStrict) {
        List<IndexedWordData.IndexedDocumentData> queryResult = new ArrayList<>();

        IndexedWordData indexedWordDataWithLeastDocCount = indexedWordDataList.get(0);
        for(IndexedWordData indexedWordData: indexedWordDataList) {
            if(indexedWordData.getDocCount() < indexedWordDataWithLeastDocCount.getDocCount()) indexedWordDataWithLeastDocCount = indexedWordData;
        }

        for(Map.Entry<String, IndexedWordData.IndexedDocumentData> document : indexedWordDataWithLeastDocCount.getIndexedDocumentData().entrySet()) {
            boolean docPresentInAllWords = true;
            String documentHash = document.getKey();

            for(IndexedWordData indexedWordData : indexedWordDataList) {
                docPresentInAllWords = indexedWordData.isDocumentPresent(documentHash);

                if(!docPresentInAllWords) break;
            }

            if(docPresentInAllWords) {
                List<IndexedWordData.IndexedDocumentData> documentSpecificIndexedWordData = new ArrayList<>();

                for(IndexedWordData iwd : indexedWordDataList) {
                    documentSpecificIndexedWordData.add(iwd.getIndexedDocumentData().get(documentHash));
                }

                if(checkIndexOccurrences(documentSpecificIndexedWordData, isStrict)) {
                    queryResult.add(documentSpecificIndexedWordData.get(0));
                }
            }
        }


//        Map<String, Integer> occurrences = new HashMap<>();
//        for(IndexedWordData indexedWordData : indexedWordDataList) {
//            for (String documentHash : indexedWordData.getIndexedDocumentData().keySet()) {
//                if(occurrences.containsKey(documentHash)) {
//                    occurrences.put(documentHash, occurrences.get(documentHash) + 1);
//                } else {
//                    occurrences.put(documentHash, 1);
//                }
//
//                if(occurrences.get(documentHash) == requiredDocumentSize) {
//                    List<IndexedWordData.IndexedDocumentData> documentSpecificIndexedWordData = new ArrayList<>();
//
//                    for(IndexedWordData iwd : indexedWordDataList) {
//                        documentSpecificIndexedWordData.add(iwd.getIndexedDocumentData().get(documentHash));
//                    }
//
//                    if(checkIndexOccurrences(documentSpecificIndexedWordData, isStrict)) {
//                        queryResult.add(documentSpecificIndexedWordData.get(0));
//                    }
//                }
//            }
//        }

        return queryResult;
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

            overallResult = intermediateResult;

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
            else return true;
        }
    }

    public static IndexedWordData.QueryResult fetchQueryResults(String query, int offset, int fetch) {
        List<IndexedWordData.IndexedDocumentData> queryResultList = fetchQueryResults(query);

        if(queryResultList.size() < offset) return null;
        else {
            List<IndexedWordData.IndexedDocumentData> subQueryList = queryResultList.subList(offset, Math.min(offset + fetch, queryResultList.size()));
            return new IndexedWordData.QueryResult(subQueryList, queryResultList.size());
        }
    }

    public static List<IndexedWordData.IndexedDocumentData> fetchQueryResults(String query) {
        query = query.toLowerCase();

        QueryTokenizer.QueryTokenizedResult tokenizedResult = QueryTokenizer.tokenizeQuery(query);
        boolean isStrict = tokenizedResult.isStrict();

        if(CollectionUtils.isEmpty(tokenizedResult.getTokens())) {
            logger.info("No results found for <" + query + ">");
            return List.of();
        } else {
            List<IndexedWordData> indexedWordDataList = new ArrayList<>(tokenizedResult.getTokenCount());

            long calculationsTimeTaken;

            for(String token : tokenizedResult.getTokens()) {
                if (!StopWords.isStopWord(token)) {
                    IndexedWordData indexedWordData = PostingListCacheLayer.get(token);
                    if(null == indexedWordData) return Collections.emptyList();
                    indexedWordDataList.add(indexedWordData);
                }
            }

            long start = DateUtils.getCurrentTime();

            List<IndexedWordData.IndexedDocumentData> queryResult = checkDocumentIntersectionAndPositionalIndexCheck(indexedWordDataList, indexedWordDataList.size(), isStrict);

            calculationsTimeTaken = DateUtils.getTimeDiffFromNow(start);
            logTimeStats(query, calculationsTimeTaken);

            long sortTimeStart = DateUtils.getCurrentTime();
            Collections.sort(queryResult);
            logger.info("Sorting took: " + DateUtils.getTimeDiffFromNow(sortTimeStart) + "ms");

            return queryResult;
        }
    }

    private static void logTimeStats(String query, long calculationsTimeTaken) {
        logger.info(
                "\n" +
                        "Stats for <" + query + ">" +
                        "\n" +
                        "Post parse calculations took " + calculationsTimeTaken + "ms"
        );
    }
}
