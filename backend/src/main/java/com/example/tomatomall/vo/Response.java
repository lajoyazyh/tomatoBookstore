package com.example.tomatomall.vo;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Response<T> implements Serializable {

    private String code;

    private String msg;

    private T data;

    public static <T> Response<T> buildSuccess(T data) {
        return new Response<T>("200", null, data);
    }

    public static <T> Response<T> buildFailure(String code, String msg) {
        return new Response<T>(code, msg, null);
    }
}
