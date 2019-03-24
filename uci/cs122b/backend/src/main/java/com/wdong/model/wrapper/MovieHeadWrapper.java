package com.wdong.model.wrapper;

import java.util.List;

public class MovieHeadWrapper extends ResponseWrapper {
    private List<MovieHead> ids;

    public MovieHeadWrapper(List<MovieHead> ids) {
        this.ids = ids;
    }

    public List<MovieHead> getIds() {
        return ids;
    }

    public void setIds(List<MovieHead> ids) {
        this.ids = ids;
    }

    public static class MovieHead {
        private String value;
        private String label;

        public MovieHead(String value, String label) {
            this.value = value;
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}

