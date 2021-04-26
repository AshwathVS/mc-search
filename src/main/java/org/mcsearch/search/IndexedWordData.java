package org.mcsearch.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.CollectionUtils;
import org.mcsearch.utils.RedisUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class IndexedWordData implements Serializable {
    private String word;
    private HashMap<String, IndexedDocumentData> indexedDocumentData;

    public IndexedWordData(String word, HashMap<String, IndexedDocumentData> indexedDocumentData) {
        this.word = word;
        this.indexedDocumentData = indexedDocumentData;
    }

    public IndexedWordData() {
    }

    public String getWord() {
        return word;
    }

    public HashMap<String, IndexedDocumentData> getIndexedDocumentData() {
        return indexedDocumentData;
    }

    public boolean isDocumentPresent(String documentHash) {
        return !CollectionUtils.isEmpty(this.indexedDocumentData) && this.indexedDocumentData.containsKey(documentHash);
    }

    @JsonIgnore
    public int getDocCount() {
        return this.indexedDocumentData == null ? 0 : this.indexedDocumentData.size();
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setIndexedDocumentData(HashMap<String, IndexedDocumentData> indexedDocumentData) {
        this.indexedDocumentData = indexedDocumentData;
    }

    public static class IndexedDocumentData implements Comparable, Serializable {
        private String documentUrl;
        private int domainRank;
        private Date publishedDate;
        @JsonIgnore
        private List<Integer> occurrenceIndexes;

        public IndexedDocumentData(String documentHash, int domainRank, Date publishedDate, List<Integer> occurrenceIndexes) {
            this.documentUrl = RedisUtils.get(documentHash);
            this.domainRank = domainRank;
            this.publishedDate = publishedDate;
            this.occurrenceIndexes = occurrenceIndexes;
        }

        public IndexedDocumentData() {
        }

        
        
        public int getDomainRank() {
            return domainRank;
        }

        public Date getPublishedDate() {
            return publishedDate;
        }

        public List<Integer> getOccurrenceIndexes() {
            return occurrenceIndexes;
        }

        public String getDocumentUrl() {
            return documentUrl;
        }

        public void setDocumentUrl(String documentUrl) {
            this.documentUrl = documentUrl;
        }

        public void setDomainRank(int domainRank) {
            this.domainRank = domainRank;
        }

        public void setPublishedDate(Date publishedDate) {
            this.publishedDate = publishedDate;
        }

        public void setOccurrenceIndexes(List<Integer> occurrenceIndexes) {
            this.occurrenceIndexes = occurrenceIndexes;
        }

        @Override
        public int compareTo(Object o) {
            IndexedDocumentData dr = (IndexedDocumentData) o;

            if(dr.domainRank == this.domainRank) {
                return dr.publishedDate.compareTo(publishedDate);
            } else {
                return this.domainRank - dr.domainRank;
            }
        }
    }

    public static class QueryResult {
        private List<IndexedDocumentData> documentResults;
        private int totalResultsFound;

        public QueryResult(List<IndexedDocumentData> documentResults, int totalResultsFound) {
            this.documentResults = documentResults;
            this.totalResultsFound = totalResultsFound;
        }

        public List<IndexedDocumentData> getDocumentResults() {
            return documentResults;
        }

        public int getTotalResultsFound() {
            return totalResultsFound;
        }
    }
}
