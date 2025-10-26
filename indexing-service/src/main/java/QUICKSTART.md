# Indexing Service - Guía Rápida

## 🚀 Inicio Rápido

### Compilar y ejecutar
```bash
mvn spring-boot:run
```

Acceder a:
- **API**: http://localhost:8081/api/v1/indexing-service
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **Health Check**: http://localhost:8081/actuator/health

## 📡 Endpoints de la API

### 1. Indexar un libro específico
```bash
curl -X POST "http://localhost:8081/api/v1/indexing-service/index/update/42?downloadDate=20251024&downloadHour=14"
```

### 2. Reconstruir todo el índice
```bash
curl -X POST http://localhost:8081/api/v1/indexing-service/index/rebuild
```

### 3. Ver estado del índice
```bash
curl http://localhost:8081/api/v1/indexing-service/index/status
```

## 📝 Respuestas de la API

### Index Update (200 OK)
```json
{
  "book_id": 42,
  "success": true,
  "status": "indexed",
  "message": "Book successfully indexed"
}
```

### Index Status (200 OK)
```json
{
  "books_indexed": 150,
  "unique_terms": 45000,
  "last_update": "2025-10-26T14:30:00Z",
  "index_size_mb": 45.0,
  "index_type": "MongoInvertedIndex",
  "metadata_storage_type": "MongoDbStorage"
}
```

### Rebuild Result (200 OK)
```json
{
  "books_processed": 150,
  "books_failed": 0,
  "elapsed_time": "45.3s",
  "message": "Rebuild completed"
}
```

## ⚙️ Configuración

Edita `src/main/resources/application.properties`:
```properties
server.port=8081
mongodb.connection-string=mongodb://localhost:27017/
datalake.base-path=datalake/
```

## 🔧 Requisitos
- Java 21
- MongoDB corriendo en localhost:27017
- Datalake con libros descargados