package control.utils;

import java.util.List;

public interface BookStorageRepository {
    boolean exists(int bookId);
    List<Integer> findAllBookIds();
}