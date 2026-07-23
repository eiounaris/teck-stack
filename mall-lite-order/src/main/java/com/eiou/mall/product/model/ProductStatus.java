package com.eiou.mall.product.model;

import com.eiou.mall.common.exception.BusinessException;
import com.eiou.mall.common.exception.ErrorCode;

public enum ProductStatus {

    OFF_SALE(0, "OFF_SALE"),
    ON_SALE(1, "ON_SALE");

    private final int code;
    private final String text;

    ProductStatus(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int code() {
        return code;
    }

    public String text() {
        return text;
    }

    public static ProductStatus fromCode(Integer code) {
        if (code == null) {
            return ON_SALE;
        }
        for (ProductStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid product status");
    }
}
