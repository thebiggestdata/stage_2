package com.thebiggestdata.benchmark;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.results.format.ResultFormatType;

public class BenchmarkRunner {

	public static void main(String[] args) throws Exception {
		Options opt = new OptionsBuilder()
				.include("com.thebiggestdata.benchmark.*")
				.result("benchmark-result.csv")
				.resultFormat(ResultFormatType.CSV)
				.build();

		new Runner(opt).run();
	}
}
