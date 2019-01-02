package com.rg.http;

import com.rg.http.io.HttpRequest;
import com.rg.http.io.HttpResponse;

public interface RequestHandler {

    void handle(HttpRequest request, HttpResponse response);

}
