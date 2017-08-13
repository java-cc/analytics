package com.analytics.entity;

import java.util.Date;

public class DAUEntity {
    private Date date;
    private int count;

    public DAUEntity(Date date, int count) {
        this.date = date;
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DAUEntity that = (DAUEntity) o;

        return date != null ? date.equals(that.date) : that.date == null;
    }

    @Override
    public int hashCode() {
        return date != null ? date.hashCode() : 0;
    }
}
