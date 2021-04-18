package org.mcsearch.search;

import org.springframework.util.CollectionUtils;
import org.mcsearch.utils.BinarySearchUtils;
import org.mcsearch.utils.StopWords;

import java.util.*;

public class QueryHandler {
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

    public static List<IndexedWordData.DocumentResult> fetchQueryResults(String query, int offset, int fetch) {
        List<IndexedWordData.DocumentResult> queryResultList = fetchQueryResults(query);

        if(queryResultList.size() < offset) return Arrays.asList();
        else {
            return queryResultList.subList(offset, Math.min(offset + fetch, queryResultList.size()));
        }
    }

    public static List<IndexedWordData.DocumentResult> fetchQueryResults(String query) {
        query = query.toLowerCase();

        QueryTokenizer.QueryTokenizedResult tokenizedResult = QueryTokenizer.tokenizeQuery(query);
        boolean isStrict = tokenizedResult.isStrict();

        if(CollectionUtils.isEmpty(tokenizedResult.getTokens())) {
            return List.of();
        } else {
            List<IndexedWordData.DocumentResult> queryResult = new ArrayList<>();
            List<IndexedWordData> indexedWordDataList = new ArrayList<>(tokenizedResult.getTokenCount());

            for(String token : tokenizedResult.getTokens()) {
                if (!StopWords.isStopWord(token)) {
                    indexedWordDataList.add(IndexedDataParser.parseInvertedIndexForWord(token));
                }
            }

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

            Collections.sort(queryResult);
            return queryResult;
        }
    }
}
