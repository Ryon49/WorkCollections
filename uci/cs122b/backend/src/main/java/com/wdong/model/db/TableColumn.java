package com.wdong.model.db;

public class TableColumn {

    private String name;

    private String type;

    private boolean isPrimary;

    public TableColumn(String name, String type, boolean isPrimary) {
        this.name = name;
        this.type = type;
        this.isPrimary = isPrimary;
    }

    // region getter and setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    // endregion
}
