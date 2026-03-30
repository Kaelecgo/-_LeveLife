# Memoria Base - >_LeveLife

## 1. Introducción

LeveLife es una aplicación móvil Android desarrollada como proyecto de TFG con el objetivo de ayudar al usuario a mejorar su organización personal, reducir la procrastinación y mantener hábitos de forma más motivadora mediante mecánicas de gamificación.

La idea principal del proyecto surge de un problema muy frecuente en estudiantes y jóvenes usuarios: la dificultad para mantener constancia en tareas cotidianas, académicas o personales cuando las aplicaciones tradicionales de productividad resultan demasiado rígidas, frías o poco estimulantes.

Frente a ese escenario, LeveLife propone una experiencia centrada en la motivación progresiva del usuario. Para ello, combina la gestión de tareas y hábitos con un sistema de recompensas basado en experiencia, niveles, bayas virtuales, EcoCoins y compra de mobiliario para un inventario visual. De esta forma, cada acción productiva dentro de la aplicación tiene una consecuencia visible e inmediata.

---

## 2. Problema que se pretende resolver

Uno de los problemas más frecuentes en la gestión personal del tiempo es que muchas personas conocen lo que deben hacer, pero no consiguen sostener la motivación suficiente para hacerlo con regularidad. Esta situación se ve reforzada por varios factores:

- dificultad para transformar objetivos grandes en acciones pequeñas
- baja constancia en hábitos diarios
- abandono de aplicaciones de productividad por falta de estímulo
- sensación de rutina o castigo asociada a la organización
- escasa conexión entre el esfuerzo diario y una recompensa visible

LeveLife pretende abordar este problema desde una perspectiva más amable y motivadora, en la que completar tareas no sea únicamente una obligación, sino también una acción con recompensa, progreso, identidad visual y personalización.

---

## 3. Objetivo general

Desarrollar una aplicación móvil Android en Java que permita gestionar tareas y hábitos personales y fomentar la constancia del usuario mediante mecánicas de gamificación, integrando un sistema de progreso, economía virtual, tienda e inventario.

Además, el proyecto busca reforzar la seguridad y la persistencia de datos, sustituyendo decisiones propias de un prototipo inicial por una base más robusta, mantenible y defendible técnicamente.

---

## 4. Objetivos específicos

- Implementar un sistema de registro e inicio de sesión local seguro mediante hashing PBKDF2.
- Permitir la creación, visualización y seguimiento de tareas y hábitos.
- Enriquecer el modelo de tarea con dificultad, frecuencia y atributos ecológicos.
- Asociar recompensas a las tareas en forma de experiencia, bayas y EcoCoins.
- Diseñar un sistema de niveles basado en acumulación de experiencia.
- Implementar una tienda virtual con muebles comprables.
- Implementar un inventario asociado al usuario.
- Mantener la persistencia local de sesión, saldo, progreso e inventario mediante migraciones no destructivas.
- Aplicar una arquitectura mantenible basada en Room, Repository, ViewModel y LiveData.
- Centralizar la lógica de recompensas y desacoplarla de la interfaz.
- Mejorar la experiencia de creación de tareas mediante un formulario enriquecido con vista previa de recompensa.
- Documentar el desarrollo del proyecto de forma paralela a la implementación.

---

## 5. Alcance del proyecto

La versión actual del proyecto incluye un conjunto de funcionalidades centradas en el flujo principal de uso:

- autenticación local de usuario con seguridad reforzada
- gestión de tareas e inicio de evolución hacia hábitos recurrentes
- sistema de experiencia, niveles, bayas y EcoCoins
- formulario avanzado de creación de tareas mediante BottomSheetDialog
- cálculo dinámico de recompensas según dificultad e impacto ecológico
- control básico de recurrencia por periodo actual
- catálogo de muebles en tienda
- compra de muebles con validación de saldo y atomicidad
- inventario del usuario
- persistencia de sesión y datos locales garantizada mediante migraciones explícitas de esquema

Quedan como ampliaciones futuras aspectos como una habitación totalmente interactiva, colocación persistente rica de muebles, historial completo de hábitos recurrentes mediante entidad separada, sincronización en la nube, estadísticas avanzadas o una ampliación más ambiciosa del sistema ecológico.

---

## 6. Justificación técnica

Para el desarrollo del proyecto se ha optado por una arquitectura basada en componentes ampliamente utilizados en Android nativo:

- **Room** para persistencia local estructurada con soporte para migraciones.
- **Repository** como capa de acceso y coordinación de datos, actuando como fuente única de verdad.
- **ViewModel** para desacoplar la lógica de presentación y gestionar el estado observable.
- **LiveData** para actualizar la interfaz de manera reactiva.

Esta elección permite mantener una separación clara de responsabilidades, mejorar la mantenibilidad del código y garantizar la integridad de los datos ante evoluciones del sistema.

Además, durante el desarrollo se ha reforzado la robustez del proyecto mediante tres líneas claras:

- endurecimiento de seguridad en credenciales
- evolución controlada del esquema de base de datos
- desacoplamiento entre lógica interna y representación visual

---

## 7. Estado actual del desarrollo

En el estado actual del proyecto ya se han implementado y validado los siguientes bloques:

- arquitectura base con Room, Repository, ViewModel y LiveData
- login local seguro y recuperación de sesión consistente
- registro de usuario reforzado
- sistema de recompensas atómico
- tienda virtual e inventario persistente
- cadena de migraciones explícitas hasta la versión 7
- validaciones de seguridad e integridad mediante hash de contraseñas y nombres de usuario únicos
- enriquecimiento de la entidad Task con dificultad, frecuencia, recompensa ecológica y control temporal básico
- economía dual con bayas y EcoCoins
- creación de tareas mediante BottomSheetDialog con vista previa de recompensas
- lógica de recompensas centralizada en `TaskRewardCalculator`
- recurrencia básica gestionada mediante `TaskRecurrenceUtils`
- normalización de etiquetas para desacoplar lógica de negocio y textos visibles de UI
- pruebas unitarias del núcleo de recompensas y recurrencia

Actualmente, el foco principal se centra en formalizar casos de prueba, preparar evidencias para memoria y defensa, cerrar documentalmente FASE 3 y decidir el alcance final de la acción "Colocar" dentro del inventario.

---

## 8. Metodología de trabajo

El proyecto se está desarrollando por fases y sesiones de trabajo, manteniendo una documentación paralela a la implementación. Para ello se han creado documentos internos de control como:

- panel general del proyecto
- bitácora de sesiones
- backlog técnico
- log de implementación técnica

Este enfoque permite llevar trazabilidad del avance real, registrar decisiones técnicas y convertir el trabajo diario en material útil para la memoria final y la defensa del TFG.

---

## 9. Próximos apartados a desarrollar en esta memoria

Esta memoria base servirá como documento madre para consolidar en capítulos posteriores:

- requisitos funcionales y no funcionales
- diseño de arquitectura detallado
- modelo de datos y esquema de migraciones
- implementación técnica y seguridad
- sistema de recompensas y lógica de hábitos
- pruebas unitarias y validación funcional
- conclusiones y valoración técnica

---

## 10. Requisitos del sistema

### 10.1 Requisitos funcionales

#### RF-01. Registro de usuario local seguro
La aplicación debe permitir crear usuarios nuevos generando un hash seguro de su contraseña mediante PBKDF2 e inicializando su estado base de forma consistente.

#### RF-02. Inicio de sesión local
La aplicación debe validar las credenciales del usuario comparando la contraseña introducida con el hash almacenado en la base de datos local.

#### RF-03. Persistencia de sesión
La aplicación debe recordar el usuario activo entre reinicios mediante SharedPreferences, validando su coherencia en el arranque.

#### RF-04. Visualización reactiva de tareas
La aplicación debe mostrar al usuario su lista de tareas e historial visible de estado de forma reactiva a través de LiveData.

#### RF-05. Creación enriquecida de tareas
La aplicación debe permitir añadir nuevas tareas mediante un formulario que incluya título, descripción, categoría, dificultad y frecuencia, mostrando además una vista previa de recompensas antes de confirmar.

#### RF-06. Actualización del estado de tarea
La aplicación debe permitir marcar tareas como completadas de forma persistente o bloquear su repetición temporal según el periodo actual en tareas recurrentes.

#### RF-07. Recompensa atómica por completar tareas
Al completar una tarea, el sistema debe otorgar experiencia, bayas y, cuando proceda, EcoCoins garantizando que la operación sea única y consistente.

#### RF-08. Sistema de niveles
La aplicación debe calcular automáticamente la progresión y actualizar el nivel del usuario basándose en los umbrales de experiencia definidos.

#### RF-09. Visualización del progreso del usuario
La aplicación debe mostrar el nivel, experiencia, saldo de bayas y saldo de EcoCoins de forma actualizada en la interfaz principal.

#### RF-10. Catálogo de tienda
La aplicación debe mostrar un catálogo de muebles disponibles para compra, incluyendo recuperación automática del catálogo base si la tabla correspondiente está vacía.

#### RF-11. Compra de muebles atómica
La aplicación debe permitir adquirir muebles validando saldo y propiedad previa en una sola transacción para evitar inconsistencias de datos.

#### RF-12. Inventario del usuario
La aplicación debe mostrar los muebles adquiridos recuperando la relación desde la tabla cruzada en la base de datos.

#### RF-13. Persistencia e integridad ante actualizaciones
La aplicación debe conservar los datos de usuario, tareas e inventario incluso tras actualizaciones del esquema de la base de datos mediante migraciones explícitas.

#### RF-14. Feedback visual de acciones y recompensas
La aplicación debe informar al usuario del éxito de sus acciones y de las cantidades exactas de recompensas obtenidas.

#### RF-15. Recompensas dinámicas
La aplicación debe calcular las recompensas de las tareas en función de su dificultad y de su impacto ecológico.

---

### 10.2 Requisitos no funcionales

#### RNF-01. Funcionamiento offline
La aplicación debe ser funcional sin conexión a internet.

#### RNF-02. Seguridad de credenciales
Las contraseñas no deben almacenarse nunca en texto plano. Se debe utilizar un algoritmo de hash robusto con salt aleatoria.

#### RNF-03. Separación de responsabilidades
La arquitectura debe separar claramente persistencia, lógica de negocio y presentación.

#### RNF-04. Reactividad y fluidez
La interfaz debe ser reactiva a los cambios en la base de datos y las operaciones pesadas deben realizarse fuera del hilo principal.

#### RNF-05. Integridad referencial
La base de datos debe utilizar claves foráneas y restricciones de unicidad para evitar datos huérfanos o duplicados.

#### RNF-06. Evolución controlada del esquema
No se debe depender de migraciones destructivas como estrategia principal. Cualquier cambio en el modelo debe ir acompañado de su correspondiente migración.

#### RNF-07. Usabilidad y feedback
El sistema debe proporcionar una experiencia fluida y respuestas claras a las interacciones del usuario.

#### RNF-08. Robustez frente a presentación e idioma
La lógica de negocio debe mantenerse estable aunque cambien los textos visibles, los idiomas o los recursos de interfaz.

#### RNF-09. Compatibilidad y soporte de API
La aplicación se ha desarrollado con un SDK mínimo 24 (Android 7.0), permitiendo compatibilidad amplia y uso de características modernas de Java 8 y Room.

---

## 11. Arquitectura del sistema

La arquitectura de LeveLife se apoya en el principio de **Single Source of Truth**. La base de datos local actúa como núcleo fiable del estado del sistema, mientras que el resto de capas reaccionan a sus cambios.

### 11.1 Capas principales

- **Model**: Contiene entidades como `User`, `Task`, `Furniture` y la relación de inventario. Incluye lógica de dominio relacionada con progresión, economía y normalización básica de datos.
- **Persistence (Room)**: Abstracción sobre SQLite que gestiona el acceso físico a los datos y las migraciones de esquema.
- **Repository**: Capa de coordinación que encapsula operaciones complejas y transaccionales como registro, compra, login y completado de tareas.
- **ViewModel**: Expone estados observables a la vista, incluyendo mensajes de error y recompensa.
- **View (Activities/Adapters)**: Observa LiveData y captura la interacción del usuario.

### 11.2 Seguridad y persistencia crítica

Un pilar fundamental de la arquitectura es la gestión de credenciales mediante `PasswordUtils`. Este componente se encarga de generar y verificar hashes, además de gestionar la transición de cuentas antiguas mediante un sistema de actualización transparente durante el login.

En cuanto a la persistencia, el sistema implementa migraciones explícitas hasta la versión 7 del esquema. Esto permite evolucionar el modelo sin comprometer el progreso guardado por el usuario, incluyendo campos nuevos relacionados con seguridad, recompensas ecológicas y hábitos enriquecidos.

### 11.3 Lógica desacoplada y robustez del dominio

La arquitectura también ha evolucionado para reducir la dependencia entre interfaz y lógica interna. Para ello:

- el cálculo de recompensas se concentra en `TaskRewardCalculator`
- la recurrencia básica se gestiona en `TaskRecurrenceUtils`
- la entidad `Task` incorpora normalización de etiquetas para evitar errores provocados por emojis, traducciones o textos visibles

Este enfoque mejora la mantenibilidad y hace que el sistema sea más resistente ante cambios futuros de UI o internacionalización.

---

## 12. Modelo de datos

El esquema relacional se basa en cuatro tablas principales y en la evolución progresiva de sus atributos.

### 12.1 Tabla `users`
Almacena la información principal del usuario:

- identificador
- nombre de usuario único
- hash de contraseña
- nivel
- experiencia
- bayas
- EcoCoins

### 12.2 Tabla `tasks`
Representa tareas y hábitos asociados a un usuario. Además del núcleo clásico de título, descripción y recompensa, la entidad ha evolucionado para incluir:

- dificultad
- frecuencia
- indicador de tarea ecológica
- recompensa ecológica
- marca temporal de último completado

Esto permite soportar una lógica de hábitos más rica que la de una lista de tareas convencional.

### 12.3 Tabla `furniture`
Actúa como catálogo global de muebles comprables dentro de la tienda.

### 12.4 Tabla `user_furniture_cross_ref`
Relaciona usuarios y muebles adquiridos en una estructura N:M mediante clave compuesta, evitando compras duplicadas del mismo mueble por un mismo usuario.

---

## 13. Implementación del sistema

### 13.1 Seguridad e integridad en el registro y login
El registro se ha implementado como una operación consistente en el repositorio. El sistema verifica unicidad del nombre de usuario, genera el hash de la contraseña e inicializa el estado del usuario. El login ya no delega la validación al SQL puro, sino que recupera el usuario y verifica las credenciales mediante lógica específica de seguridad.

### 13.2 Transacciones atómicas en compras y recompensas
Para evitar fallos de integridad, todas las operaciones de gasto o recompensa se ejecutan dentro de bloques transaccionales. Esto garantiza coherencia entre saldo, progreso, inventario y estado de la tarea.

### 13.3 Sistema enriquecido de tareas y hábitos
La aplicación ha evolucionado desde una creación mínima de tareas hacia un flujo más completo. La creación se realiza ahora mediante un `BottomSheetDialog` que permite introducir metadatos adicionales y visualizar una vista previa de recompensa antes de confirmar.

### 13.4 Lógica centralizada de recompensas
El cálculo de experiencia, bayas y EcoCoins se ha desacoplado de la interfaz mediante `TaskRewardCalculator`, permitiendo adaptar la recompensa al esfuerzo y al impacto ecológico de cada tarea.

### 13.5 Recurrencia básica y control temporal
La lógica de recurrencia básica se apoya en `TaskRecurrenceUtils`, que permite bloquear el completado repetido de determinadas tareas dentro del periodo actual. Este bloque se considera actualmente un MVP funcional, ya que todavía no existe una entidad separada de historial completo de completados.

### 13.6 Normalización y robustez interna
Durante la evolución del sistema se detectó un problema derivado de comparar lógica interna con textos visibles de la interfaz. Para resolverlo, se reforzó la normalización de categorías, dificultad y frecuencia dentro del modelo de tareas, desacoplando así la lógica de negocio de emojis, traducciones y variaciones visuales.

### 13.7 Evolución de la base de datos
Se ha sustituido la estrategia destructiva original por un sistema de evolución controlada mediante migraciones explícitas. Este enfoque culmina en la migración a la versión 7, alineada con el nuevo sistema enriquecido de tareas y recompensas ecológicas.

---

## 14. Pruebas y validación

### 14.1 Pruebas unitarias de seguridad
Se han implementado tests específicos para `PasswordUtils` que validan:

- generación correcta de hashes
- rechazo de contraseñas incorrectas
- compatibilidad con el sistema actual de autenticación

### 14.2 Pruebas unitarias de recompensas y recurrencia
Se han ejecutado pruebas unitarias para validar:

- cálculo de recompensas según dificultad
- asignación de EcoCoins cuando corresponde
- bloqueo por periodo en tareas recurrentes
- comportamiento de normalización en etiquetas

### 14.3 Validación funcional
Además de las pruebas unitarias, se han realizado validaciones manuales de los flujos principales:

- registro y login
- recuperación de sesión
- creación de tareas
- reward preview
- subida de nivel
- compra de muebles
- persistencia de inventario y saldo
- arranque correcto tras alinear migraciones
- repoblado del catálogo cuando la tabla de muebles está vacía

---

## 15. Conclusiones y trabajo futuro

### 15.1 Conclusiones
LeveLife ha evolucionado de un prototipo funcional a una aplicación con una base técnica más robusta. Se ha reforzado la seguridad de credenciales, la integridad de la persistencia y la riqueza del modelo de hábitos sin romper el flujo principal de uso. Además, se ha mejorado la mantenibilidad desacoplando lógica de recompensas, representación visual y tratamiento de datos.

### 15.2 Trabajo futuro
- Definir el alcance final de la acción "Colocar" dentro del inventario.
- Evolucionar el inventario hacia una habitación más interactiva.
- Valorar una entidad separada para historial completo de hábitos recurrentes.
- Separar repositorio y ViewModel por funcionalidades cuando el núcleo quede completamente estabilizado.
- Ampliar cobertura mediante pruebas instrumentadas de interfaz.
- Estudiar opciones de copia de seguridad externa o sincronización opcional.

### 15.3 Valoración final
LeveLife constituye una propuesta sólida que combina gamificación, organización personal y una arquitectura técnica orientada a la seguridad, la persistencia y la evolución controlada del sistema.