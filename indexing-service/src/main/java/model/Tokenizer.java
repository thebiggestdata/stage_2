package model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private final Pattern pattern;

    public Tokenizer() {
        this.pattern = Pattern.compile("\\b[a-zA-Z0-9']+\\b");
    }

    public List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            tokens.add(matcher.group().toLowerCase());
        }
        return tokens;
    }
}
