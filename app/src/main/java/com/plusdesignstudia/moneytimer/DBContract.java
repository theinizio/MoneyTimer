package com.plusdesignstudia.moneytimer;


import android.provider.BaseColumns;

import java.util.Date;

public class DBContract {

    /**
     * Describes History Table and model.
     */
    public static class Names {

        //имя таблицы
        public static final String TABLE_NAME = "sessions";
        private Date date;
        private float hourRate;

        public long getTime_ms() {
            return time_ms;
        }

        public void setTime_ms(long time_ms) {
            this.time_ms = time_ms;
        }

        public float getHourRate() {
            return hourRate;
        }

        public void setHourRate(float hourRate) {
            this.hourRate = hourRate;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        private long time_ms;

        /*
         * (non-Javadoc)
         *
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {

            StringBuilder builder = new StringBuilder();
            builder.append(date);
            return builder.toString();
        }

        //Класс с именами наших полей в базе
        public class NamesColumns implements BaseColumns {
            /**
             * Strings
             */
            public static final String ID = "id";
            /**
             * Strings
             */
            public static final String DATE = "date";
            /**
             * Strings
             */
            public static final String HOUR_RATE = "hour_rate";
            /**
             * String
             */
            public static final String TIME_MS = "time_ms";

        }
    }
}