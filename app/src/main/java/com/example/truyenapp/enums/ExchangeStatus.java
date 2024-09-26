package com.example.truyenapp.enums;

public enum ExchangeStatus {
    EXIST_IN_REPO(1), POINT_NOT_ENOUGH(2), EXCHANGE_SUCCESS(3), EXCHANGE_FAIL(4);
    private Integer status;

    ExchangeStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }
}
