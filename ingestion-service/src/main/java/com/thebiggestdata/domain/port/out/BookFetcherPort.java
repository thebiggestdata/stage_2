package com.thebiggestdata.domain.port.out;


public interface BookFetcherPort {
    String fetchBookContent(int bookId) throws Exception;
}
