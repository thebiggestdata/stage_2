package com.thebiggestdata.application.config;

import com.thebiggestdata.application.usecase.BookIngestionUseCase;
import com.thebiggestdata.domain.port.out.BookFetcherPort;
import com.thebiggestdata.domain.port.out.BookStoragePort;
import com.thebiggestdata.domain.port.out.FileSystemPort;
import com.thebiggestdata.domain.service.BookIngestionService;
import com.thebiggestdata.infrastructure.adapter.in.CrawlerController;
import com.thebiggestdata.infrastructure.adapter.out.external.HttpBookFetcher;
import com.thebiggestdata.infrastructure.adapter.out.persistence.BookStorer;
import com.thebiggestdata.infrastructure.adapter.out.persistence.FileSystemBookRepository;
import com.thebiggestdata.infrastructure.util.DatalakePathBuilder;


public class DependencyFactory {

    public static CrawlerController createCrawlerController(CrawlerConfig config, String datalakeBasePath) {
        BookFetcherPort bookFetcherPort = new HttpBookFetcher();
        DatalakePathBuilder pathBuilder = new DatalakePathBuilder(datalakeBasePath);
        FileSystemPort fileSystemPort = new BookStorer(pathBuilder);
        BookStoragePort bookStoragePort = new FileSystemBookRepository(datalakeBasePath);
        BookIngestionService bookIngestionService = new BookIngestionService(
            bookFetcherPort,
            fileSystemPort,
            bookStoragePort
        );
        BookIngestionUseCase bookIngestionUseCase = new BookIngestionUseCase(bookIngestionService);
        return new CrawlerController(config, bookIngestionUseCase);
    }
}

