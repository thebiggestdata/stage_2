package com.thebiggestdata.benchmark;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import com.thebiggestdata.indexing.index.model.Tokenizer;


@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@Fork(1)
@State(Scope.Benchmark)
public class TokenizerBenchmark {

    private Tokenizer tokenizer;
    private String text;

    // Diferentes tamaños de texto a probar
    @Param({"1000", "10000", "100000"})
    private int textLength;

    @Setup(Level.Trial)
    public void setup() {
        tokenizer = new Tokenizer();
        String base = "Alice was beginning to get very tired of sitting by her sister ";
        StringBuilder sb = new StringBuilder();
        while (sb.length() < textLength) {
            sb.append(base);
        }
        text = sb.toString();
    }

    @Benchmark
    public Object benchmarkTokenize() {
        return tokenizer.tokenize(text);
    }
}

