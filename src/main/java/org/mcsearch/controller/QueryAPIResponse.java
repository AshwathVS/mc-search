package org.mcsearch.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.mcsearch.search.IndexedWordData;

import java.util.List;

public class QueryAPIResponse {
    private List<IndexedWordData.DocumentResult> queryResults;
    private int totalResultsFound;
    private long milliSecondsTaken;

    public QueryAPIResponse(List<IndexedWordData.DocumentResult> queryResults, int totalResultsFound, long milliSecondsTaken) {
        this.queryResults = queryResults;
        this.totalResultsFound = totalResultsFound;
        this.milliSecondsTaken = milliSecondsTaken;
    }

    @JsonProperty("results")
    public List<IndexedWordData.DocumentResult> getQueryResults() {
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
