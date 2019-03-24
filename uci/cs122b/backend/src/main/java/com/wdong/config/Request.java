package com.wdong.config;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Optional;

public class Request {
    private static int size;
    private static int page;
    private static int sort;

    private static HashMap<Integer, Sort> sortMap;

    static {
        size = 10;
        page = 0;
        sort = 0;
        sortMap = new HashMap<>();
        sortMap.put(0, null);
        sortMap.put(1, Sort.by(Order.asc("title")));
        sortMap.put(2, Sort.by(Order.desc("title")));
        sortMap.put(3, Sort.by(Order.asc("rating_rating")));
        sortMap.put(4, Sort.by(Order.desc("rating_rating")));
        sortMap.put(5, Sort.by(Order.asc("id_movie_title")));
        sortMap.put(6, Sort.by(Order.desc("id_movie_title")));
        sortMap.put(7, Sort.by(Order.asc("id_movie_rating_rating")));
        sortMap.put(8, Sort.by(Order.desc("id_movie_rating_rating")));
    }

    public static PageRequest getRequest() {
        if (sort != 0) {
            return PageRequest.of(page, size, sortMap.get(sort));
        }
        return PageRequest.of(page, size);
    }

    // region getter and setter
    public static void setSize(int maxRecords) {
        size = maxRecords;
    }

    public static void setPage(int pageNum) {
        page = pageNum - 1;
    }

    public static int getSize() {
        return size;
    }

    public static int getPage() {
        return page;
    }

    public static int getSort(boolean returnFunction) {
        return sort;
    }

    public static void setSort(int sort) {
        Request.sort = sort;
    }

    public static void handlePageAndSort(Optional<Integer> pageNum, Optional<Integer> maxRecords,
                                         Optional<Integer> sortBy) {
        Request.handlePageAndSort(pageNum, maxRecords, sortBy, false);
    }


    public static void handlePageAndSort(Optional<Integer> pageNum, Optional<Integer> maxRecords,
                                         Optional<Integer> sortBy, boolean isGenre) {
        pageNum.ifPresent(Request::setPage);
        maxRecords.ifPresent(Request::setSize);

        if (sortBy.isPresent()) {
            if (isGenre) {
                Request.setSort(sortBy.get() + 4);
            } else {
                Request.setSort(sortBy.get());
            }
        } else {
            Request.setSort(0);
        }
    }
    // endregion
}