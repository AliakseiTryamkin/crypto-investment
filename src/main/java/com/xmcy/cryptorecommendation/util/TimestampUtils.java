package com.xmcy.cryptorecommendation.util;

import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;

public final class TimestampUtils {

    private TimestampUtils() {
    }

    /**
     * This method convert milliSeconds to Timestamp format
     *
     * @param milliSecTimestamp milliSeconds
     */
    public static Timestamp parseTimestamp(String milliSecTimestamp) {
        return StringUtils.isNotEmpty(milliSecTimestamp) ? new Timestamp(Long.parseLong(milliSecTimestamp)) : null;
    }
}
