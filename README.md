# Veranum Core Pago

Microservicio encargado de la gestión de pagos del sistema hotelero **Veranum Core**.

Este servicio permite registrar, consultar, actualizar y eliminar pagos asociados a las reservas del sistema. Antes de registrar un pago, se comunica mediante HTTP con el microservicio de reservas para verificar que la reserva exista, que no esté cancelada y que el monto ingresado corresponda al costo total de la reserva.

## Tecnologías utilizadas

- Java 17
- Spring Boot 4.1.2
- Spring Web
- Spring Data JPA
- Hibernate
- Bean Validation
- MySQL
- Maven
- RestClient
- Springdoc OpenAPI / Swagger UI
- Postman
- Git y GitHub

## Arquitectura

El microservicio utiliza una arquitectura por capas:

```text
controller
    ↓
service
    ↓
repository
    ↓
MySQL
```

Para validar las reservas utiliza comunicación REST entre microservicios:

```text
Cliente / Postman
        ↓
veranum_corepago :8084
        ↓
PagoController
        ↓
PagoService
        ↓
ReservaClient
        ↓ HTTP
veranum_corereserva :8082
```

El microservicio de pagos no accede directamente a la base de datos de reservas.

Cada microservicio mantiene su propia base de datos.

## Puerto

```text
8084
```

URL base:

```text
http://localhost:8084
```

## Base de datos

Base de datos utilizada:

```text
veranum_pago
```

Configuración principal:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/veranum_pago?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=${DB_USER:root}
spring.datasource.password=${DB_PASSWORD:}
```

Hibernate administra automáticamente las tablas mediante:

```properties
spring.jpa.hibernate.ddl-auto=update
```

## Entidad Pago

La tabla principal es:

```text
pagos
```

Campos:

| Campo | Descripción |
|---|---|
| id | Identificador único del pago |
| reserva_id | Identificador de la reserva asociada |
| monto | Monto total del pago |
| metodo_pago | Método utilizado para pagar |
| estado | Estado actual del pago |
| fecha_pago | Fecha y hora de registro |

### Métodos de pago permitidos

```text
CREDITO
DEBITO
TRANSFERENCIA
```

### Estados permitidos

```text
PENDIENTE
PAGADO
RECHAZADO
```

## Comunicación con Reserva

El servicio de Pago se comunica con:

```text
http://localhost:8082
```

Configuración:

```properties
reserva.service.base-url=${RESERVA_SERVICE_BASE_URL:http://localhost:8082}
```

Para consultar una reserva utiliza:

```http
GET /api/v1/reservas/{id}
```

Antes de crear un pago se comprueba:

1. Que la reserva exista.
2. Que todavía no exista un pago asociado.
3. Que la reserva no esté cancelada.
4. Que posea un costo total válido.
5. Que el monto enviado coincida con el costo total de la reserva.

## Endpoints

### Registrar pago

```http
POST /api/v1/pagos
```

Ejemplo:

```json
{
  "reservaId": 1,
  "monto": 90000,
  "metodoPago": "DEBITO"
}
```

Respuesta correcta:

```text
201 Created
```

### Listar pagos

```http
GET /api/v1/pagos
```

Respuesta:

```text
200 OK
```

### Obtener pago por ID

```http
GET /api/v1/pagos/{id}
```

Ejemplo:

```http
GET /api/v1/pagos/1
```

### Obtener pagos por reserva

```http
GET /api/v1/pagos/reserva/{reservaId}
```

Ejemplo:

```http
GET /api/v1/pagos/reserva/1
```

### Actualizar estado

```http
PUT /api/v1/pagos/{id}/estado
```

Ejemplo:

```json
{
  "estado": "RECHAZADO"
}
```

### Eliminar pago

```http
DELETE /api/v1/pagos/{id}
```

Ejemplo:

```http
DELETE /api/v1/pagos/2
```

## Formato estándar de respuestas

Ejemplo exitoso:

```json
{
  "codigo": 200,
  "mensaje": "Pago encontrado",
  "error": false,
  "data": {}
}
```

Ejemplo de error:

```json
{
  "codigo": 404,
  "mensaje": "Pago no encontrado con ID: 999",
  "error": true,
  "data": null
}
```

## Validaciones implementadas

El microservicio valida:

- ID de reserva obligatorio.
- Monto obligatorio.
- Monto mayor que cero.
- Método de pago obligatorio.
- Método de pago válido.
- Existencia de la reserva.
- Reserva no cancelada.
- Coincidencia entre monto y costo total.
- Prevención de pagos duplicados.
- Estados de pago válidos.

## Pruebas realizadas en Postman

Se comprobaron los siguientes casos:

| Prueba | Resultado |
|---|---|
| Registrar pago correcto | 201 Created |
| Listar pagos | 200 OK |
| Buscar pago por ID | 200 OK |
| Buscar pagos por reserva | 200 OK |
| Pago duplicado | 400 Bad Request |
| Método CHEQUE no permitido | 400 Bad Request |
| Pago inexistente | 404 Not Found |
| Monto distinto al costo de reserva | 400 Bad Request |
| Actualización de estado | 200 OK |
| Eliminación de pago | 200 OK |
| Consultar pago eliminado | 404 Not Found |

## Swagger / OpenAPI

Swagger UI:

```text
http://localhost:8084/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8084/v3/api-docs
```

Swagger documenta los endpoints correspondientes a:

- Registrar un pago.
- Listar todos los pagos.
- Buscar pago por ID.
- Buscar pagos por reserva.
- Actualizar estado.
- Eliminar pago.

## Requisitos

Antes de ejecutar el proyecto se requiere:

- Java 17.
- MySQL.
- Maven Wrapper incluido en el proyecto.
- Base de datos `veranum_pago`.
- Microservicio de reservas disponible en el puerto 8082 para registrar pagos.

Comprobar Java:

```powershell
java -version
```

Si es necesario configurar Java 17 en PowerShell:

```powershell
$env:JAVA_HOME = "C:\Users\Usuario\.jdks\ms-17.0.19"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
```

## Crear base de datos

En MySQL:

```sql
CREATE DATABASE veranum_pago;
```

No es necesario crear manualmente la tabla `pagos`, ya que Hibernate la genera automáticamente.

## Compilar

```powershell
.\mvnw.cmd clean compile
```

## Ejecutar pruebas

```powershell
.\mvnw.cmd test
```

Resultado comprobado:

```text
Tests run: 1
Failures: 0
Errors: 0
Skipped: 0
BUILD SUCCESS
```

## Generar JAR

```powershell
.\mvnw.cmd clean package
```

Artefacto generado:

```text
target\veranum_corepago-0.0.1-SNAPSHOT.jar
```

## Instalar con Maven

```powershell
.\mvnw.cmd install
```

## Ejecutar mediante Spring Boot

```powershell
.\mvnw.cmd spring-boot:run
```

## Ejecutar el JAR

```powershell
java -jar .\target\veranum_corepago-0.0.1-SNAPSHOT.jar
```

El servicio debe iniciar en:

```text
http://localhost:8084
```

## Clonar proyecto

```powershell
git clone https://github.com/ViktorD30/veranum_corepago.git
cd veranum_corepago
```

Crear previamente la base:

```sql
CREATE DATABASE veranum_pago;
```

Luego:

```powershell
.\mvnw.cmd clean package
```

y ejecutar:

```powershell
java -jar .\target\veranum_corepago-0.0.1-SNAPSHOT.jar
```

## Estrategia Git

El proyecto utiliza las ramas:

```text
main
develop
feature/corepago
```

Flujo de desarrollo:

```text
feature/corepago
        ↓
develop
        ↓
main
```

## Microservicios de Veranum Core

La solución está compuesta por:

```text
veranum_coreusuario      → puerto 8081
veranum_corereserva      → puerto 8082
veranum_corehabitacion   → puerto 8083
veranum_corepago         → puerto 8084
```

Cada microservicio mantiene su propia responsabilidad, puerto y base de datos.

## Estado del microservicio

```text
CRUD de Pago                 COMPLETADO
MySQL                        COMPLETADO
JPA / Hibernate              COMPLETADO
Validaciones                 COMPLETADO
Manejo de excepciones        COMPLETADO
Integración Pago → Reserva   COMPLETADO
Postman                      COMPLETADO
Maven                        COMPLETADO
JAR ejecutable               COMPLETADO
Swagger / OpenAPI            COMPLETADO
```

Proyecto desarrollado para el sistema **Veranum Core**.