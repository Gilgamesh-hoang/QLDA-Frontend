package com.example.truyenapp.enums;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


public enum CommentState {
    SHOW(1),
    HIDE(2);
    private final Integer state;

    CommentState(Integer state) {
        this.state = state;
    }

    public Integer getState() {
        return state;
    }

}
