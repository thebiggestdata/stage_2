package api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record FileSystemBookRepository(String basePath) implements BookStorageRepository {
    private static final Logger logger = LoggerFactory.getLogger(FileSystemBookRepository.class);

    @Override
    public boolean exists(int bookId) {
        try {
            Path datalakePath = Paths.get(basePath);
            if (!Files.exists(datalakePath)) return false;
            return findBookDirectory(datalakePath, bookId);
        } catch (IOException e) {
            logger.error("Error checking book existence: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<Integer> findAllBookIds() {
        try {
            Path datalakePath = Paths.get(basePath);
            if (!Files.exists(datalakePath)) return Collections.emptyList();
            return extractBookIds(datalakePath);
        } catch (IOException e) {
            logger.error("Error listing book IDs: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private boolean findBookDirectory(Path basePath, int bookId) throws IOException {
        try (Stream<Path> paths = Files.walk(basePath)) {
            return paths.filter(Files::isDirectory).anyMatch(path -> path.getFileName().toString().equals(String.valueOf(bookId)));
        }
    }

    private List<Integer> extractBookIds(Path basePath) throws IOException {
        List<Integer> bookIds = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(basePath)) {
            paths.filter(Files::isDirectory)
                    .filter(path -> !path.equals(basePath))
                    .forEach(path -> parseBookId(path).ifPresent(bookIds::add));
        }
        return bookIds;
    }

    private Optional<Integer> parseBookId(Path path) {
        try {
            String fileName = path.getFileName().toString();
            return Optional.of(Integer.parseInt(fileName));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}
