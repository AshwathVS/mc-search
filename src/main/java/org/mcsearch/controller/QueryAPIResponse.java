package org.mcsearch.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mcsearch.search.IndexedWordData;

import java.util.Arrays;
import java.util.List;

public class QueryAPIResponse {
    public static QueryAPIResponse getEmptyResponse(long timeTaken) {
        return new QueryAPIResponse(Arrays.asList(), 0, timeTaken);
    }

    private List<IndexedWordData.IndexedDocumentData> queryResults;
    private int totalResultsFound;
    private long milliSecondsTaken;

    public QueryAPIResponse(List<IndexedWordData.IndexedDocumentData> queryResults, int totalResultsFound, long milliSecondsTaken) {
        this.queryResults = queryResults;
        this.totalResultsFound = totalResultsFound;
        this.milliSecondsTaken = milliSecondsTaken;
    }

    @JsonProperty("results")
    public List<IndexedWordData.IndexedDocumentData> getQueryResults() {
        return queryResults;
    }

    @JsonProperty("totalResults")
    public int getTotalResultsFound() {
        return totalResultsFound;
    }

    @JsonIgnore
    public long getMilliSecondsTaken() {
        return milliSecondsTaken;
    }

    @JsonProperty("millisecondsTaken")
    public String getMillisTakenAsString() {
        return milliSecondsTaken + "ms";
    }
}
