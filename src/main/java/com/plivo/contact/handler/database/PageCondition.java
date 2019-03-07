package com.plivo.contact.handler.database;

public class PageCondition {
    private int size ;
    private String from ;

    public PageCondition(int size, String from) {
        this.size = size;
        this.from = from;
    }

    public int getSize() {
        return size;
    }

    public String getFrom() {
        return from;
    }
}
