package com.thebiggestdata.infrastructure.adapter.out.persistence;

import com.thebiggestdata.domain.port.out.BookStoragePort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public record FileSystemBookRepository(String basePath) implements BookStoragePort {
    private static final Logger logger = Logger.getLogger(String.valueOf(FileSystemBookRepository.class));
    private static final Pattern BOOK_FILE_PATTERN = Pattern.compile("(\\d+)\\.(body|header)\\.txt");

    @Override
    public boolean exists(int bookId) {
        try {
            Path datalakePath = Paths.get(basePath);
            if (!Files.exists(datalakePath)) return false;
            return findBookInStructure(datalakePath, bookId);
        } catch (IOException e) {
            logger.info("Error checking book existence: {}" + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Integer> findAllBookIds() {
        try {
            Path datalakePath = Paths.get(basePath);
            if (!Files.exists(datalakePath)) return Collections.emptyList();
            Set<Integer> bookIds = new TreeSet<>();
            extractAllBookIdsFromStructure(datalakePath, bookIds);
            return new ArrayList<>(bookIds);
        } catch (IOException e) {
            logger.info("Error listing book IDs: {}" + e.getMessage());
            return Collections.emptyList();
        }
    }

    private boolean findBookInStructure(Path datalakePath, int bookId) throws IOException {
        try (Stream<Path> dateDirs = Files.list(datalakePath)) {
            return dateDirs
                    .filter(Files::isDirectory)
                    .filter(this::isDateDirectory)
                    .anyMatch(dateDir -> bookExistsInDateDir(dateDir, bookId));
        }
    }

    private boolean bookExistsInDateDir(Path dateDir, int bookId) {
        try (Stream<Path> hourDirs = Files.list(dateDir)) {
            return hourDirs.filter(Files::isDirectory).anyMatch(hourDir -> bookExistsInHourDir(hourDir, bookId));
        } catch (IOException e) {
            logger.warning("Error checking date directory {}: {}" + dateDir + "Err msg: " + e.getMessage());
            return false;
        }
    }

    private boolean bookExistsInHourDir(Path hourDir, int bookId) {
        try (Stream<Path> files = Files.list(hourDir)) {
            return files
                    .filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .anyMatch(fileName -> {
                        Matcher matcher = BOOK_FILE_PATTERN.matcher(fileName);
                        return matcher.matches() && Integer.parseInt(matcher.group(1)) == bookId;
                    });
        } catch (IOException e) {
            logger.warning("Error checking hour directory {}: {}" + hourDir + "Err msg:" + e.getMessage());
            return false;
        }
    }

    private void extractAllBookIdsFromStructure(Path datalakePath, Set<Integer> bookIds) throws IOException {
        try (Stream<Path> dateDirs = Files.list(datalakePath)) {
            dateDirs
                    .filter(Files::isDirectory)
                    .filter(this::isDateDirectory)
                    .forEach(dateDir -> extractBookIdsFromDateDir(dateDir, bookIds));
        }
    }

    private void extractBookIdsFromDateDir(Path dateDir, Set<Integer> bookIds) {
        try (Stream<Path> hourDirs = Files.list(dateDir)) {
            hourDirs.filter(Files::isDirectory).forEach(hourDir -> extractBookIdsFromHourDir(hourDir, bookIds));
        } catch (IOException e) {
            logger.warning("Error extracting from date directory {}: {}" + dateDir + "Err msg: " + e.getMessage());
        }
    }

    private void extractBookIdsFromHourDir(Path hourDir, Set<Integer> bookIds) {
        try (Stream<Path> files = Files.list(hourDir)) {
            files.filter(Files::isRegularFile)
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .forEach(fileName -> {
                        Matcher matcher = BOOK_FILE_PATTERN.matcher(fileName);
                        if (matcher.matches()) {
                            bookIds.add(Integer.parseInt(matcher.group(1)));
                        }
                    });
        } catch (IOException e) {
            logger.warning("Error extracting from hour directory {}: {}" + hourDir + "Err msg: " + e.getMessage());
        }
    }

    private boolean isDateDirectory(Path path) {
        String dirName = path.getFileName().toString();
        return dirName.matches("\\d{8}");
    }
}