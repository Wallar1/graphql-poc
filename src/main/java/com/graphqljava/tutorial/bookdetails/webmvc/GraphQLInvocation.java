package com.graphqljava.tutorial.bookdetails.webmvc;

import graphql.ExecutionResult;
import graphql.PublicApi;
import org.springframework.web.context.request.WebRequest;

import java.util.concurrent.CompletableFuture;

@PublicApi
public interface GraphQLInvocation {

    CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest);

}
