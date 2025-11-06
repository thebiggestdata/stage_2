package com.thebiggestdata.domain.port.out;

import java.util.List;


public interface BookStoragePort {
    boolean exists(int bookId);
    List<Integer> findAllBookIds();
}