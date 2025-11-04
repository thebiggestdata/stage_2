package com.thebiggestdata.benchmark;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

import com.thebiggestdata.indexing.index.model.Tokenizer;
import com.thebiggestdata.indexing.index.model.StopWordFilter;

/**
 * Mide SOLO el coste de filtrar stopwords.
 * Los tokens se preparan en @Setup para no mezclar tokenización con filtrado.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@Fork(1)
@State(Scope.Benchmark)
public class StopWordFilterBenchmark {

    private Tokenizer tokenizer;
    private StopWordFilter filter;
    private String text;
    private List<String> tokens; // precomputados en setup

    @Param({"1000", "10000", "100000"})
    private int textLength;

    @Setup(Level.Trial)
    public void setup() {
        tokenizer = new Tokenizer();
        filter    = new StopWordFilter();

        String base = "Alice was beginning to get very tired of sitting by her sister ";
        StringBuilder sb = new StringBuilder();
        while (sb.length() < textLength) sb.append(base);
        text = sb.toString();

        // PRE: tokenizamos una sola vez para aislar el coste de filtrar
        tokens = new ArrayList<>(tokenizer.tokenize(text));
    }

    @Benchmark
    public Object benchmarkFilter() {

        return filter.filter(tokens);
    }
}
