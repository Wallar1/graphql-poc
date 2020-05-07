package com.graphqljava.tutorial.bookdetails;

import com.graphqljava.tutorial.bookdetails.batchLoader.BookLoader;
import graphql.schema.DataFetcher;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


@Component
public class GraphQLDataFetchers {

    public DataFetcher getAllBooks() {
        return dataFetchingEnvironment -> {
            DataLoader<String, Object> dataLoader = dataFetchingEnvironment.getDataLoader("book");
            List<String> allIds = BookLoader.BOOKS.stream().map(b -> b.get("id")).collect(toList());
            return dataLoader.loadMany(allIds);
        };
    }

    // We put the async code into the batchloader, not the data fetcher
    public DataFetcher getBookByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            DataLoader<String, Object> dataLoader = dataFetchingEnvironment.getDataLoader("book");
            String bookId = dataFetchingEnvironment.getArgument("id");
            return dataLoader.load(bookId);
        };
    }

    ;


    public DataFetcher getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, String> book = dataFetchingEnvironment.getSource();
            String authorId = book.get("authorId");
            DataLoader<String, Object> dataLoader = dataFetchingEnvironment.getDataLoader("author");
            return dataLoader.load(authorId);
        };
    }
}
