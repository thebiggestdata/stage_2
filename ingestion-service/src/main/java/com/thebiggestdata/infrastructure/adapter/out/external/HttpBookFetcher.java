package com.thebiggestdata.infrastructure.adapter.out.external;

import com.thebiggestdata.domain.exception.BookFetchException;
import com.thebiggestdata.domain.port.out.BookFetcherPort;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class HttpBookFetcher implements BookFetcherPort {
    private final String Base_Url = "https://www.gutenberg.org/cache/epub/{book_id}/pg{book_id}.txt";
    private final HttpClient httpClient;

    public HttpBookFetcher() {this.httpClient = HttpClient.newHttpClient();}

    @Override
    public String fetchBookContent(int bookId) throws BookFetchException {
        try {return fetchBook(bookId);}
        catch (IOException | InterruptedException e) {
            throw new BookFetchException(bookId, e.getMessage(), e);
        }
    }

    private String fetchBook(int bookId) throws IOException, InterruptedException {
        HttpResponse<String> response = sendRequest(bookId);
        if (response.statusCode() != 200) throw new IOException("HTTP status: " + response.statusCode());
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