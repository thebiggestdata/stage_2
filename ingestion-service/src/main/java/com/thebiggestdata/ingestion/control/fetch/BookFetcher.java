package com.thebiggestdata.ingestion.control.fetch;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;


public class BookFetcher {
    private final String Base_Url = "https://www.gutenberg.org/cache/epub/{book_id}/pg{book_id}.txt";
    private final HttpClient httpClient;

    public BookFetcher() {this.httpClient = HttpClient.newHttpClient();}

    public String fetch(int bookId) throws IOException, InterruptedException {
        return fetchBook(bookId);
    }

    private String fetchBook(int bookId) throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest(bookId);
        if (response.statusCode() != 200) throw new IOException("Failed to fetch book with ID: " + bookId);
        return response.body();
    }

    private HttpResponse<String> sendRequest(int bookId) throws IOException, InterruptedException {
        HttpRequest request = createRequest(bookId);
        return this.httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpRequest createRequest(int bookId) {
        return HttpRequest.newBuilder()
                .uri(URI.create(Base_Url.replace("{book_id}", String.valueOf(bookId))))
                .build();
    }
}