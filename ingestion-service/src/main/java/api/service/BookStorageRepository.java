package api.service;

import java.util.List;

public interface BookStorageRepository {
    boolean exists(int bookId);
    List<Integer> findAllBookIds();
}