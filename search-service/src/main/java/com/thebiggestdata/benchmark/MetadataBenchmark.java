package com.thebiggestdata.benchmark;

import com.thebiggestdata.searchservice.repository.MetadataRepository;
import org.bson.Document;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class MetadataBenchmark {

	private MetadataRepository metadataRepo;
	private Document testDoc;

	@Setup
	public void setup() {
		metadataRepo = new MetadataRepository();
		metadataRepo.initialize("mockUri", "mockDb", "mockCollection");
		testDoc = new Document()
				.append("bookid", 123)
				.append("title", "Título de ejemplo")
				.append("author", "Autor Ejemplo")
				.append("language", "es")
				.append("releasedate", "2020");
	}

	@Benchmark
	public void benchmarkDocumentToBookInfo() {
		metadataRepo.documentToBookInfo(testDoc);
	}

	@TearDown
	public void tearDown() {
		metadataRepo.close();
	}
}
