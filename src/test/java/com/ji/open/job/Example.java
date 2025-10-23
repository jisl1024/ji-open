package com.ji.open.job;

import org.springframework.web.client.RestClient;
import org.springframework.http.ResponseEntity;

public class Example {
    public static void main(String[] args) {
        RestClient client = RestClient.create("https://jsonplaceholder.typicode.com");

        // GET 请求
        String response = client.get()
                .uri("/posts/1")
                .retrieve()
                .body(String.class);

        System.out.println(response);
    }
}
