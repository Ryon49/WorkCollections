package com.wdong.model.db;

import java.util.ArrayList;

public class Table {

    private String tableName;

    private ArrayList<TableColumn> columns;

    public Table(String tableName) {
        this.tableName = tableName;
        this.columns = new ArrayList<>();
    }

    public void addColumn(TableColumn column) {
        columns.add(column);
    }

    // region getter and setter
    public ArrayList<TableColumn> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<TableColumn> columns) {
        this.columns = columns;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    // endregion
}
