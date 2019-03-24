package com.wdong.model.wrapper;

import com.wdong.model.db.Table;

import java.util.ArrayList;

public class SchemeWrapper extends ResponseWrapper {
    private ArrayList<Table> tables;

    public SchemeWrapper(ArrayList<Table> tables) {
        this.tables = tables;
    }

    public ArrayList<Table> getTables() {
        return tables;
    }

    public void setTables(ArrayList<Table> tables) {
        this.tables = tables;
    }
}
