package com.thebiggestdata.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IngestionResponse(
        @JsonProperty("book_id") int bookId,
        String status,
        String path
) {
    public boolean isSuccess() {
        // Consideramos éxito si descargó o ya estaba descargado
        return "downloaded".equals(status) || "already_downloaded".equals(status);
    }

    // Para compatibilidad con el código existente
    public String getDate() {
        // Si tu API devuelve timestamp, ajusta esto
        // Por ahora retornamos formato simple YYYYMMDD
        return java.time.LocalDate.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")
        );
    }

    public String getHour() {
        // ✅ CAMBIO: Retornar con cero a la izquierda (formato HH)
        return java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH")
        );
    }

    public String getTimestamp() {
        return getDate() + "/" + getHour();
    }
}