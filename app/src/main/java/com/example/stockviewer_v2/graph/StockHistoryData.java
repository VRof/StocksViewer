package com.example.stockviewer_v2.graph;

import java.util.List;

public class StockHistoryData {
        private Meta meta;
        private List<PriceData> values;
        private String status;
        public static class Meta {
            private String symbol;
            private String interval;
            private String currency;
            private String exchange_timezone;
            private String exchange;
            private String mic_code;
            private String type;

        }

        public static class PriceData {
            private String datetime;
            private String open;
            private String high;
            private String low;
            private String close;
            private String volume;

            // Getters and setters


            public String getDatetime() {
                return datetime;
            }

            public void setDatetime(String datetime) {
                this.datetime = datetime;
            }

            public String getOpen() {
                return open;
            }

            public void setOpen(String open) {
                this.open = open;
            }

            public String getHigh() {
                return high;
            }

            public void setHigh(String high) {
                this.high = high;
            }

            public String getLow() {
                return low;
            }

            public void setLow(String low) {
                this.low = low;
            }

            public String getClose() {
                return close;
            }

            public void setClose(String close) {
                this.close = close;
            }

            public String getVolume() {
                return volume;
            }

            public void setVolume(String volume) {
                this.volume = volume;
            }
        }

    public List<PriceData> getValues() {
        return values;
    }
}

