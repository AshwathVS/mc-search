package org.mcsearch.search;

import org.springframework.util.CollectionUtils;
import utils.RedisUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class IndexedWordData {
    private String word;
    private HashMap<String, IndexedDocumentData> indexedDocumentData;

    public IndexedWordData(String word, HashMap<String, IndexedDocumentData> indexedDocumentData) {
        this.word = word;
        this.indexedDocumentData = indexedDocumentData;
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

    public static class IndexedDocumentData {
        private String documentHash;
        private int domainRank;
        private Date publishedDate;
        private List<Integer> occurrenceIndexes;

        public IndexedDocumentData(String documentHash, int domainRank, Date publishedDate, List<Integer> occurrenceIndexes) {
            this.documentHash = documentHash;
            this.domainRank = domainRank;
            this.publishedDate = publishedDate;
            this.occurrenceIndexes = occurrenceIndexes;
        }

        public String getDocumentHash() {
            return documentHash;
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
    }

    public static class DocumentResult implements Comparable {
        private String documentHash;
        private String documentLink;
        private Date publishedDate;
        private int domainRank;

        public DocumentResult(IndexedDocumentData indexedDocumentData) {
            this.documentHash = indexedDocumentData.documentHash;
            this.documentLink = RedisUtils.get(this.documentHash);
            this.publishedDate = indexedDocumentData.publishedDate;
            this.domainRank = indexedDocumentData.domainRank;
        }

        public String getDocumentHash() {
            return documentHash;
        }

        public String getDocumentLink() {
            return documentLink;
        }

        public Date getPublishedDate() {
            return publishedDate;
        }

        public int getDomainRank() {
            return domainRank;
        }

        @Override
        public int compareTo(Object o) {
            DocumentResult dr = (DocumentResult) o;

            if(dr.domainRank == this.domainRank) {
                return publishedDate.compareTo(dr.publishedDate);
            } else {
                return dr.domainRank - this.domainRank;
            }
        }
    }
}
