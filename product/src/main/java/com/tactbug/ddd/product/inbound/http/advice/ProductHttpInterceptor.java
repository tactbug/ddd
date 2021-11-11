package com.tactbug.ddd.product.inbound.http.advice;

import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.AsyncWebRequestInterceptor;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;

import java.util.Objects;

public class ProductHttpInterceptor implements WebRequestInterceptor {
    @Override
    public void preHandle(WebRequest request) throws Exception {

    }

    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception {

    }

    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception {

    }
}
