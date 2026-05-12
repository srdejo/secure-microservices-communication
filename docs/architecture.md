# Arquitectura Técnica del Sistema

Este documento describe la estructura y el flujo de comunicación entre los microservicios, enfocándose en la implementación de la **Arquitectura Hexagonal** y las buenas prácticas de diseño.

## 1. Visión General del Sistema

El sistema consta de dos microservicios principales que interactúan para gestionar productos y procesar compras, protegidos por autenticación basada en **API-Key**.

```mermaid
graph TD
    Client["Swagger / Frontend"] -- "X-API-KEY" --> PS["Product Service"]
    Client -- "X-API-KEY" --> IS["Inventory Service"]
    
    IS -- "Feign Client (v1)" --> PS
    
    subgraph "Persistencia"
        PS --> PDB[("Product DB (Postgres)")]
        IS --> IDB[("Inventory DB (Postgres)")]
    end
```

---

## 2. Arquitectura Hexagonal (Puertos y Adaptadores)

Cada servicio está estructurado para desacoplar la lógica de negocio de los detalles tecnológicos.

### Estructura de Componentes

```mermaid
graph LR
    subgraph "Adaptadores de Entrada (Inbound)"
        CTRL[REST Controller]
    end

    subgraph "Capa de Dominio y Aplicación"
        UC[Use Case / Business Logic]
    end

    subgraph "Adaptadores de Salida (Outbound)"
        REPO[JPA / Persistence Adapter]
        EXT[Feign / External Client]
    end

    CTRL --> UC
    UC --> REPO
    UC --> EXT
```

- **Puertos (Ports)**: Interfaces que definen *qué* puede hacer el sistema (ej. `ProductRepositoryPort`).
- **Adaptadores (Adapters)**: Implementaciones técnicas de esos puertos (ej. `JpaProductAdapter`, `ProductFeignClient`).

---

## 3. Flujo de Compra y Comunicación

El flujo de compra demuestra la interacción entre servicios y la validación transaccional.

```mermaid
sequenceDiagram
    autonumber
    participant Client as Swagger / Frontend
    participant IS as Inventory Service
    participant PS as Product Service
    participant DB as Inventory DB

    Client->>IS: POST /api/v1/inventory/purchase (X-API-KEY)
    IS->>IS: Validar API-Key
    
    IS->>PS: GET /api/v1/products/{id} (Check exists)
    PS-->>IS: 200 OK / 404 Not Found
    
    alt Producto Válido
        IS->>DB: Consultar Stock
        alt Stock Disponible
            IS->>DB: Actualizar Stock & Registrar Compra
            IS-->>Client: 201 Created (JSON API)
        else Stock Insuficiente
            IS-->>Client: 400 Bad Request
        end
    else Producto Inválido
        IS-->>Client: 404 Not Found
    end
```

---

## 4. Seguridad: Autenticación por API-Key

Para asegurar la comunicación, ambos servicios implementan un filtro de seguridad:
- **Header**: `X-API-KEY`
- **Mecanismo**: Interceptor/Filtro en la capa de infraestructura que valida la clave antes de permitir el acceso a los Casos de Uso.
- **Inter-service**: El `Inventory Service` propaga o utiliza una clave válida para comunicarse con el `Product Service`.

---

## 5. Buenas Prácticas y Clean Code

- **Modelos de Dominio Puros**: Entidades sin anotaciones de persistencia (cuando es posible) o aisladas del modelo JPA.
- **Mapeo Explícito**: Uso de **MapStruct** para transformar datos entre capas (DTO -> Domain -> Entity).
- **Inmutabilidad**: Uso de objetos inmutables y UUIDs para identificadores.
- **Resiliencia**: Implementación de **Circuit Breaker** y **Retry** en las llamadas entre servicios para manejar fallos transitorios.
