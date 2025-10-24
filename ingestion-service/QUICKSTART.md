# Ingestion Service - Guía Rápida

## 🚀 Inicio Rápido

### Opción 1: API REST (Recomendado)

```bash
# Compilar y ejecutar
mvn spring-boot:run
```

Acceder a:
- **API**: http://localhost:8080/api/v1/ingestion
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

### Opción 2: Línea de Comandos (CLI)

```bash
# Compilar
mvn clean package

# Ejecutar (startId, endId, delayMs)
java -jar target/ingestion-service.jar 1 100 1000
```

## 📡 Endpoints de la API

### 1. Descargar un libro específico
```bash
curl -X POST http://localhost:8080/api/v1/ingestion/download/42
```

### 2. Descargar el siguiente libro
```bash
curl -X POST http://localhost:8080/api/v1/ingestion/download/next
```

### 3. Crawlear un rango de libros
```bash
curl -X POST "http://localhost:8080/api/v1/ingestion/crawl?startId=1&endId=20"
```

### 4. Ver estado del crawler
```bash
curl http://localhost:8080/api/v1/ingestion/status
```

### 5. Actualizar configuración
```bash
curl -X PUT "http://localhost:8080/api/v1/ingestion/config?startId=1&endId=1000&delay=2000"
```

## 📂 Estructura de Salida

Los libros se guardan en:
```
datalake/
  YYYYMMDD/
    HH/
      {bookId}.header.txt
      {bookId}.body.txt
```

Ejemplo:
```
datalake/20251024/14/1.header.txt
datalake/20251024/14/1.body.txt
```

## 🔧 Configuración

Edita `src/main/resources/application.properties`:

```properties
server.port=8080
crawler.default.start-id=1
crawler.default.end-id=1000
crawler.default.delay-ms=1000
datalake.base-path=datalake/
```

## 📝 Respuestas de la API

### Éxito (200 OK)
```json
{
  "success": true,
  "headerPath": "datalake/20251024/14/1.header.txt",
  "bodyPath": "datalake/20251024/14/1.body.txt",
  "timestamp": "2025-10-24T14:30:00"
}
```

### Error (500)
```json
{
  "success": false,
  "headerPath": null,
  "bodyPath": null,
  "timestamp": "2025-10-24T14:30:00"
}
```

### Puerto 8080 en uso
Cambia el puerto en `application.properties`:
```properties
server.port=8081
```
