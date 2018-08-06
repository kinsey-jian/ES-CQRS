package com.kinsey.order.client;

import com.kinsey.order.client.model.ProductModel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by zj on 2018/8/3
 */
@FeignClient(value = "product-service",path = "/product")
public interface ProductClient {

    @GetMapping("/{id}")
    ProductModel queryProductById(@PathVariable("id") String id);
}
