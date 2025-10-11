package control.storer;

import model.Book;
import model.StorageResult;
import java.time.LocalDateTime;

public class BookStorer {
    private final FileSystemManager fileManager;
    private final BookFileNameGenerator fileNameGenerator;
    private final BookFileWriter fileWriter;

    public BookStorer() {
        this.fileManager = new FileSystemManager();
        this.fileNameGenerator = new BookFileNameGenerator();
        this.fileWriter = new BookFileWriter();
    }

    public StorageResult save(Book book, String directory) {
        LocalDateTime timestamp = LocalDateTime.now();
        fileManager.ensureDirectoryExists(directory);

        String headerPath = saveFile(directory, fileNameGenerator.generateHeaderFileName(book), book.header());
        String bodyPath = saveFile(directory, fileNameGenerator.generateBodyFileName(book), formatBody(book));

        return new StorageResult(true, headerPath, bodyPath, timestamp);
    }

    private String saveFile(String directory, String fileName, String content) {
        String filePath = fileManager.buildPath(directory, fileName);
        fileWriter.write(filePath, content);
        return filePath;
    }

    private String formatBody(Book book) {
        return book.body() + "\n\n" + book.footer();
    }
}

