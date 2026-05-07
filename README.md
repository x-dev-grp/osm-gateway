# API Gateway (osm-gateway)

The entry point for all client requests. Handles routing, load balancing, and global CORS configuration.

## 🛠 Tech Stack
- **Framework:** Spring Cloud Gateway
- **Discovery:** Netflix Eureka
- **Security:** Spring Security OAuth2 (Resource Server)

## 🚦 Routing Rules
| Path | Target Service |
| :--- | :--- |
| `/api/security/**` | `security-service` |
| `/api/production/**` | `oilproductionservice` |
| `/api/finance/**` | `finance-service` |
| `/api/hr/**` | `hr-service` |
| `/api/inventaire/**` | `inventory-service` |

## 🚀 Getting Started
```bash
./mvnw spring-boot:run
```

## ⚙️ Configuration
| Variable | Default | Description |
| :--- | :--- | :--- |
| `SERVER_PORT` | `8084` | Gateway port |
| `EUREKA_CLIENT_SERVICEURL_DEFAULTZONE` | `http://localhost:8761/eureka/` | Eureka Server URL |
| `OAUTH2_ISSUER_URI` | `http://localhost:8088` | Security Service Issuer |
