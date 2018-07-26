package com.kinsey.es.query;


import org.axonframework.queryhandling.responsetypes.InstanceResponseType;
import org.axonframework.queryhandling.responsetypes.ResponseType;

import java.util.List;

public class CustomResponseTypes {

    private CustomResponseTypes() {
    }

    public static <R> ResponseType<List<R>> listOf(Class<R> type) {
        return new ListResponseType<>(type);
    }

    public static <R> ResponseType<R> instanceOf(Class<R> type) {
        return new InstanceResponseType<>(type);
    }
}
