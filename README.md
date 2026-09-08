# GesTutoUEES · Sistema de gestión de tutorías

Sistema integral de gestión, reserva y auditoría de tutorías académicas universitarias desarrollado en Java 17 y Maven para la asignatura de **Diseño de Software (UCOM0310)** de la **Universidad Espíritu Santo (UEES)**.

**Autor:** Miguel Iván Delgado Anazco  
**Docente:** Ph.D. Jaime Paul Sayago Heredia  
**Repositorio GitHub:** [https://github.com/cidmikeEC/GesTutoUEES](https://github.com/cidmikeEC/GesTutoUEES)  
**Entrega:** Actividad 5 | Ae3 – Incremento 1 del proyecto integrador  

---

## 🎯 1. Propósito del Proyecto y Alcance del Incremento 1

Integrar diseño orientado a objetos y patrones de diseño (estructurales, de comportamiento y creacionales) en un incremento funcional, documentado y versionado de **GesTutoUEES**, de modo que las responsabilidades, dependencias y puntos de variación sean explícitos y desacoplados bajo principios SOLID.

### Evolución respecto a incrementos anteriores (Ae1 y Ae2)
- **Ae1 (Diseño Orientado a Objetos Base):** Modelado del dominio (`Usuario`, `Estudiante`, `Docente`, `Horario`, `Reserva`), asignación de responsabilidades bajo alta cohesión y bajo acoplamiento, aplicación de SOLID y arquitectura limpia en Maven.
- **Ae2 (Patrones Creacionales):**
  - **Builder:** `ReservaBuilder` con Fluent API, validaciones cruzadas e inmutabilidad (erradicando el constructor telescópico).
  - **Factory Method:** Jerarquía creadora (`CreadorNotificador`) y de productos (`NotificadorTeams`, `NotificadorCorreo`, etc.) bajo OCP.
- **Ae3 (Incremento 1 · Semana 4):**
  - **Adapter (Estructural):** Integración con la API externa propietaria de videoconferencias (**Zoom Video Communications**), adaptando su SDK incompatible al contrato del dominio `ProveedorVideoconferencia`.
  - **Observer (Comportamiento):** Desacoplamiento del ciclo de vida de la reserva (`CREADA`, `CONFIRMADA`, `CANCELADA`) mediante un gestor de eventos reactivo que despacha simultáneamente a **Notificaciones multicanal** (reutilizando Factory Method de Ae2), **Auditoría institucional inmutable** y **Sincronización de calendario 365**.
  - **Facade (Estructural):** Fachada `AgendamientoTutoriaFacade` que unifica para clientes externos y controladores las operaciones complejas de agendamiento virtual y presencial en una sola interfaz limpia y cohesiva.

---

## 🧭 2. Revisión y Justificación de Patrones Heredados (Ae2)

| Patrón | Problema que resuelve en mi proyecto | ¿Se mantiene? | Justificación técnica |
| :--- | :--- | :---: | :--- |
| **Builder** | Antipatrón del constructor telescópico en la entidad `Reserva` (más de 10 atributos obligatorios y opcionales entre virtual y presencial). | **SÍ** | Permite construir instancias consistentes con valores por defecto inteligentes, validaciones estrictas en `.build()` e inmutabilidad de la entidad. |
| **Factory Method** | Acoplamiento rígido a clases concretas al despachar avisos por diversos canales institucionales. | **SÍ** | Se preserva y se integra como suscriptor dentro del patrón Observer (`NotificacionReservaObserver`), despachando notificaciones a Teams, WhatsApp o Correo sin acoplar el servicio central. |

---

## 🧩 3. Patrones de Semana 4 Integrados (Incremento 1)

### Plantilla de Justificación de Patrones (Sección 7 del Requerimiento Oficial)

| Elemento | Patrón 1: Observer (Comportamiento) | Patrón 2: Adapter (Estructural) | Patrón 3: Facade (Estructural) |
| :--- | :--- | :--- | :--- |
| **Problema real** | Acoplamiento 1-a-1 en `ServicioReservas`. Cuando una reserva cambia de estado, múltiples receptores independientes (notificaciones, auditoría, calendario) necesitan actuar sin que el servicio los conozca. | Incompatibilidad de interfaz al conectar con la API en la nube de Zoom (`ZoomSdkClient`), que maneja parámetros en inglés y tipos numéricos ajenos a nuestro dominio. | El cliente (consola/controlador) debe conocer y orquestar múltiples pasos y subsistemas (disponibilidad, Zoom adapter, builder, repositorio y eventos). |
| **Contexto** | Ciclo de vida de la tutoría universitaria (creación, confirmación, cancelación). | Agendamiento de tutorías virtuales universitarias mediante salas generadas dinámicamente. | Capa de aplicación que expone los casos de uso hacia clientes externos (CLI, API REST). |
| **Qué cambia** | Los suscriptores que reaccionan a los eventos (se pueden agregar más canales o analítica). | La implementación concreta del proveedor de videollamadas (Zoom, Google Meet, Teams). | La implementación interna y orden de orquestación de los subsistemas. |
| **Qué permanece estable** | El modelo de reserva y la interfaz `ReservaObserver` con su contrato `onEvento()`. | El contrato abstracto del dominio `ProveedorVideoconferencia` y el Value Object `ReunionVirtual`. | Las operaciones unificadas de alto nivel de la fachada hacia el cliente. |
| **Patrón seleccionado** | **Observer** | **Adapter** | **Facade** |
| **Clases / Interfaces** | `GestorEventosReserva`, `ReservaObserver`, `EventoReserva`, `NotificacionReservaObserver`, `AuditoriaReservaObserver`, `SincronizacionCalendarioObserver`. | `ProveedorVideoconferencia`, `ReunionVirtual`, `ZoomServiceAdapter`, `ZoomSdkClient`, `ZoomMeetingPayload`. | `AgendamientoTutoriaFacade`. |
| **Principio SOLID** | **OCP** (Abierto a nuevos observadores sin modificar el servicio) y **SRP** (responsabilidades separadas). | **DIP** (El dominio depende de su propia interfaz abstracta) e **ISP** (contrato específico y mínimo). | **Principio de Menor Conocimiento (Ley de Deméter)** y bajo acoplamiento cliente-subsistema. |
| **Beneficio esperado** | Desacoplamiento total; bitácora de auditoría inmutable en tiempo real; cero impacto al agregar nuevos oyentes. | Aislamiento completo del SDK de Zoom; posibilidad de intercambiar proveedor sin tocar el dominio. | Interfaz extraordinariamente simple para el cliente; código limpio y mantenible. |
| **Costo / compromiso** | Indirección en la ejecución y gestión de orden de observadores. | Sobrecarga de traducción de objetos y creación de adaptadores por cada proveedor externo. | La fachada puede convertirse en un cuello de botella si se sobrecarga de lógica de negocio que no le corresponde. |
| **Cómo se verificó** | Suite de pruebas `ObserverTest.java` (4 pruebas unitarias automatizadas). | Suite de pruebas `AdapterTest.java` (3 pruebas unitarias automatizadas). | Suite de pruebas `FacadeTest.java` (3 pruebas unitarias automatizadas). |

---

## 🏛️ 4. Principios SOLID, Cohesión y Acoplamiento

1. **Single Responsibility Principle (SRP):**
   - `ServicioReservas` solo orquesta reglas de negocio de reserva de horarios; delega la notificación a `GestorEventosReserva`.
   - `AuditoriaReservaObserver` únicamente registra trazas institucionales inmutables para acreditación.
   - `ZoomServiceAdapter` se encarga exclusivamente de traducir entre contratos.
2. **Open/Closed Principle (OCP):**
   - Nuevos receptores de eventos (por ejemplo, métricas estadísticas o alertas a directores de carrera) se conectan implementando `ReservaObserver` sin tocar una sola línea de código existente.
3. **Liskov Substitution Principle (LSP):**
   - Cualquier implementación de `ProveedorVideoconferencia` puede sustituir a `ZoomServiceAdapter` transparentemente.
4. **Interface Segregation Principle (ISP):**
   - `ProveedorVideoconferencia` solo expone métodos específicos de salas virtuales, sin mezclar lógica de mensajería o usuarios.
5. **Dependency Inversion Principle (DIP):**
   - Los módulos de alto nivel (`AgendamientoTutoriaFacade`, `ServicioReservas`) dependen de interfaces abstractas (`ProveedorVideoconferencia`, `ReservaObserver`), nunca de librerías concretas de terceros como `ZoomSdkClient`.

---

## 📊 5. Diagrama UML de Clases (Incremento 1)

El siguiente diagrama refleja la arquitectura completa con sus paquetes, clases, realizaciones, dependencias y multiplicidades:

![Diagrama UML Incremento 1](docs/uml-incremento1.png)

*Archivo fuente en PlantUML:* [`docs/uml-incremento1.puml`](docs/uml-incremento1.puml)

---

## 🏗️ 6. Estructura del Repositorio y Paquetes

```text
sistema-tutorias/
├── pom.xml                                   # Configuración de compilación Java 17 y JUnit 5
├── README.md                                 # Documentación técnica completa
├── docs/
│   ├── uml-incremento1.puml                  # Diagrama PlantUML formal del Incremento 1
│   ├── uml-incremento1.png                   # Renderizado gráfico de alta resolución
│   ├── factory-method.png, builder.png       # Evidencias gráficas de Ae2
│   └── modelo-clases.png                     # Diagrama base de Ae1
└── src/
    ├── main/java/edu/uees/tutorias/
    │   ├── Main.java                         # Demostración interactiva guiada por consola
    │   ├── adapter/                          # [NUEVO - Semana 4] Patrón Adapter
    │   │   ├── ProveedorVideoconferencia.java (Target)
    │   │   ├── ReunionVirtual.java           (Value Object del dominio)
    │   │   ├── ZoomServiceAdapter.java       (Adapter)
    │   │   └── external/
    │   │       ├── ZoomSdkClient.java        (Adaptee externo simulado)
    │   │       └── ZoomMeetingPayload.java   (DTO de respuesta propietaria)
    │   ├── domain/                           # Clases de entidad del dominio
    │   │   ├── Usuario.java, Estudiante.java, Docente.java
    │   │   ├── Horario.java, EstadoReserva.java, ModalidadTutoria.java
    │   │   ├── Reserva.java
    │   │   └── ReservaBuilder.java           (Builder con Fluent API)
    │   ├── events/                           # [NUEVO - Semana 4] Patrón Observer
    │   │   ├── TipoEventoReserva.java        (Enum de eventos del ciclo de vida)
    │   │   ├── EventoReserva.java            (Objeto inmutable de evento)
    │   │   ├── ReservaObserver.java          (Observer)
    │   │   ├── GestorEventosReserva.java     (Subject / Event Manager)
    │   │   ├── NotificacionReservaObserver.java (Concrete Observer - integra Factory Method)
    │   │   ├── AuditoriaReservaObserver.java    (Concrete Observer - bitácora inmutable)
    │   │   └── SincronizacionCalendarioObserver.java (Concrete Observer - buzón 365)
    │   ├── facade/                           # [NUEVO - Semana 4] Patrón Facade
    │   │   └── AgendamientoTutoriaFacade.java (Fachada unificada de alto nivel)
    │   ├── notification/                     # Patrón Factory Method (Ae2)
    │   │   ├── Notificador.java              (Product)
    │   │   ├── NotificadorCorreo.java, NotificadorSMS.java, NotificadorWhatsApp.java, NotificadorTeams.java
    │   │   ├── CreadorNotificador.java       (Creator)
    │   │   └── CreadorNotificadorTeams.java, etc. (Concrete Creators)
    │   ├── persistence/                      # Repositorios de persistencia
    │   │   ├── RepositorioReservas.java
    │   │   └── RepositorioReservasEnMemoria.java
    │   └── service/                          # Lógica orquestadora de negocio
    │       └── ServicioReservas.java         (Integrado con GestorEventos)
    └── test/java/edu/uees/tutorias/
        ├── AdapterTest.java                  # [NUEVO] 3 tests unitarios del Adapter
        ├── ObserverTest.java                 # [NUEVO] 4 tests unitarios del Observer
        ├── FacadeTest.java                   # [NUEVO] 3 tests unitarios del Facade
        ├── FactoryMethodTest.java            # 5 tests unitarios de Ae2
        ├── ReservaBuilderTest.java           # 7 tests unitarios de Ae2
        └── ServicioReservasTest.java         # 3 tests unitarios de integración
```

---

## 🚀 7. Compilación, Pruebas y Ejecución

Requisitos: **JDK 17** y **Maven 3.8+**.

```powershell
# 1. Compilar y ejecutar la suite completa de pruebas automatizadas (25 tests en verde)
mvn clean test

# 2. Ejecutar la demostración interactiva guiada por consola
java -cp target/classes edu.uees.tutorias.Main
```

### Resultado de la suite de pruebas automatizadas:
```text
[INFO] Running edu.uees.tutorias.AdapterTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.uees.tutorias.FacadeTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.uees.tutorias.FactoryMethodTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.uees.tutorias.ObserverTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.uees.tutorias.ReservaBuilderTest
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running edu.uees.tutorias.ServicioReservasTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 25, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 📝 8. Declaración de uso ético de IA

Durante el desarrollo de este incremento utilicé herramientas de inteligencia artificial como apoyo en la conceptualización arquitectónica de patrones estructurales y de comportamiento, estructuración sintáctica de diagramas PlantUML y redacción comparativa. Revisé, diseñé, probé, refactoricé y comprendí la totalidad del código Java, sus pruebas unitarias y las decisiones de diseño presentadas, asumiendo plena responsabilidad técnica sobre el proyecto.