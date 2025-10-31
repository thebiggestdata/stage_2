package com.thebiggestdata.benchmark;

import com.thebiggestdata.searchservice.repository.InvertedIndexRepository;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;


@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class InvertedIndexBenchmark {

	private InvertedIndexRepository indexRepo;

	@Setup
	public void setup() {
		indexRepo = new InvertedIndexRepository();
		indexRepo.initialize("mockUri", "mockDb", "mockCollection");
	}

	@Benchmark
	public void benchmarkFindBookIdsByTerm() {
		indexRepo.findBookIdsByTerm("ejemplo");
	}

	@TearDown
	public void tearDown() {
		indexRepo.close();
	}
}
