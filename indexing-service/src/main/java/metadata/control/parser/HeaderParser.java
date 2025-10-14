package metadata.control.parser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeaderParser {
    private final Pattern TITLE_PATTERN = Pattern.compile("Title:\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private final Pattern AUTHOR_PATTERN = Pattern.compile("Author:\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private final Pattern LANGUAGE_PATTERN = Pattern.compile("Language:\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private final Pattern RELEASE_DATE_PATTERN = Pattern.compile("Release Date:\\s*(.+)", Pattern.CASE_INSENSITIVE);
    private static final Map<String, String> LANGUAGE_MAP = new HashMap<>();
    static {
        LANGUAGE_MAP.put("english", "en");
        LANGUAGE_MAP.put("spanish", "es");
        LANGUAGE_MAP.put("french", "fr");
        LANGUAGE_MAP.put("german", "de");
        LANGUAGE_MAP.put("italian", "it");
        LANGUAGE_MAP.put("portuguese", "pt");
        LANGUAGE_MAP.put("dutch", "nl");
        LANGUAGE_MAP.put("russian", "ru");
        LANGUAGE_MAP.put("chinese", "zh");
        LANGUAGE_MAP.put("japanese", "ja");
        LANGUAGE_MAP.put("latin", "la");
        LANGUAGE_MAP.put("greek", "el");
    }

    public Map<String, String> parse(String headerContent, int bookId) {
        Map<String, String> metadata = new HashMap<>();
        String title = extractField(TITLE_PATTERN, headerContent);
        String author = extractField(AUTHOR_PATTERN, headerContent);
        String language = extractField(LANGUAGE_PATTERN, headerContent);
        String releaseDate = extractField(RELEASE_DATE_PATTERN, headerContent);
        if (title != null) metadata.put("title", title);
        if (author != null) metadata.put("author", author);
        if (language != null) metadata.put("language", normalizeLanguage(language));
        if (releaseDate != null) metadata.put("release_date", releaseDate);
        if (title == null || author == null) {
            List<String> missingFields = new ArrayList<>();
            if (title == null) missingFields.add("title");
            if (author == null) missingFields.add("author");
        }
        return metadata;
    }

    private String extractField(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String value = matcher.group(1).strip();
            value = value.replaceAll("[\\[\\]]+$", "").strip();
            return value.isEmpty() ? null : value;
        }
        return null;
    }

    private String normalizeLanguage(String language) {
        String languageLower = language.toLowerCase();
        if (languageLower.length() == 2 && languageLower.matches("[a-z]{2}")) return languageLower;
        for (Map.Entry<String, String> entry : LANGUAGE_MAP.entrySet()) {
            if (languageLower.contains(entry.getKey())) return entry.getValue();
        }
        if (languageLower.length() >= 2) return languageLower.substring(0, 2);
        return languageLower;
    }
}
