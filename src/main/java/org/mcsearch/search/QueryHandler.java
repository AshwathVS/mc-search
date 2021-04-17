package org.mcsearch.search;

import org.springframework.util.CollectionUtils;
import utils.StopWords;

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

    private static boolean checkIndexOccurrences(List<IndexedWordData.IndexedDocumentData> indexedWordDataList, boolean isStrict) {
        if(indexedWordDataList.size() == 1) return true;
        else return false;
    }

    public static List<IndexedWordData.DocumentResult> fetchQueryResults(String query) {
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

// 1565d11d79e86adde09fabb33407ee54b273f9f0f188a2e368b3e5fa219bdfd9@@@408@@@2018-01-26T20:50:00.000+02:00@@@1|12|23|34
// 1565d11d79e86adde09fabb33407ee54b273f9f0f188a2e368b3e5fa219bdfd9@@@408@@@2018-01-26T20:50:00.000+02:00@@@2|26|39|89

/*
1, 12, 23, 34
2, 9, 39, 89
*/
