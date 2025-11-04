package com.thebiggestdata.benchmark;

import com.thebiggestdata.search.model.BookInfo;
import com.thebiggestdata.search.model.SearchFilters;
import com.thebiggestdata.search.api.service.SearchService;
import org.openjdk.jmh.annotations.*;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SearchServiceBenchmark {

	private SearchService searchService;
	private List<BookInfo> books;
	private SearchFilters filters;

	@Param({"1000", "10000", "100000"})
	private int booksCount;

	@Setup
	public void setup() {
		searchService = new SearchService();
		books = new ArrayList<>();
		for (int i = 0; i < booksCount; i++) {
			books.add(new BookInfo(i, "Libro" + i, "Autor" + (i % 5), i % 2 == 0 ? "es" : "en", 2018 + (i % 5)));
		}
		filters = new SearchFilters("Autor2", "es", 2022);
	}

	@Benchmark
	public void benchmarkApplyFilters() {
		searchService.applyFilters(books, filters);
	}
}
