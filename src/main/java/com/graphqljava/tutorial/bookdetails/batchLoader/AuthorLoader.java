package com.graphqljava.tutorial.bookdetails.batchLoader;

import com.google.common.collect.ImmutableMap;
import org.dataloader.BatchLoader;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.stream.Collectors.toList;

public class AuthorLoader {
    private static List<Map<String, String>> authors = Arrays.asList(
            ImmutableMap.of("id", "author-1",
                    "firstName", "Joanne",
                    "lastName", "Rowling"),
            ImmutableMap.of("id", "author-2",
                    "firstName", "Herman",
                    "lastName", "Melville"),
            ImmutableMap.of("id", "author-3",
                    "firstName", "Anne",
                    "lastName", "Rice")
    );

    private static BatchLoader<String, Object> authorBatchLoader = new BatchLoader<String, Object>() {
        @Override
        public CompletionStage<List<Object>> load(List<String> keys) {
            //
            // we use supplyAsync() of values here for maximum parellisation
            //
            return CompletableFuture.supplyAsync(() -> getAuthorsByIds(keys));
        }
    };

    public static BatchLoader<String, Object> getAuthorBatchLoader() {
        return authorBatchLoader;
    }

    public static class AuthorDto {
        private final String id;
        private final String firstName;
        private final String lastName;

        public AuthorDto(Map<String, String> map) {
            this.id = map.get("id");
            this.firstName = map.get("firstName");
            this.lastName = map.get("lastName");
        }

        public String getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    private static List<Object> getAuthorsByIds(List<String> authorIds) {
        HashSet<String> idSet = new HashSet<>(authorIds);
        return authors
                .stream()
                .filter(a -> idSet.contains(a.get("id")))
                .map(AuthorDto::new)
                .collect(toList());
    }
}
