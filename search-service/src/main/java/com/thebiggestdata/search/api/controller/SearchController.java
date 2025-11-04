package com.thebiggestdata.search.api.controller;

import com.thebiggestdata.search.api.dto.SearchResponseDto;
import com.thebiggestdata.search.model.SearchFilters;
import com.thebiggestdata.search.api.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/search-service")
@Tag(name = "Search", description = "Endpoints para búsqueda de libros con filtros opcionales")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    @Operation(
            summary = "Buscar libros por término",
            description = "Busca libros que contengan el término especificado, con filtros opcionales por autor, idioma y año"
    )
    public ResponseEntity<SearchResponseDto> search(
            @Parameter(description = "Término de búsqueda", required = true, example = "adventure")
            @RequestParam("q") String query,

            @Parameter(description = "Filtro por autor (opcional)", example = "Jane Austen")
            @RequestParam(value = "author", required = false) String author,

            @Parameter(description = "Filtro por idioma ISO 639-1 (opcional)", example = "en")
            @RequestParam(value = "language", required = false) String language,

            @Parameter(description = "Filtro por año de publicación (opcional)", example = "1813")
            @RequestParam(value = "year", required = false) Integer year
    ) {
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        var filters = new SearchFilters(author, language, year);
        var result = searchService.search(query.trim(), filters);

        var response = new SearchResponseDto(
                result.query(),
                result.filters(),
                result.count(),
                result.results()
        );

        return ResponseEntity.ok(response);
    }
}