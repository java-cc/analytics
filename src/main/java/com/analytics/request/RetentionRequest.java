package com.analytics.request;

import java.util.Date;

public class RetentionRequest {
    private Date installDate;
    private RetentionType retentionType;

    public Date getInstallDate() {
        return installDate;
    }

    public void setInstallDate(Date installDate) {
        this.installDate = installDate;
    }

    public RetentionType getRetentionType() {
        return retentionType;
    }

    public void setRetentionType(RetentionType retentionType) {
        this.retentionType = retentionType;
    }

    public enum RetentionType {
        DAY1(1),
        DAY7(7),
        DAY40(40);

        private int retention;

        RetentionType(int retention) {
            this.retention = retention;
        }

        public int getRetention() {
            return retention;
        }
    }
}
