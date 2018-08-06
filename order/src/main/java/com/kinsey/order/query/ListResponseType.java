package com.kinsey.order.query;

import org.axonframework.queryhandling.responsetypes.AbstractResponseType;

import java.lang.reflect.Type;
import java.util.List;

public class ListResponseType<R> extends AbstractResponseType<List<R>> {

    public ListResponseType(Class<R> expectedPageGenericType) {
        super(expectedPageGenericType);
    }

    @Override
    public boolean matches(Type responseType) {

        return isParameterizedType(responseType);
    }
}