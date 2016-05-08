package com.patriotcoder.automation.pihomeautomationactor.rest;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpMethod.PUT;

import org.restexpress.RestExpress;


public abstract class Routes
{

    public static void define(HealthCheckController hcc, ActionController ac, RestExpress server)
    {
        //health check        
        server.uri("/health", hcc)
                .action("getHealth", GET)
                .name("health").noSerialization();

        /**
         * route to perform some action
         */
        server.uri("/", ac)
                .method(POST, PUT)
                .name("act");
        

    }
}
