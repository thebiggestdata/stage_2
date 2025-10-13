package model;

import java.util.List;

public class Processor {
    private final Tokenizer tokenizer;
    private final StopWordFilter stopWordFilter;

    public Processor() {
        this.tokenizer = new Tokenizer();
        this.stopWordFilter = new StopWordFilter();
    }

    public Processor(Tokenizer tokenizer, StopWordFilter stopWordFilter) {
        this.tokenizer = tokenizer != null ? tokenizer : new Tokenizer();
        this.stopWordFilter = stopWordFilter != null ? stopWordFilter : new StopWordFilter();
    }

    public List<String> process(String text) {
        List<String> tokens = tokenizer.tokenize(text);
        List<String> filteredTokens = stopWordFilter.filter(tokens);
        return filteredTokens;
    }
}