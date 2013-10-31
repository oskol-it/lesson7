package ru.ifmo.rain.loboda.rss;

import java.io.Serializable;

public class RSSRecord implements Serializable {
    private String author;
    private String annotation;
    private String date;
    private String description;

    Fields toRecord;

    RSSRecord() {
    }

    public String getAuthor() {
        return author;
    }

    public String getAnnotation() {
        return annotation;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public void setAuthor() {
        toRecord = Fields.AUTHOR;
    }

    public void setDescription() {
        toRecord = Fields.DESCRIPTION;
    }

    public void setAnnotation() {
        toRecord = Fields.ANNOTATION;
    }

    public void setDate() {
        toRecord = Fields.DATE;
    }

    public void set(String value) {
        switch (toRecord) {
            case DATE:
                date = value;
                break;
            case ANNOTATION:
                annotation = value;
                break;
            case AUTHOR:
                author = value;
                break;
            case DESCRIPTION:
                description = value;
                break;
        }
    }

    private enum Fields {
        DATE, ANNOTATION, AUTHOR, DESCRIPTION;
    }
}
