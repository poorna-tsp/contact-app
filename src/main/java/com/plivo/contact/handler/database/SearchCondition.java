package com.plivo.contact.handler.database;

public class SearchCondition {
    private Field field ;
    private String fromValue ;
    private String toValue ;

    public SearchCondition(Field field, String fromValue, String toValue) {
        this.field = field;
        this.fromValue = fromValue;
        this.toValue = toValue;
    }

    public Field getField() {
        return field;
    }

    public String getFromValue() {
        return fromValue;
    }

    public String getToValue() {
        return toValue;
    }
}
