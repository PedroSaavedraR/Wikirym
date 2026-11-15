package es.udc.ps.wikirym.app.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {

    @SerializedName("query")
    private Query mQuery;

    public SearchResponse(Query query) {

        mQuery = query;
    }

    public Query getQuery() {

        return mQuery;
    }

    public static class Query {

        @SerializedName("search")
        private List<WikiEntry> mSearch;

        public Query(List<WikiEntry> search) {

            mSearch = search;
        }

        public List<WikiEntry> getSearch() {

            return mSearch;
        }
    }
}
