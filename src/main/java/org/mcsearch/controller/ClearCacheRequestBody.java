package org.mcsearch.controller;

import java.util.List;

public class ClearCacheRequestBody {
    private List<String> wordsToClearFromCache;

    public List<String> getWordsToClearFromCache() {
        return this.wordsToClearFromCache;
    }

    public void setWordsToClearFromCache(List<String> wordsToClearFromCache) {
        this.wordsToClearFromCache = wordsToClearFromCache;
    }
}
