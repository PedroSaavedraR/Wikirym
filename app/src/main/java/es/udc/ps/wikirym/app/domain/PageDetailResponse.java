package es.udc.ps.wikirym.app.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PageDetailResponse {

    @SerializedName("query")
    private Query mQuery;

    public PageDetailResponse(Query query) {

        mQuery = query;
    }

    public Query getQuery() {

        return mQuery;
    }

    public static class Query {

        // One element for every requested page (here, always one)
        @SerializedName("pages")
        private List<PageDetail> mPages;

        public Query(List<PageDetail> pages) {

            mPages = pages;
        }

        public List<PageDetail> getPages() {

            return mPages;
        }
    }
}
