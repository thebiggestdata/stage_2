package com.thebiggestdata.benchmark;

import com.thebiggestdata.searchservice.model.BookInfo;
import com.thebiggestdata.searchservice.model.SearchFilters;
import com.thebiggestdata.searchservice.service.SearchService;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SearchServiceBenchmark {

	private SearchService searchService;
	private List<BookInfo> books;
	private SearchFilters filters;

	@Setup
	public void setup() {
		searchService = new SearchService();
		books = List.of(
				new BookInfo(101, "Libro X", "Autor A", "es", 2018),
				new BookInfo(102, "Libro Y", "Autor B", "es", 2022),
				new BookInfo(103, "Libro Z", "Autor C", "en", 2020)
		);
		filters = new SearchFilters("Autor B", "es", 2022);
	}

	@Benchmark
	public void benchmarkApplyFilters() {
		searchService.applyFilters(books, filters);
	}
}