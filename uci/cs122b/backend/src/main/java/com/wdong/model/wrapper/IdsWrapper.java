package com.wdong.model.wrapper;

import java.util.ArrayList;
import java.util.HashMap;

public class IdsWrapper extends ResponseWrapper {
    private HashMap<String, String> ids;

    public IdsWrapper(HashMap<String, String> ids) {
        this.ids = ids;
    }

    public HashMap<String, String> getIds() {
        return ids;
    }

    public void setIds(HashMap<String, String> ids) {
        this.ids = ids;
    }
}
