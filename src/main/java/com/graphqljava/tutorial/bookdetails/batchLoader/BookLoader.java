package com.graphqljava.tutorial.bookdetails.batchLoader;

import com.google.common.collect.ImmutableMap;
import org.dataloader.BatchLoader;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BookLoader {

    public static final List<Map<String, String>> BOOKS = Arrays.asList(
            ImmutableMap.of("id", "book-1",
                    "name", "Harry Potter and the Philosopher's Stone",
                    "pageCount", "223",
                    "authorId", "author-1"),
            ImmutableMap.of("id", "book-2",
                    "name", "Moby Dick",
                    "pageCount", "635",
                    "authorId", "author-2"),
            ImmutableMap.of("id", "book-3",
                    "name", "Interview with the vampire",
                    "pageCount", "371",
                    "authorId", "author-3")
    );


    private static BatchLoader<String, Object> bookBatchLoader = new BatchLoader<String, Object>() {
        @Override
        public CompletionStage<List<Object>> load(List<String> keys) {
            //
            // we use supplyAsync() of values here for maximum parellisation
            //
            return CompletableFuture.supplyAsync(() -> getBooksById(keys));
        }
    };

    public static BatchLoader<String, Object> getBookBatchLoader() {
        return bookBatchLoader;
    }

    public static class BookDto {
        private String id;
        private String name;
        private String pageCount;
        private String authorId;

        BookDto(Map<String, String> bookMap) {
            this.id = bookMap.get("id");
            this.name = bookMap.get("name");
            this.pageCount = bookMap.get("pageCount");
            this.authorId = bookMap.get("authorId");
        }

        public String getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getPageCount() {
            return pageCount;
        }
        public String getAuthorId() {
            return authorId;
        }

        public String toString() {
            return "id: " + id +
                    ", name: " + name +
                    ", pageCount: " + pageCount +
                    ", authorId: " + authorId;
        }
    }

    private static List<Object> getBooksById(List<String> bookIds) {
        Stream<Map<String, String>> bookStream = BOOKS.stream();
        HashSet<String> keyset = new HashSet<>(bookIds);
        if (!keyset.contains("allBooks")) {
            bookStream = bookStream.filter(b -> bookIds.contains(b.get("id")));
        }
        return bookStream.collect(Collectors.toList());
    }
}
