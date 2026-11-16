package es.udc.ps.wikirym.app.domain;

import com.google.gson.annotations.SerializedName;

public class PageDetail {

    @SerializedName("pageid")
    private int mPageId;

    @SerializedName("title")
    private String mTitle;

    // Plain text of the introduction of the page (prop=extracts&exintro=1&explaintext=1)
    @SerializedName("extract")
    private String mExtract;

    // Main image of the page (prop=pageimages&piprop=thumbnail); null if the page has none
    @SerializedName("thumbnail")
    private Thumbnail mThumbnail;

    // true when no page has the requested id
    @SerializedName("missing")
    private boolean mMissing;

    public PageDetail(int pageId,
                      String title,
                      String extract,
                      Thumbnail thumbnail,
                      boolean missing) {

        mPageId = pageId;
        mTitle = title;
        mExtract = extract;
        mThumbnail = thumbnail;
        mMissing = missing;
    }

    public int getPageId() {

        return mPageId;
    }

    public String getTitle() {

        return mTitle;
    }

    public String getExtract() {

        return mExtract;
    }

    public Thumbnail getThumbnail() {

        return mThumbnail;
    }

    public boolean isMissing() {

        return mMissing;
    }

    public static class Thumbnail {

        @SerializedName("source")
        private String mSource;

        public Thumbnail(String source) {

            mSource = source;
        }

        public String getSource() {

            return mSource;
        }
    }
}
