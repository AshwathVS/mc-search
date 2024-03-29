package org.mcsearch.controller;

import org.mcsearch.search.IndexedWordData;
import org.mcsearch.search.QueryHandler;
import org.mcsearch.utils.DateUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QueryController {

    @GetMapping("/search")
    public QueryAPIResponse fetchSearchResult(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNumber,
            @RequestParam(value = "itemsPerPage", defaultValue = "10") Integer itemsPerPage) {

        long start = DateUtils.getCurrentTime();
        IndexedWordData.QueryResult result = QueryHandler.fetchQueryResults(query, --pageNumber * itemsPerPage, itemsPerPage);
        long timeTaken = DateUtils.getTimeDiffFromNow(start);

        if(null == result) {
            return QueryAPIResponse.getEmptyResponse(timeTaken);
        } else {
            return new QueryAPIResponse(result.getDocumentResults(), result.getTotalResultsFound(), timeTaken);
        }
    }
}
