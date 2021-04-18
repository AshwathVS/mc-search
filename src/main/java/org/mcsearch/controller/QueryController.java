package org.mcsearch.controller;

import org.mcsearch.search.IndexedWordData;
import org.mcsearch.search.QueryHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class QueryController {

    @GetMapping("/fetch-search-result")
    public List<IndexedWordData.DocumentResult> fetchSearchResult(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "itemsPerPage", defaultValue = "10") Integer itemsPerPage) {
        return QueryHandler.fetchQueryResults(query, --pageNumber * itemsPerPage, itemsPerPage);
    }

}
