package control;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Mantiene un registro persistente de los libros que ya han sido procesados.
 * Usa un archivo de texto simple donde cada línea es un book_id.
 */
public class ProcessingTracker {
    private final String trackingFilePath;
    private final Set<Integer> processedBooks;

    /**
     * Constructor que especifica la ruta del archivo de seguimiento.
     *
     * @param trackingFilePath Ruta del archivo donde se guardan los IDs procesados
     */
    public ProcessingTracker(String trackingFilePath) {
        this.trackingFilePath = trackingFilePath;
        this.processedBooks = new HashSet<>();
        loadProcessedBooks();
    }

    /**
     * Constructor por defecto que usa "processed_books.txt" en el directorio actual.
     */
    public ProcessingTracker() {
        this("processed_books.txt");
    }

    /**
     * Carga del archivo los IDs de libros ya procesados.
     */
    private void loadProcessedBooks() {
        Path path = Paths.get(trackingFilePath);

        if (!Files.exists(path)) {
            System.out.println("[TRACKER] No previous tracking file found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        int bookId = Integer.parseInt(line);
                        processedBooks.add(bookId);
                    } catch (NumberFormatException e) {
                        System.err.println("[TRACKER] Invalid book ID in tracking file: " + line);
                    }
                }
            }
            System.out.println("[TRACKER] Loaded " + processedBooks.size() + " previously processed books");
        } catch (IOException e) {
            System.err.println("[TRACKER] Error loading tracking file: " + e.getMessage());
        }
    }

    /**
     * Verifica si un libro ya ha sido procesado.
     *
     * @param bookId ID del libro a verificar
     * @return true si el libro ya fue procesado, false si no
     */
    public boolean isProcessed(int bookId) {
        return processedBooks.contains(bookId);
    }

    /**
     * Marca un libro como procesado y lo guarda en el archivo.
     *
     * @param bookId ID del libro procesado
     */
    public void markAsProcessed(int bookId) {
        if (processedBooks.add(bookId)) {
            saveToFile(bookId);
        }
    }

    /**
     * Guarda un book_id en el archivo de seguimiento.
     */
    private void saveToFile(int bookId) {
        try (FileWriter writer = new FileWriter(trackingFilePath, true)) {
            writer.write(bookId + "\n");
        } catch (IOException e) {
            System.err.println("[TRACKER] Error saving book " + bookId + " to tracking file: " + e.getMessage());
        }
    }

    /**
     * Obtiene el número de libros procesados hasta ahora.
     *
     * @return Cantidad de libros procesados
     */
    public int getProcessedCount() {
        return processedBooks.size();
    }

    /**
     * Limpia el registro de libros procesados (solo en memoria, no borra el archivo).
     * Útil para testing.
     */
    public void clear() {
        processedBooks.clear();
    }
}