package com.example.franc.italianpoliticsclosely;

import android.nfc.Tag;

import java.util.List;
import java.lang.StringBuilder;

/**
 * An {@link Ipc} object contains information related to a single earthquake.
 */

public class Ipc {

    /**
     * News' title, author, section, date and web url
     */
    private String mTitle;
    private String mSection;
    private String mDate;
    private String mWebUrl;
    private List<String> tags;

    /**
     * Constructs a new {@link Ipc} object.
     *
     * @param title   is the head of an article
     * @param section is the section of the newspaper where it appeared
     * @param date    is the the publication date
     * @param webUrl  is the website URL to find more details about the news
     * @param tags    corresponds to the article's author
     */
    public Ipc(String title, String section, String date, String webUrl, List<String> tags) {
        mTitle = title;
        mSection = section;
        this.mDate = date;
        this.mWebUrl = webUrl;
        this.tags = tags;
    }

    /**
     * Returns the news' title, author, section, date and web url
     */
    public String getTitle() {
        return mTitle;
    }

    public String getSection() {
        return mSection;
    }

    public String getDate() {
        return mDate;
    }

    public String getWebUrl() {
        return mWebUrl;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getTagsString() {
        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            sb.append(tag);
        }
        return sb.toString();
    }
}

