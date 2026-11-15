package es.udc.ps.wikirym.app.domain;

public class WikiEntry {

    private int mPageId;
    private String mTitle;
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

    public EntryType getType() {

        return mType;
    }
}
