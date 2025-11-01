package ulpgc.bd.thebiggestdata.indexing.benchmark;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

import indexing.model.Tokenizer;
import indexing.model.StopWordFilter;

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
        tokenizer = new Tokenizer();       // tu implementación real
        filter    = new StopWordFilter();  // tu implementación real

        String base = "Alice was beginning to get very tired of sitting by her sister ";
        StringBuilder sb = new StringBuilder();
        while (sb.length() < textLength) sb.append(base);
        text = sb.toString();

        // PRE: tokenizamos una sola vez para aislar el coste de filtrar
        tokens = new ArrayList<>(tokenizer.tokenize(text)); // <-- si tu método se llama distinto, cambia esta línea
    }

    @Benchmark
    public Object benchmarkFilter() {
        // Filtrado puro (si tu API devuelve nueva lista, usa ese retorno)
        // Cambia 'filter(tokens)' si tu método se llama distinto (p.ej. apply/remove/clean)
        return filter.filter(tokens);
    }
}
