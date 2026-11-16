package es.udc.ps.wikirym.app.domain;

import com.google.gson.annotations.SerializedName;

public class PageDetail {

    @SerializedName("pageid")
    private int mPageId;

    @SerializedName("title")
    private String mTitle;

    // Introducción en texto plano
    @SerializedName("extract")
    private String mExtract;

    // Imagen principal; null si no tiene
    @SerializedName("thumbnail")
    private Thumbnail mThumbnail;

    // true si no existe la página
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
