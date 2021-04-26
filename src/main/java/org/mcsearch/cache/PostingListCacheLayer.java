package org.mcsearch.cache;

import org.mcsearch.search.IndexedDataParser;
import org.mcsearch.search.IndexedWordData;

import java.util.logging.Logger;

public class PostingListCacheLayer {
    private static final Logger logger = Logger.getLogger(PostingListCacheLayer.class.toString());

    private static long cacheHits = 0L;
    private static long totalCacheCalls = 0L;

    private static ICache<String, IndexedWordData> postingListCache = new LRUCache<>(50);

    public static IndexedWordData get(String word) {
        totalCacheCalls++;
        if(postingListCache.containsKey(word)) {
            logger.info("Fetching results from cache for: " + word);
            cacheHits++;
            return postingListCache.get(word);
        } else {
            IndexedWordData indexedWordData = IndexedDataParser.getParsedInvertedIndex(word);

            if(null != indexedWordData) postingListCache.put(word, indexedWordData);

            return indexedWordData;
        }
    }

    public static void printCacheStats() {
        logger.info("Cache hits: " + cacheHits
                + "\n" +
                "Total Cache Calls: " + totalCacheCalls
                + "\n" +
                "Cache Hit Percentage: " + ((cacheHits/totalCacheCalls) * 100)
        );
    }
}
