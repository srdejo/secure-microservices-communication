# Arquitectura del Sistema

Este documento describe la arquitectura del sistema de microservicios utilizando el modelo C4 para proporcionar diferentes niveles de abstracción.

## 1. Nivel 1: Diagrama de Contexto del Sistema

Muestra el sistema en su conjunto y cómo interactúa con los usuarios y otros sistemas.

```mermaid
C4Context
    title Diagrama de Contexto del Sistema de E-commerce
    
    Person(customer, "Cliente", "Un usuario que desea comprar productos y gestionar inventario.")
    System(ecommerce_system, "Sistema de Microservicios", "Permite la gestión de productos, inventario y procesamiento de compras.")
    
    Rel(customer, ecommerce_system, "Usa", "HTTPS/JSON API")
```

## 2. Nivel 2: Diagrama de Contenedores

Desglosa el sistema en contenedores (aplicaciones, bases de datos, etc.).

```mermaid
C4Container
    title Diagrama de Contenedores del Sistema
    
    Person(customer, "Cliente", "Un usuario que desea comprar productos y gestionar inventario.")
    
    System_Boundary(c1, "Sistema de Microservicios") {
        Container(product_service, "Product Service", "Java, Spring Boot", "Gestiona el catálogo de productos y su información base.")
        Container(inventory_service, "Inventory Service", "Java, Spring Boot", "Gestiona el stock, transacciones de compra y validación de inventario.")
        
        ContainerDb(product_db, "Product DB", "PostgreSQL", "Almacena datos de productos.")
        ContainerDb(inventory_db, "Inventory DB", "PostgreSQL", "Almacena stock e historial de compras.")
    }
    
    Rel(customer, product_service, "Consulta productos", "JSON API")
    Rel(customer, inventory_service, "Realiza compras y consulta stock", "JSON API")
    
    Rel(inventory_service, product_service, "Valida existencia de producto", "Feign (HTTP)")
    
    Rel(product_service, product_db, "Lee/Escribe", "JPA")
    Rel(inventory_service, inventory_db, "Lee/Escribe", "JPA")
```

## 3. Nivel 3: Diagrama de Componentes (Zoom-In)

### Product Service: CRUD de Productos

Muestra los componentes internos que facilitan la gestión de productos siguiendo la Arquitectura Hexagonal.

```mermaid
C4Component
    title Diagrama de Componentes - Product Service (CRUD)
    
    Container_Boundary(ps, "Product Service") {
        Component(controller, "Product Controller", "Spring REST", "Expone endpoints JSON API para CRUD.")
        Component(usecase, "Product UseCase", "Service Layer", "Orquestra la lógica de negocio de productos.")
        Component(port, "Product Repository Port", "Interface", "Define operaciones de persistencia.")
        Component(adapter, "JPA Adapter", "Spring Data JPA", "Implementa persistencia en PostgreSQL.")
    }
    
    Rel(controller, usecase, "Llama")
    Rel(usecase, port, "Usa")
    Rel(adapter, port, "Implementa")
```

### Inventory Service: Funcionalidad de Compra

Muestra cómo se orquestan los componentes para procesar una compra.

```mermaid
C4Component
    title Diagrama de Componentes - Inventory Service (Compra)
    
    Container_Boundary(is, "Inventory Service") {
        Component(inv_controller, "Inventory Controller", "Spring REST", "Endpoint de compra.")
        Component(inv_usecase, "Inventory UseCase", "Service Layer", "Lógica transaccional de compra.")
        Component(inv_port, "Inventory Port", "Interface", "Persistencia de stock y compras.")
        Component(prod_client, "Product Client Port", "Interface", "Puerto para comunicación externa.")
        Component(feign_adapter, "Feign Adapter", "Resilience4j", "Llamadas resilientes al Product Service.")
    }
    
    Rel(inv_controller, inv_usecase, "Procesa compra")
    Rel(inv_usecase, prod_client, "Valida producto")
    Rel(inv_usecase, inv_port, "Actualiza stock y registra compra")
    Rel(feign_adapter, prod_client, "Implementa")
```

## 4. Diagrama de Interacción: Proceso de Compra

Diagrama de secuencia detallado de una interacción exitosa de compra.

```mermaid
sequenceDiagram
    autonumber
    participant Client as Cliente
    participant IC as Inventory Controller
    participant IU as Inventory UseCase
    participant PC as Product Service
    participant IDB as Inventory DB

    Client->>IC: POST /inventory/purchase {productId, quantity}
    IC->>IU: processPurchase(productId, quantity)
    
    Note over IU, PC: Validación Externa
    IU->>PC: GET /products/{id}
    alt Producto Existe
        PC-->>IU: 200 OK (Product Info)
    else Producto No Existe
        PC-->>IU: 404 Not Found
        IU-->>IC: throw ProductNotFoundException
        IC-->>Client: 404 Error
    end

    Note over IU, IDB: Gestión de Stock
    IU->>IDB: Find Inventory for Product
    IDB-->>IU: Inventory Object
    
    IU->>IU: validateStock(quantity)
    alt Stock Suficiente
        IU->>IDB: Save Updated Stock & New Purchase
        IDB-->>IU: Success
        IU-->>IC: Purchase Confirmation
        IC-->>Client: 201 Created (JSON API)
## 5. Estrategia de Versionado de API

Para asegurar la evolución del sistema sin romper la compatibilidad con clientes existentes, se ha adoptado una estrategia de **versionado por ruta (Path Versioning)**.

- **Formato**: `/api/v{n}/[recurso]`
- **Estado Actual**: `v1` implementado en todos los endpoints públicos.
- **Transición**: Cuando se introduzcan cambios disruptivos (breaking changes), se desplegará una versión `v2` manteniendo `v1` activa durante un periodo de deprecación.

---

## 6. Propuesta de Escalabilidad y Mejoras

Como parte de la visión de Líder Técnico, se proponen las siguientes mejoras para el futuro:

1.  **Patrón Saga**: Implementar coreografía de eventos para manejar transacciones distribuidas entre Producto e Inventario de forma asíncrona.
2.  **API Gateway**: Introducir un punto de entrada único para centralizar seguridad, métricas y ruteo.
3.  **Observabilidad Distribuida**: Integrar OpenTelemetry para trazado de peticiones entre servicios (Distributed Tracing).
