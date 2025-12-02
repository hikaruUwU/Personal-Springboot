package com.demo.base.domain.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Accessors(chain = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Result<T> implements Serializable {
    String message;
    T data;
    int code;

    public static <T> Result<T> success(T data) {
        return new Result<T>().setData(data).setCode(200);
    }

    public static <T> Result<T> success(String message) {
        return new Result<T>().setMessage(message).setCode(200);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<T>().setMessage(message).setCode(400);
    }

}