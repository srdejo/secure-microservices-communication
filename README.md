# Microservicios de Productos e Inventario - Linktic

Solución robusta de microservicios basada en **Arquitectura Hexagonal (DDD)**, diseñada para escalabilidad, resiliencia y mantenibilidad.

## 1. Instalación y Ejecución

**Requisitos**: Docker y Docker Compose.

```bash
# Levantar toda la infraestructura y servicios
docker-compose up --build
```
*Los servicios estarán disponibles en los puertos 8081 (Products) y 8082 (Inventory). Ver [Swagger](http://localhost:8081/api/v1/swagger-ui/index.html).*

---

## 2. Arquitectura y Flujo

El sistema sigue una **Arquitectura Hexagonal** pura para desacoplar la lógica de negocio de la infraestructura.

- **Diagramas Detallados (C4 y Secuencia)**: [Ver docs/architecture.md](docs/architecture.md)
- **Estrategia de Pruebas**: [Ver docs/testing.md](docs/testing.md)

### Resumen del Flujo de Compra:
1. El cliente solicita una compra al `Inventory Service`.
2. El servicio valida la existencia del producto mediante el `Product Service` (vía Feign con Resilience4j).
3. Se verifica el stock disponible.
4. Se descuenta el stock, se registra la transacción y se emite un evento de dominio.

---

## 3. Decisiones Técnicas y Justificaciones

| Decisión | Justificación |
| :--- | :--- |
| **Arquitectura Hexagonal** | Facilita el testing, mantiene el dominio puro y permite cambiar de base de datos o frameworks sin afectar el negocio. |
| **PostgreSQL** | Garantiza transaccionalidad ACID e integridad referencial, críticas para la gestión de inventario. |
| **JSON:API Standard** | Proporciona una estructura de respuesta consistente y optimizada para relaciones entre recursos. |
| **Endpoint de Compra en Inventory** | **Justificación Clave**: El inventario es el recurso "dueño" de la existencia y la transacción. Centralizar la lógica aquí garantiza atomicidad y evita que el catálogo de productos se ensucie con lógica transaccional de ventas. |

---

## 4. Documentación y Metodología con IA

El desarrollo de este proyecto se realizó bajo un esquema de **Pair Programming con IA**, utilizando herramientas avanzadas (**Antigravity**) para garantizar un código de nivel Senior que cumple con los requerimientos técnicos y arquitectónicos.

### Proceso de Colaboración:
- **Planeación Estratégica**: Se utilizaron herramientas de IA para planificar las funcionalidades y definir la estructura de microservicios, asegurando que cada componente cumpliera con una responsabilidad única.
- **Directrices de Clean Code**: Se establecieron reglas estrictas de código limpio y diseño orientado al dominio (DDD). Se iteró en múltiples ocasiones para lograr **controladores delgados**, una **capa de dominio libre de frameworks** (POJOs puros) y una clara separación de intereses.
- **Arquitectura y Refactorización**: La IA asistió en la transición hacia una Arquitectura Hexagonal robusta, facilitando refactorizaciones complejas (como la migración a UUIDs) para asegurar la integridad del sistema distribuido.
- **Estrategia de Pruebas**: Se implementó una **Pirámide de Pruebas** dirigida por la IA, priorizando pruebas unitarias de lógica de negocio y pruebas de integración para los adaptadores de infraestructura.

### Verificación de Calidad:
La calidad no fue delegada, sino supervisada: cada sugerencia fue validada mediante revisiones de código línea por línea, verificada mediante una suite de pruebas automatizadas y validada contra los requerimientos funcionales originales.

---

*Desarrollado para la Prueba Técnica de Linktic - Senior/Tech Lead Edition.*
