package com.wdong.model.wrapper;

public class CreditCheckWrapper extends ResponseWrapper {
    // 1 = holder error, 2 = card No. error, 3 = expiration date error

    private int type;


    public CreditCheckWrapper(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
