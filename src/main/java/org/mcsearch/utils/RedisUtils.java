package org.mcsearch.utils;

import redis.clients.jedis.Jedis;

import java.util.Set;

public class RedisUtils {
    private static final Jedis jedis = new Jedis("localhost");
    public static final String INVALIDATION_DOCUMENT_SET_KEY = "INVALIDATION_DOC_SET";

    public static void set(String key, String value) {
        jedis.set(key, value);
    }

    public static String get(String key) {
        return jedis.get(key);
    }

    public static Set<String> getInvalidatedDocuments() {
        return jedis.smembers(INVALIDATION_DOCUMENT_SET_KEY);
    }

    public static boolean isDocInvalidated(String docHash) {
        return jedis.sismember(INVALIDATION_DOCUMENT_SET_KEY, docHash);
    }

    public static void addToSet(String key, String... values) {
        jedis.sadd(key, values);
    }
}
