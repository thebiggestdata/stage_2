package control.storer;

import control.utils.DatalakePathBuilder;
import model.Book;
import model.StorageResult;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookStorer {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH");
    private final DatalakePathBuilder pathBuilder;
    private final FileSystemManager fileManager;
    private final BookFileNameGenerator fileNameGenerator;
    private final BookFileWriter fileWriter;

    public BookStorer(DatalakePathBuilder pathBuilder) {
        this.pathBuilder = pathBuilder;
        this.fileManager = new FileSystemManager();
        this.fileNameGenerator = new BookFileNameGenerator();
        this.fileWriter = new BookFileWriter();
    }

    public StorageResult save(Book book) {return save(book, LocalDateTime.now());}

    public StorageResult save(Book book, LocalDateTime timestamp) {
        String directory = pathBuilder.buildPath(timestamp);
        try {fileManager.ensureDirectoryExists(directory);}
        catch (IOException e) {throw new RuntimeException(e);}
        String headerPath = saveFile(directory, fileNameGenerator.generateHeaderFileName(book), book.header());
        String bodyPath = saveFile(directory, fileNameGenerator.generateBodyFileName(book), formatBody(book));
        String dateStr = timestamp.format(DATE_FORMATTER);
        String hourStr = timestamp.format(HOUR_FORMATTER);
        String timestampStr = dateStr + "/" + hourStr;
        return new StorageResult(true, headerPath, bodyPath, timestampStr);
    }

    private String saveFile(String directory, String fileName, String content) {
        String filePath = fileManager.buildPath(directory, fileName);
        fileWriter.write(filePath, content);
        return filePath;
    }

    private String formatBody(Book book) {return book.body() + "\n\n" + book.footer();}
}
