package ulpgc.bd.thebiggestdata.indexing.benchmark;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import indexing.model.Tokenizer;


@BenchmarkMode(Mode.AverageTime)       // Medimos el tiempo promedio por operación
@OutputTimeUnit(TimeUnit.MILLISECONDS) // Mostramos resultados en milisegundos
@Warmup(iterations = 5)                // 5 iteraciones de calentamiento (JIT)
@Measurement(iterations = 10)          // 10 iteraciones de medida real
@Fork(1)                               // 1 proceso JVM
@State(Scope.Benchmark)                // Estado compartido entre iteraciones
public class TokenizerBenchmark {

    private Tokenizer tokenizer;
    private String text;

    // Diferentes tamaños de texto a probar
    @Param({"1000", "10000", "100000"})
    private int textLength;

    @Setup(Level.Trial)
    public void setup() {
        tokenizer = new Tokenizer(); // Usa tu implementación real
        // Generamos un texto sintético del tamaño indicado
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

