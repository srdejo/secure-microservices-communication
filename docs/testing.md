# Estrategia de Pruebas

Este documento detalla la lógica y el enfoque utilizado para asegurar la calidad y fiabilidad de los microservicios.

## 1. La Pirámide de Pruebas

Hemos adoptado la **Pirámide de Pruebas** como modelo para nuestra estrategia, priorizando las pruebas que proporcionan feedback rápido y son fáciles de mantener.

```mermaid
graph TD
    UI[Pruebas E2E / UI - Manual] --- INT[Pruebas de Integración]
    INT --- UNIT[Pruebas Unitarias]
    
    style UNIT fill:#d4edda,stroke:#28a745
    style INT fill:#fff3cd,stroke:#ffc107
    style UI fill:#f8d7da,stroke:#dc3545
```

### Distribución de Pruebas:

1.  **Pruebas Unitarias (Base)**:
    *   **Enfoque**: Lógica de negocio pura en la capa de Dominio y Casos de Uso.
    *   **Aislamiento**: Uso intensivo de Mocks (Mockito) para dependencias de infraestructura.
    *   **Meta**: Cobertura exhaustiva de casos de éxito y manejo de errores.

2.  **Pruebas de Integración (Medio)**:
    *   **Enfoque**: Validar la interacción entre componentes (ej. Adaptador JPA con base de datos real o H2, Feign Clients).
    *   **Infraestructura**: Uso de `@SpringBootTest` y `@DataJpaTest`.
    *   **Meta**: Asegurar que las piezas de infraestructura se comunican correctamente con la base de datos y otros servicios.

3.  **Pruebas E2E / Contrato (Cima)**:
    *   **Enfoque**: Flujos completos desde el controlador hasta la persistencia.
    *   **Herramientas**: RestAssured o MockMvc.

---

## 2. Enfoque en Casos de Uso vs. Cobertura

En lugar de perseguir un porcentaje de cobertura de líneas de código (aunque se mantiene alto por naturaleza), nuestro enfoque principal es el **Testing basado en Casos de Uso**.

### Casos Críticos Cubiertos:

#### Gestión de Productos
- **Éxito**: Creación de un producto con datos válidos.
- **Error**: Intento de obtener un producto inexistente.

#### Proceso de Compra e Inventario
- **Éxito**: Compra exitosa con stock suficiente y producto válido.
- **Validación**: Registro correcto en el historial de compras tras la venta.
- **Error - Stock Insuficiente**: El sistema debe rechazar la compra si la cantidad solicitada supera el stock.
- **Error - Producto No Encontrado**: El sistema debe fallar si el servicio de productos no reconoce el ID solicitado.

#### Comunicación entre Servicios
- **Resiliencia**: Verificación de que el Circuit Breaker y los Reintentos funcionan cuando el servicio externo falla.
- **Integridad**: Mapeo correcto de respuestas JSON API entre servicios.

---

## 3. Ejecución de Pruebas

Para ejecutar las pruebas unitarias y de integración, navega a la carpeta de cada microservicio y ejecuta:

```bash
# En product-service o inventory-service
./gradlew test
```

Para ejecutar todos los tests desde la raíz (si se desea automatizar):
```bash
# Linux/macOS
./product-service/gradlew test && ./inventory-service/gradlew test

# Windows (PowerShell)
cd product-service; ./gradlew test; cd ../inventory-service; ./gradlew test
```

---

## 4. Reportes de Cobertura (JaCoCo)

Una vez ejecutadas las pruebas, puedes visualizar el detalle de la cobertura abriendo los siguientes archivos en tu navegador:

- **Product Service**: [Ver Reporte JaCoCo](../product-service/build/reports/jacoco/test/html/index.html)
- **Inventory Service**: [Ver Reporte JaCoCo](../inventory-service/build/reports/jacoco/test/html/index.html)

*Nota: Los reportes se generan automáticamente en la carpeta `build/reports/jacoco/test/html/` tras ejecutar el comando de test.*
