package com.rg.loader;

import com.rg.servlet.context.JerryServletContext;

import java.util.Map;

public interface ContextLoader {

    Map<String, JerryServletContext> load(String path);

}