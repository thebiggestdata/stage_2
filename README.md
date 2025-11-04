# Stage 2: Service-Oriented Architecture

Distributed search engine with microservices for Project Gutenberg books.

## 📋 Description

Service-oriented architecture with three independent microservices:
- **Ingestion Service** (8080): Downloads books from Project Gutenberg
- **Indexing Service** (8081): Processes text and builds inverted indexes
- **Search Service** (8082): REST API for keyword search with filters
- **Control Module**: Orchestrates ingestion → indexing workflow

## 🏗️ Architecture
```
stage_2/
├── ingestion-service/    # Port 8080
├── indexing-service/     # Port 8081  
├── search-service/       # Port 8082
├── control-module/       # Orchestrator
└── datalake/            # YYYYMMDD/HH/ structure
```

## 🔧 Prerequisites

- **Java 21+**
- **Maven 3.8+**
- **MongoDB 7.0+** (local, port 27017)
```bash
java -version   # Verify Java 21
mvn -version    # Verify Maven
mongosh         # Test MongoDB connection
```

## 📦 Installation
```bash
git clone https://github.com/thebiggestdata/stage_2.git
cd stage_2

# Build all services
cd ingestion-service && mvn clean package && cd ..
cd indexing-service && mvn clean package && cd ..
cd search-service && mvn clean package && cd ..
cd control-module && mvn clean package && cd ..
```

## 🚀 Usage

### Start Services (3 separate terminals)

**Terminal 1 - Ingestion Service:**
```bash
cd ingestion-service
java -jar target/ingestion-service-1.0-SNAPSHOT.jar
```

**Terminal 2 - Indexing Service:**
```bash
cd indexing-service
java -jar target/indexing-service-1.0-SNAPSHOT.jar
```

**Terminal 3 - Search Service:**
```bash
cd search-service
java -jar target/search-service-1.0-SNAPSHOT.jar
```

Wait for all services to show "Started Application in X seconds"

### Run Control Module (Terminal 4)

Process books 10-50:
```bash
cd control-module
java -jar target/control-module-1.0-SNAPSHOT.jar 10 50
```

**Note:** Already processed books are automatically skipped (tracked in `processed_books.txt`)

### Query API
```bash
# Keyword search
curl "http://localhost:8082/api/v1/search?q=adventure"

# Filter by author
curl "http://localhost:8082/api/v1/search-service?q=adventure&author=Jane%20Austen"

# Filter by language
curl "http://localhost:8082/api/v1/search-service?q=adventure&language=en"

# Combined filters
curl "http://localhost:8082/api/v1/search-service?q=adventure&author=Jane%20Austen&language=en&year=1813"
```

**Response:**
```json
{
  "query": "adventure",
  "filters": {},
  "count": 25,
  "results": [
    {"bookId": 5, "title": "Robinson Crusoe", "author": "Daniel Defoe", 
     "language": "en", "year": 1719}
  ]
}
```

## 📊 API Endpoints

### Ingestion (8080)
- `POST /api/v1/ingestion-service/download/{book_id}` - Download book
- `GET /api/v1/ingestion-service/status` - Service status

### Indexing (8081)
- `POST /api/v1/indexing-service/index/update/{book_id}` - Index book
- `POST /api/v1/indexing-service/index/rebuild` - Rebuild entire index
- `GET /api/v1/indexing-service/index/status` - Index statistics

### Search (8082)
- `GET /api/v1/search?q={term}` - Keyword search
- `GET /api/v1/search?q={term}&author={name}` - Filter by author
- `GET /api/v1/search?q={term}&language={code}` - Filter by language (ISO 639-1)
- `GET /api/v1/search?q={term}&year={YYYY}` - Filter by year

## 🔍 Swagger UI

Access interactive API documentation:
- Ingestion: http://localhost:8080/swagger-ui.html
- Indexing: http://localhost:8081/swagger-ui.html
- Search: http://localhost:8082/swagger-ui.html

## 📝 Tech Stack

- Java 21 (LTS)
- Spring Boot 3.2.0
- Jackson (JSON serialization)
- MongoDB 7.0 (inverted index + metadata)
- Maven 3.8+

## 👥 Authors

**The Biggest Data Team**
- Juan Diego González Noguera
- Pablo Herrera González
- Jaime Ercilla Martín
- Miguel Cabeza Lantigua
- Alejandro Hernández de León

## 📄 License

Academic project - Big Data course, Universidad de Las Palmas de Gran Canaria (2025-2026)

---

**Repository:** https://github.com/thebiggestdata/stage_2