package api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IndexStatusDto(
        @JsonProperty("books_indexed")
        int booksIndexed,

        @JsonProperty("unique_terms")
        int uniqueTerms,

        @JsonProperty("last_update")
        String lastUpdate,

        @JsonProperty("index_size_mb")
        double indexSizeMB,

        @JsonProperty("index_type")
        String indexType,

        @JsonProperty("metadata_storage_type")
        String metadataStorageType
) {}