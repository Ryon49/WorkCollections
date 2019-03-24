package com.wdong.model.wrapper;

import java.util.List;

public class SalesWrapper extends ResponseWrapper {
    private List<Integer> sids;

    public SalesWrapper(List<Integer> sids) {
        this.sids = sids;
    }

    public List<Integer> getSids() {
        return sids;
    }

    public void setSids(List<Integer> sids) {
        this.sids = sids;
    }
}
