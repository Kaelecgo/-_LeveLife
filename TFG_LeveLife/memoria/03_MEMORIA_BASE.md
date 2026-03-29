# Memoria Base - >_LeveLife

## 1. Introducción

LeveLife es una aplicación móvil Android desarrollada como proyecto de TFG con el objetivo de ayudar al usuario a mejorar su organización personal, reducir la procrastinación y mantener hábitos de forma más motivadora mediante elementos de gamificación.

La idea principal del proyecto surge de un problema muy común en estudiantes y jóvenes usuarios: la dificultad para mantener constancia en tareas cotidianas, académicas o personales cuando las aplicaciones tradicionales de productividad resultan demasiado rígidas, frías o poco estimulantes.

Frente a ese escenario, LeveLife propone una experiencia centrada en la motivación progresiva del usuario. Para ello, combina la gestión de tareas con un sistema de recompensas basado en experiencia, niveles, bayas virtuales y compra de mobiliario para una habitación virtual. De esta forma, cada acción productiva dentro de la aplicación tiene una recompensa visible e inmediata.

## 2. Problema que se pretende resolver

Uno de los problemas más frecuentes en la gestión personal del tiempo es que muchas personas conocen lo que deben hacer, pero no consiguen sostener la motivación suficiente para hacerlo con regularidad. Esta situación se ve reforzada por varios factores:

- dificultad para transformar objetivos grandes en tareas pequeñas
- baja constancia en hábitos diarios
- abandono de aplicaciones de productividad por falta de estímulo
- sensación de rutina o castigo asociada a la organización

LeveLife pretende abordar este problema desde una perspectiva más amable y motivadora, en la que completar tareas no sea únicamente una obligación, sino también una acción con recompensa, progreso y personalización visual.

## 3. Objetivo general

Desarrollar una aplicación móvil Android en Java que permita gestionar tareas personales y fomentar la constancia del usuario mediante mecánicas de gamificación, integrando un sistema de progreso, economía virtual, tienda e inventario.

El proyecto busca reforzar la seguridad y la persistencia de datos, manteniendo el flujo funcional existente de login, registro y progreso del usuario, pero sustituyendo soluciones propias de prototipo por una base más robusta y defendible.

## 4. Objetivos específicos

- Implementar un sistema de registro e inicio de sesión local seguro (Hashing PBKDF2).
- Permitir la creación, visualización y seguimiento de tareas.
- Asociar recompensas a las tareas en forma de experiencia y bayas.
- Diseñar un sistema de niveles basado en acumulación de experiencia.
- Implementar una tienda virtual con muebles comprables.
- Implementar un inventario asociado al usuario.
- Mantener la persistencia local de sesión, saldo, progreso e inventario mediante migraciones no destructivas.
- Aplicar una arquitectura mantenible basada en Room, Repository, ViewModel y LiveData.
- Documentar el desarrollo del proyecto de forma paralela a la implementación.

## 5. Alcance del proyecto

La versión actual del proyecto incluye un conjunto de funcionalidades centradas en el flujo principal de uso:

- autenticación local de usuario con seguridad reforzada
- gestión básica de tareas
- sistema de experiencia, niveles y bayas
- catálogo de muebles en tienda
- compra de muebles con validación de saldo y atomicidad
- inventario del usuario
- persistencia de sesión y datos locales garantizada mediante migraciones de esquema

Quedan como posibles ampliaciones futuras aspectos como una habitación totalmente interactiva, colocación persistente de muebles, sincronización en la nube, estadísticas avanzadas o ampliación del sistema de hábitos.

## 6. Justificación técnica

Para el desarrollo del proyecto se ha optado por una arquitectura basada en componentes ampliamente utilizados en Android nativo:

- **Room** para persistencia local estructurada con soporte para migraciones.
- **Repository** como capa de acceso y coordinación de datos (fuente única de verdad).
- **ViewModel** para desacoplar la lógica de UI y gestionar el estado.
- **LiveData** para actualizar la interfaz de manera reactiva.

Esta elección permite mantener una separación clara de responsabilidades, mejorar la mantenibilidad del código y garantizar la integridad de los datos ante evoluciones del sistema.

## 7. Estado actual del desarrollo

En el estado actual del proyecto ya se han implementado y validado los siguientes bloques:

- arquitectura base con Room, Repository, ViewModel y LiveData
- login local seguro y recuperación de sesión consistente
- registro de usuario transaccional con generación de tareas iniciales
- sistema de recompensas atómico
- tienda virtual e inventario persistente
- cadena de migraciones (1 a 6) para evolución del esquema sin pérdida de datos
- validaciones de seguridad (hash de contraseñas) e integridad (nombres de usuario únicos)

Actualmente, el foco se centra en consolidar la modularización del código y ampliar la cobertura de pruebas de integración.

## 8. Metodología de trabajo

El proyecto se está desarrollando por fases y sesiones de trabajo, manteniendo una documentación paralela al desarrollo. Para ello se han creado documentos internos de control como:

- panel general del proyecto
- bitácora de sesiones
- backlog técnico

Este enfoque permite llevar trazabilidad del avance real, registrar decisiones técnicas y convertir el trabajo diario en material útil para la memoria final y la defensa del TFG.

## 9. Próximos apartados a desarrollar en esta memoria

Esta memoria base servirá como documento madre para consolidar en capítulos posteriores:

- requisitos funcionales y no funcionales
- diseño de arquitectura detallado
- modelo de datos y esquema de migraciones
- implementación técnica y seguridad
- pruebas unitarias y de integración
- conclusiones y valoración técnica

## 10. Requisitos del sistema

### 10.1 Requisitos funcionales

#### RF-01. Registro de usuario local seguro
La aplicación debe permitir crear usuarios nuevos generando un hash seguro de su contraseña (PBKDF2) e inicializando sus recursos y tareas básicas en una única operación transaccional.

#### RF-02. Inicio de sesión local
La aplicación debe validar las credenciales del usuario comparando el hash de la contraseña proporcionada con el almacenado en la base de datos local.

#### RF-03. Persistencia de sesión
La aplicación debe recordar el usuario activo entre reinicios mediante SharedPreferences, validando la existencia del usuario en cada arranque para garantizar la integridad.

#### RF-04. Visualización de tareas
La aplicación debe mostrar al usuario su lista de tareas registradas de forma reactiva a través de LiveData.

#### RF-05. Creación de tareas
La aplicación debe permitir añadir nuevas tareas con atributos de título, categoría y recompensas.

#### RF-06. Actualización del estado de tarea
La aplicación debe permitir marcar tareas como completadas de forma persistente.

#### RF-07. Recompensa atómica por completar tareas
Al completar una tarea, el sistema debe otorgar experiencia y bayas garantizando que la operación sea única y consistente (evitando dobles recompensas).

#### RF-08. Sistema de niveles
La aplicación debe calcular automáticamente la progresión y actualizar el nivel del usuario basándose en los umbrales de experiencia definidos.

#### RF-09. Visualización del progreso del usuario
La aplicación debe mostrar el nivel, experiencia y saldo de bayas de forma actualizada en la interfaz principal.

#### RF-10. Catálogo de tienda
La aplicación debe mostrar un catálogo de muebles disponibles para compra.

#### RF-11. Compra de muebles atómica
La aplicación debe permitir adquirir muebles validando saldo y propiedad previa en una sola transacción para evitar inconsistencias de datos.

#### RF-12. Inventario del usuario
La aplicación debe mostrar los muebles adquiridos recuperando la relación desde la tabla cruzada en la base de datos.

#### RF-13. Persistencia e integridad ante actualizaciones
La aplicación debe conservar todos los datos de usuario, tareas e inventario incluso tras actualizaciones del esquema de la base de datos mediante migraciones explícitas.

#### RF-14. Feedback visual de acciones y recompensas
La aplicación debe informar al usuario del éxito de sus acciones y de las cantidades exactas de recompensas obtenidas.

---

### 10.2 Requisitos no funcionales

#### RNF-01. Funcionamiento offline
La aplicación debe ser 100% funcional sin conexión a internet.

#### RNF-02. Seguridad de credenciales
Las contraseñas no deben almacenarse nunca en texto plano. Se debe utilizar un algoritmo de hash robusto (PBKDF2) con salt aleatoria.

#### RNF-03. Separación de responsabilidades (MVVM + Repository)
La arquitectura debe separar claramente la persistencia, la lógica de negocio y la presentación.

#### RNF-04. Reactividad y fluidez
La interfaz debe ser reactiva a los cambios en la base de datos y todas las operaciones pesadas deben realizarse fuera del hilo de UI.

#### RNF-05. Integridad referencial
La base de datos debe utilizar claves foráneas y restricciones de unicidad (ej. nombres de usuario únicos) para evitar datos huérfanos o duplicados.

#### RNF-06. Evolución controlada del esquema
Se prohíbe el uso de migraciones destructivas en producción. Cualquier cambio en el modelo debe ir acompañado de su correspondiente script de migración.

#### RNF-07. Usabilidad y Feedback
El sistema debe proporcionar una experiencia fluida y respuestas claras a las interacciones del usuario.

#### RNF-08. Compatibilidad y Soporte de API
La aplicación se ha desarrollado con un **SDK mínimo 24 (Android 7.0)**. Esta elección garantiza compatibilidad con más del 90% de los dispositivos activos, permitiendo al mismo tiempo el uso de características modernas de Java 8 y de la librería Room.

## 11. Arquitectura del sistema

La arquitectura de LeveLife se apoya en el principio de **Single Source of Truth** (fuente única de verdad). La base de datos local actúa como núcleo fiable del estado del sistema, mientras que el resto de capas reaccionan a sus cambios.

### 11.1 Capas principales

- **Model**: Contiene las entidades (User, Task, Furniture) y la lógica de dominio (progresión, gastos). Incluye el **Diagrama de Estados de la Tarea**, donde una tarea transita de *Pendiente* a *Completada* mediante una acción atómica que dispara el sistema de recompensas.
- **Persistence (Room)**: Abstracción sobre SQLite que gestiona el acceso físico a los datos y las migraciones de esquema.
- **Repository**: Capa de coordinación que encapsula las operaciones complejas y transaccionales (registro, compra, completado de tareas).
- **ViewModel**: Expone estados observables a la vista y gestiona la lógica de presentación.
- **View (Activities/Adapters)**: Observa los LiveData y captura la interacción del usuario.

### 11.2 Seguridad y Persistencia Crítica

Un pilar fundamental de la arquitectura es la gestión de la seguridad mediante `PasswordUtils`. Este componente se encarga de generar y verificar hashes, además de gestionar la transición de cuentas antiguas mediante un sistema de "upgrade" automático de credenciales durante el login.

En cuanto a la persistencia, el sistema implementa una cadena de **migraciones explícitas** (de la versión 1 a la 6). Esto permite evolucionar el modelo (por ejemplo, añadiendo restricciones de unicidad o normalizando datos de usuario) sin comprometer el progreso guardado por el usuario.

## 12. Modelo de datos

El esquema relacional se basa en cuatro tablas principales:
- `users`: Perfiles, experiencia, bayas y hash de contraseña. Incluye un índice único sobre `user_name`.
- `tasks`: Tareas vinculadas al usuario mediante clave foránea con borrado en cascada.
- `furniture`: Catálogo global de objetos.
- `user_furniture_cross_ref`: Tabla de unión N:M para el inventario, con clave compuesta para evitar duplicados.

## 13. Implementación del sistema

### 13.1 Seguridad e Integridad en el Registro y Login
El registro se ha implementado como una operación atómica en `MainRepository`. En un solo paso, el sistema verifica que el nombre no esté duplicado, genera el hash de la contraseña e inserta el usuario junto a un lote de tareas iniciales (`starter tasks`). El login, por su parte, ya no delega la validación al SQL; recupera el usuario y verifica el hash mediante lógica de dominio, permitiendo actualizar credenciales antiguas de forma transparente.

### 13.2 Transacciones Atómicas en Compras y Recompensas
Para evitar fallos de integridad (como perder bayas sin recibir el mueble), todas las operaciones de "gasto" o "recompensa" se ejecutan dentro de bloques `runInTransaction`. Esto garantiza que o bien toda la operación tiene éxito, o bien la base de datos vuelve a su estado anterior si ocurre algún error.

### 13.3 Gestión de Excepciones y Robustez
Se ha implementado un sistema de **manejo de errores centralizado**. El repositorio captura excepciones de base de datos o lógica de negocio y las comunica al ViewModel mediante interfaces de callback. El ViewModel, a su vez, expone estos errores a través de LiveData (`errorMessages`), permitiendo que la interfaz informe al usuario de forma amigable (vía Toasts o Diálogos) en lugar de producir cierres inesperados (ANR o crashes).

### 13.4 Evolución de la Base de Datos (Migraciones)
Se ha sustituido la estrategia destructiva original por un sistema de evolución controlada. Esto incluye scripts específicos para normalizar nombres de usuario duplicados antes de aplicar restricciones de unicidad, asegurando que la migración no falle en dispositivos con datos previos.

## 14. Pruebas y validación

### 14.1 Pruebas Unitarias de Seguridad
Se han implementado tests específicos para `PasswordUtils` que validan:
- Generación correcta de hashes.
- Rechazo de contraseñas incorrectas.
- Detección de credenciales que necesitan actualización.
- Compatibilidad con el formato antiguo (texto plano).

### 14.2 Validación de Migraciones y Consistencia
Se ha verificado mediante `Database Inspector` que las migraciones de esquema mantienen los datos de las tablas existentes. Las pruebas funcionales confirman que el flujo de registro -> auto-login -> obtención de recompensas funciona de forma fluida y consistente.

## 15. Conclusiones y trabajo futuro

### 15.1 Conclusiones
LeveLife ha evolucionado de un prototipo funcional a una aplicación con una base técnica robusta. Se ha logrado blindar la seguridad de las credenciales y la integridad de la persistencia sin romper la experiencia de usuario. El sistema de migraciones y la arquitectura desacoplada dejan el proyecto en un estado profesional y fácilmente defendible ante un tribunal de TFG.

### 15.3 Trabajo futuro
- **Modularización**: Separar el repositorio y ViewModel por funcionalidades (Auth, Shop, Tasks) para evitar clases excesivamente grandes.
- **Habitación Interactiva**: Evolucionar el inventario hacia una representación visual 2D o 3D donde el usuario pueda colocar objetos.
- **Sincronización**: Estudiar la posibilidad de copias de seguridad externas opcionales.
- **Ampliación de Cobertura de Tests**: Integrar pruebas instrumentadas de flujo completo (UI Tests).

### 15.4 Valoración final
LeveLife constituye una propuesta sólida que combina diseño, gamificación y una arquitectura técnica que prioriza la seguridad y la integridad de los datos del usuario.
