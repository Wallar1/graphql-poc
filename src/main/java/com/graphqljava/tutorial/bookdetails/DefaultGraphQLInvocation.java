package com.graphqljava.tutorial.bookdetails;

import com.graphqljava.tutorial.bookdetails.batchLoader.AuthorLoader;
import com.graphqljava.tutorial.bookdetails.batchLoader.BookLoader;
import com.graphqljava.tutorial.bookdetails.webmvc.ExecutionInputCustomizer;
import com.graphqljava.tutorial.bookdetails.webmvc.GraphQLInvocation;
import com.graphqljava.tutorial.bookdetails.webmvc.GraphQLInvocationData;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.Internal;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.concurrent.CompletableFuture;

@Component
@Internal
public class DefaultGraphQLInvocation implements GraphQLInvocation {

    @Autowired
    GraphQLProvider graphQLProvider;

    @Autowired
    ExecutionInputCustomizer executionInputCustomizer;

    // DataLoaderRegistry is a place to register all data loaders in that needs to be dispatched together
    // Since data loaders are stateful, they are created per execution request.
    private DataLoaderRegistry createRegistry() {
        DataLoaderRegistry registry = new DataLoaderRegistry();
        registry.register("book", DataLoader.newDataLoader(BookLoader.getBookBatchLoader()));
        registry.register("author", DataLoader.newDataLoader(AuthorLoader.getAuthorBatchLoader()));
        return registry;
    }


    @Override
    public CompletableFuture<ExecutionResult> invoke(GraphQLInvocationData invocationData, WebRequest webRequest) {
        ExecutionInput.Builder executionInputBuilder = ExecutionInput.newExecutionInput()
                .query(invocationData.getQuery())
                .operationName(invocationData.getOperationName())
                .variables(invocationData.getVariables())
                .dataLoaderRegistry(createRegistry());
        ExecutionInput executionInput = executionInputBuilder.build();
        CompletableFuture<ExecutionInput> customizedExecutionInput = executionInputCustomizer.customizeExecutionInput(executionInput, webRequest);
        return customizedExecutionInput.thenCompose(graphQLProvider.graphQL()::executeAsync);
    }

}
