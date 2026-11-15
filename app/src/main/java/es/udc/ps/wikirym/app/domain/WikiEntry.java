package es.udc.ps.wikirym.app.domain;

import com.google.gson.annotations.SerializedName;

public class WikiEntry {

    private static final String SKYRIM_PREFIX = "Skyrim:";

    @SerializedName("pageid")
    private int mPageId;

    @SerializedName("title")
    private String mTitle;

    // Not in the JSON: it depends on the category that was requested
    private EntryType mType;

    public WikiEntry(int pageId,
                     String title,
                     EntryType type) {

        mPageId = pageId;
        mTitle = title;
        mType = type;
    }

    public int getPageId() {

        return mPageId;
    }

    public String getTitle() {

        return mTitle;
    }

    public String getDisplayTitle() {

        if (mTitle != null && mTitle.startsWith(SKYRIM_PREFIX)) {
            return mTitle.substring(SKYRIM_PREFIX.length());
        }
        return mTitle;
    }

    public EntryType getType() {

        return mType;
    }

    public void setType(EntryType type) {

        mType = type;
    }
}
