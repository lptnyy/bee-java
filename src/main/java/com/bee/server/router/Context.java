package com.bee.server.router;

import com.bee.server.http.Servlet;

@FunctionalInterface
public interface Context {
   void init(Servlet controller);
}
