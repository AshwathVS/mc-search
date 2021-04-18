package org.mcsearch.controller;

import org.mcsearch.search.IndexedWordData;
import org.mcsearch.search.QueryHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class QueryController {

    @GetMapping("/fetch-search-result")
    public QueryAPIResponse fetchSearchResult(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "itemsPerPage", defaultValue = "10") Integer itemsPerPage) {

        long start = new Date().getTime();
        IndexedWordData.QueryResult result = QueryHandler.fetchQueryResults(query, --pageNumber * itemsPerPage, itemsPerPage);
        long end = new Date().getTime();

        return new QueryAPIResponse(result.getDocumentResults(), result.getTotalResultsFound(), end - start);
    }
}
