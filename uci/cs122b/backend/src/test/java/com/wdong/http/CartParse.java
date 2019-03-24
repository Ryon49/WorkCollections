package com.wdong.http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wdong.model.request.SaleMovieRequest;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class CartParse {
    @Test
    public void test() {
        String json = "[{\"id\":\"tt0094859\",\"quantity\":1}, {\"id\":\"tt0094259\",\"quantity\":2}]";
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<SaleMovieRequest> myObjects = mapper.readValue(json,
                    new TypeReference<List<SaleMovieRequest>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(json);
    }
}