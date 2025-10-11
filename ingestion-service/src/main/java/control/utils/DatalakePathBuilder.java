package control.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatalakePathBuilder {
    private final String path = "datalake/";

    private String createBookDir() {
        String datalakePath;
        LocalDateTime timestamp = LocalDateTime.now();
        String dateStr = timestamp.format(DateTimeFormatter.ofPattern("%Y%m%d"));
        String hourStr = timestamp.format(DateTimeFormatter.ofPattern("%H"));
        datalakePath = path + dateStr + "/" + hourStr + "/";
        return datalakePath;
    }
}
