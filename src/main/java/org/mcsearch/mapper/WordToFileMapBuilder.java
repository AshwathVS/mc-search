package org.mcsearch.mapper;

import org.apache.commons.lang3.tuple.Pair;
import utils.FileUtils;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class WordToFileMapBuilder {
    public static final String INDEX_FOLDER = "./index/";
    public static final String CACHE_FOLDER = "./cache/";
    private static final String WORD_FILE_LINE_MAPPING_FILE_NAME = "WORD_TO_FILE_LINE_MAPPING";
    private static final String WORD_FILE_LINE_MAPPING_FILE_PATH = CACHE_FOLDER + WORD_FILE_LINE_MAPPING_FILE_NAME;
    private static final Logger logger = Logger.getLogger(WordToFileMapBuilder.class.getName());

    private static Map<String, Pair<String, Integer>> fetchWordVsFileLineOccurrence(String path) throws Exception {
        File dir = new File(path);
        if (!dir.isDirectory()) {
            throw new Exception("Mentioned folder is invalid!");
        }

        // get all files
        File[] files = dir.listFiles();

        Map<String, Pair<String, Integer>> concurrentMap = new ConcurrentHashMap<>();
        List<MapBuilderThread> allRunnableTasks = new ArrayList<>();

        // generate all the runnable tasks (one per each file)
        for (File file : files) {
            allRunnableTasks.add(new MapBuilderThread(file.getAbsolutePath(), concurrentMap));
        }

        // invoke all the runnable tasks so that they can run in parallel
        ExecutorService executorService = Executors.newFixedThreadPool(allRunnableTasks.size());
        CompletableFuture<?>[] futures = allRunnableTasks.stream()
                .map(task -> CompletableFuture.runAsync(task, executorService))
                .toArray(CompletableFuture[]::new);

        // wait for all the tasks to complete
        CompletableFuture.allOf(futures).join();
        executorService.shutdownNow();
        return concurrentMap;
    }

    private static Map<String, Pair<String, Integer>> buildAndPersist() throws Exception {
        Map<String, Pair<String, Integer>> wordToFileLineMapping = fetchWordVsFileLineOccurrence(INDEX_FOLDER);

        // check if cache folder exists and create one if not
        FileUtils.createFolder(CACHE_FOLDER);

        // persist map object to local
        FileUtils.writeToFile(WORD_FILE_LINE_MAPPING_FILE_PATH, wordToFileLineMapping);

        return wordToFileLineMapping;
    }

    private static Map<String, Pair<String, Integer>> loadMappingFromLocalFile() {
        return FileUtils.readFromFile(WORD_FILE_LINE_MAPPING_FILE_PATH);
    }

    public static Map<String, Pair<String, Integer>> buildWordToFileMapping() {
        try {
            Map<String, Pair<String, Integer>> wordToFileLineMap = null;
            // check if cache folder exists and load from local if file exists
            if (FileUtils.folderExists(CACHE_FOLDER) && FileUtils.fileExists(WORD_FILE_LINE_MAPPING_FILE_PATH)) {
                logger.info("Cached mapping exists, loading mapping from cache");
                wordToFileLineMap = loadMappingFromLocalFile();
            } else {
                // since the cache does not exist, we build the mapping again
                logger.info("Unable to find cached mapping, building the mapping from scratch");
                wordToFileLineMap = buildAndPersist();
            }
            return wordToFileLineMap;
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.severe("Unable to build mapping due to exceptions");
            return null;
        }
    }

    public static void main(String[] args) throws Exception {

        String fileToPersistMap = "/home/ashwath/Desktop/mapp";

        Date start = new Date();
//        fetchWordVsFileLineOccurrence("/home/ashwath/Documents/MapReduce Project/indexed_doc/splits"); // 60 seconds to map 1.4 gb file parallelly.
        Map<String, Pair<String, Integer>> readMap = FileUtils.readFromFile(fileToPersistMap); // 1 second to read the map
        Date end = new Date();
        System.out.println(TimeUnit.MILLISECONDS.toSeconds(end.getTime() - start.getTime()));

        /*
        // persist this map into a local file
        String fileToPersistMap = "/home/ashwath/Desktop/mapp";
        FileUtils.writeToFile(fileToPersistMap, concurrentMap);

        Map<String, Pair<String, Integer>> readMap = FileUtils.readFromFile(fileToPersistMap);
        System.out.println(fileToPersistMap.length());
        */
    }
}
