package com.analytics.entity;

import java.util.Date;

public class RetentionEntity {
    private Date install;
    private Date request;
    private double percentage;

    public RetentionEntity(Date install, Date request, double percentage) {
        this.install = install;
        this.request = request;
        this.percentage = percentage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RetentionEntity that = (RetentionEntity) o;

        if (install != null ? !install.equals(that.install) : that.install != null) return false;
        return request != null ? request.equals(that.request) : that.request == null;
    }

    @Override
    public int hashCode() {
        int result = install != null ? install.hashCode() : 0;
        result = 31 * result + (request != null ? request.hashCode() : 0);
        return result;
    }
}
