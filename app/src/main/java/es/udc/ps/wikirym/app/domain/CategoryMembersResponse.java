package es.udc.ps.wikirym.app.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryMembersResponse {

    @SerializedName("query")
    private Query mQuery;

    public CategoryMembersResponse(Query query) {

        mQuery = query;
    }

    public Query getQuery() {

        return mQuery;
    }

    public static class Query {

        @SerializedName("categorymembers")
        private List<WikiEntry> mCategoryMembers;

        public Query(List<WikiEntry> categoryMembers) {

            mCategoryMembers = categoryMembers;
        }

        public List<WikiEntry> getCategoryMembers() {

            return mCategoryMembers;
        }
    }
}
