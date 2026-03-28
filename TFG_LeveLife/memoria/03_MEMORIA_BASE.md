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

## 4. Objetivos específicos

- Implementar un sistema de registro e inicio de sesión local.
- Permitir la creación, visualización y seguimiento de tareas.
- Asociar recompensas a las tareas en forma de experiencia y bayas.
- Diseñar un sistema de niveles basado en acumulación de experiencia.
- Implementar una tienda virtual con muebles comprables.
- Implementar un inventario asociado al usuario.
- Mantener la persistencia local de sesión, saldo, progreso e inventario.
- Aplicar una arquitectura mantenible basada en Room, Repository, ViewModel y LiveData.
- Documentar el desarrollo del proyecto de forma paralela a la implementación.

## 5. Alcance del proyecto

La versión actual del proyecto incluye un conjunto de funcionalidades centradas en el flujo principal de uso:

- autenticación local de usuario
- gestión básica de tareas
- sistema de experiencia, niveles y bayas
- catálogo de muebles en tienda
- compra de muebles con validación de saldo
- inventario del usuario
- persistencia de sesión y datos locales

Quedan como posibles ampliaciones futuras aspectos como una habitación totalmente interactiva, colocación persistente de muebles, sincronización en la nube, estadísticas avanzadas o ampliación del sistema de hábitos.

## 6. Justificación técnica

Para el desarrollo del proyecto se ha optado por una arquitectura basada en componentes ampliamente utilizados en Android nativo:

- **Room** para persistencia local estructurada
- **Repository** como capa de acceso y coordinación de datos
- **ViewModel** para desacoplar la lógica de UI
- **LiveData** para actualizar la interfaz de manera reactiva

Esta elección permite mantener una separación clara de responsabilidades, mejorar la mantenibilidad del código y reducir el acoplamiento entre interfaz, lógica y persistencia.

## 7. Estado actual del desarrollo

En el estado actual del proyecto ya se han implementado y validado los siguientes bloques:

- arquitectura base con Room, Repository, ViewModel y LiveData
- login local y recuperación de sesión
- gestión de tareas
- sistema de recompensas
- tienda virtual
- inventario de usuario
- persistencia de saldo e inventario tras reinicio
- validaciones de compra para evitar duplicados y compras sin saldo

Actualmente, uno de los siguientes focos de trabajo es consolidar la parte de pruebas, terminar de definir el comportamiento funcional de la acción de “Colocar” y trasladar todo el desarrollo técnico a una memoria estructurada y defendible.

## 8. Metodología de trabajo

El proyecto se está desarrollando por fases y sesiones de trabajo, manteniendo una documentación paralela al desarrollo. Para ello se han creado documentos internos de control como:

- panel general del proyecto
- bitácora de sesiones
- backlog técnico

Este enfoque permite llevar trazabilidad del avance real, registrar decisiones técnicas, detectar bloqueos y convertir el trabajo diario en material útil para la memoria final y la defensa del TFG.

## 9. Próximos apartados a desarrollar en esta memoria

Esta memoria base servirá como documento madre para consolidar en capítulos posteriores:

- requisitos funcionales y no funcionales
- diseño de arquitectura
- modelo de datos
- implementación técnica
- pruebas
- conclusiones
- mejoras futuras

## 10. Requisitos del sistema

### 10.1 Requisitos funcionales

Los requisitos funcionales definen las acciones concretas que la aplicación debe ser capaz de realizar para cumplir su objetivo principal.

#### RF-01. Registro de usuario local
La aplicación debe permitir crear usuarios nuevos almacenando sus credenciales y estado inicial en la base de datos local.

#### RF-02. Inicio de sesión local
La aplicación debe permitir que un usuario acceda mediante nombre y contraseña validados contra la base de datos interna.

#### RF-03. Persistencia de sesión
La aplicación debe recordar el usuario activo entre reinicios mediante almacenamiento local de sesión, evitando que tenga que autenticarse cada vez que abre la app.

#### RF-04. Visualización de tareas
La aplicación debe mostrar al usuario su lista de tareas registradas, recuperadas desde la base de datos y actualizadas de forma reactiva.

#### RF-05. Creación de tareas
La aplicación debe permitir añadir nuevas tareas con los atributos necesarios para su gestión, como título, descripción, categoría y recompensas asociadas.

#### RF-06. Actualización del estado de tarea
La aplicación debe permitir marcar tareas como completadas, persistiendo ese cambio en la base de datos.

#### RF-07. Recompensa por completar tareas
Cuando una tarea se completa, el sistema debe otorgar al usuario la cantidad correspondiente de experiencia y bayas.

#### RF-08. Sistema de niveles
La aplicación debe calcular automáticamente la progresión del usuario y actualizar su nivel cuando alcance el umbral de experiencia necesario.

#### RF-09. Visualización del progreso del usuario
La aplicación debe mostrar en la interfaz principal el nivel actual, la experiencia acumulada y el saldo de bayas del usuario.

#### RF-10. Catálogo de tienda
La aplicación debe mostrar un catálogo de muebles disponibles para compra, incluyendo información visual y precio.

#### RF-11. Compra de muebles
La aplicación debe permitir adquirir muebles únicamente si el usuario dispone de saldo suficiente y no posee ya el objeto seleccionado.

#### RF-12. Inventario del usuario
La aplicación debe mostrar el inventario de muebles adquiridos por el usuario, recuperados desde la relación persistida en la base de datos.

#### RF-13. Persistencia de inventario y saldo
La aplicación debe conservar correctamente el saldo de bayas y los muebles comprados tras cerrar y reabrir la aplicación.

#### RF-14. Feedback visual de acciones
La aplicación debe proporcionar retroalimentación visible al usuario tras acciones importantes, como completar tareas, subir de nivel o comprar muebles.

---

### 10.2 Requisitos no funcionales

Los requisitos no funcionales describen condiciones de calidad, arquitectura y comportamiento del sistema más allá de las funciones concretas.

#### RNF-01. Funcionamiento offline
La aplicación debe poder utilizarse sin conexión a internet, apoyándose exclusivamente en persistencia local.

#### RNF-02. Persistencia local segura
Los datos del usuario deben almacenarse en el propio dispositivo utilizando Room sobre SQLite, evitando dependencia de servicios externos.

#### RNF-03. Separación de responsabilidades
La aplicación debe mantener una arquitectura desacoplada basada en Room, Repository, ViewModel y LiveData para facilitar mantenimiento y escalabilidad.

#### RNF-04. Reactividad de interfaz
La interfaz debe reflejar automáticamente los cambios de estado del sistema sin necesidad de recargas manuales.

#### RNF-05. Fluidez de uso
Las operaciones de escritura y lectura deben ejecutarse sin bloquear la interfaz principal, garantizando una experiencia fluida.

#### RNF-06. Consistencia de datos
Las operaciones críticas, como completar tareas o comprar muebles, deben ejecutarse de forma atómica para evitar inconsistencias entre saldo, progreso e inventario.

#### RNF-07. Usabilidad
La interfaz debe ser comprensible, limpia y amigable, reduciendo la carga cognitiva del usuario y favoreciendo la continuidad de uso.

#### RNF-08. Reutilización de componentes
La aplicación debe reutilizar componentes visuales siempre que sea posible para reducir duplicación y simplificar mantenimiento.

#### RNF-09. Escalabilidad funcional
La arquitectura debe permitir incorporar en el futuro nuevas funcionalidades, como habitación interactiva persistente, logros o sincronización externa.

#### RNF-10. Trazabilidad y documentación
El proyecto debe mantener documentación paralela al desarrollo para facilitar seguimiento, defensa y mantenimiento posterior.

## 11. Arquitectura del sistema

La arquitectura de LeveLife se ha diseñado para priorizar tres objetivos principales: funcionamiento offline, separación clara de responsabilidades y actualización reactiva de la interfaz. Para ello, el proyecto adopta una arquitectura basada en componentes de Android Jetpack y en el patrón Repository, apoyándose en Room como sistema de persistencia local.

### 11.1 Enfoque arquitectónico general

LeveLife no sigue una arquitectura cliente-servidor tradicional. En su lugar, utiliza una arquitectura de persistencia local apoyada en SQLite mediante la librería Room. Esta decisión permite que la aplicación funcione sin conexión a internet, reduzca la latencia a cero y mantenga el control completo de los datos en el propio dispositivo del usuario.

Sobre esta base se ha construido una organización inspirada en el patrón MVVM (Model-View-ViewModel), donde cada capa tiene una responsabilidad concreta y bien delimitada.

### 11.2 Capas principales del sistema

#### Capa de modelo
La capa de modelo representa el núcleo de datos y lógica del dominio. En ella se encuentran las entidades persistentes del sistema, como User, Task, Furniture y la tabla intermedia UserFurnitureCrossRef.

Estas clases no solo almacenan datos, sino que también encapsulan parte de la lógica de negocio. Por ejemplo, la clase User contiene métodos como addExperience(), addBerries(), spendBerries() y getProgressPercentage(), lo que permite centralizar las reglas de progresión y economía virtual en el propio dominio.

#### Capa de persistencia
La persistencia se gestiona mediante Room, que actúa como capa de abstracción sobre SQLite. Room permite definir las tablas mediante entidades anotadas, estructurar el acceso a los datos a través de interfaces DAO y validar las consultas SQL en tiempo de compilación.

La base de datos principal se implementa en AppDatabase, siguiendo el patrón Singleton para garantizar una única instancia en toda la aplicación. Esta decisión evita conflictos de acceso, reduce consumo de memoria y unifica el punto de entrada a la persistencia local.

#### Capa repositorio
MainRepository actúa como intermediario entre la base de datos y la interfaz. Su función es centralizar las operaciones del sistema, coordinar acceso a DAOs y servir como fuente única de verdad para la aplicación.

Esta capa resulta especialmente importante en operaciones críticas, como completar tareas o comprar muebles, ya que permite encapsular validaciones de negocio y transacciones atómicas sin repartir lógica entre Activities.

#### Capa ViewModel
MainViewModel se encarga de exponer a la interfaz los datos observables necesarios para cada pantalla y de delegar en el repositorio las acciones del usuario.

Esta capa desacopla la lógica de presentación respecto a los controladores de interfaz y facilita que el estado sobreviva de forma más limpia a cambios de configuración. Además, permite que las Activities se mantengan más ligeras y centradas en mostrar información y reaccionar a eventos.

#### Capa de vista
La capa de vista está compuesta por Activities, adapters y layouts XML. Su responsabilidad es representar el estado actual de la aplicación y capturar las interacciones del usuario, evitando contener lógica de negocio compleja.

Pantallas como MainActivity, TaskActivity, ShopActivity o InventoryActivity observan datos expuestos por el ViewModel y actualizan la interfaz en consecuencia. Este enfoque reduce acoplamiento y mejora la mantenibilidad del proyecto.

### 11.3 Flujo de datos

El flujo de datos de LeveLife se apoya en el principio de Single Source of Truth. La base de datos local actúa como única fuente fiable del estado del sistema.

Cuando el usuario realiza una acción, como completar una tarea o comprar un mueble, la Activity no modifica directamente el estado persistente. En su lugar:

1. la vista comunica la acción al ViewModel
2. el ViewModel delega la operación en el repositorio
3. el repositorio ejecuta la lógica y actualiza la base de datos
4. Room emite automáticamente los cambios a través de LiveData
5. la interfaz recibe el nuevo estado y se actualiza de forma reactiva

Gracias a este flujo, la aplicación evita recargas manuales, reduce riesgos de desincronización entre UI y datos persistidos y mantiene una arquitectura más robusta.

### 11.4 Gestión de concurrencia

Android impide realizar operaciones de base de datos en el hilo principal cuando estas pueden bloquear la interfaz. Para evitar congelamientos y errores ANR, las operaciones de escritura y lógica crítica del sistema se ejecutan en segundo plano mediante ExecutorService.

Este enfoque se utiliza tanto en inserciones y actualizaciones simples como en operaciones más delicadas, por ejemplo:

- completar una tarea y aplicar su recompensa
- comprar un mueble y descontar bayas
- insertar relaciones en inventario

Además, al apoyarse en LiveData para observación, la entrega de resultados a la interfaz se realiza de forma segura y reactiva, sin necesidad de forzar recargas manuales desde las Activities.

### 11.5 Persistencia de sesión

La sesión del usuario se mantiene mediante SharedPreferences, almacenando de forma local el identificador del usuario activo. Esta decisión encaja con el enfoque offline-first del proyecto y evita depender de tokens externos o de una infraestructura remota.

SplashActivity actúa como punto de entrada y enrutamiento inicial. Cuando la aplicación arranca, verifica si existe una sesión activa persistida. Si la encuentra, redirige directamente al usuario a la aplicación sin necesidad de relogin; en caso contrario, muestra el flujo de autenticación.

### 11.6 Ventajas de la arquitectura elegida

La arquitectura adoptada ofrece varias ventajas para este proyecto:

- permite funcionamiento completamente offline
- mejora la organización interna del código
- reduce el acoplamiento entre interfaz y persistencia
- facilita el mantenimiento y la escalabilidad
- protege operaciones críticas mediante transacciones y validaciones centralizadas
- favorece una interfaz reactiva y coherente con el estado real del sistema

En conjunto, esta arquitectura resulta adecuada para una aplicación de hábitos gamificada desarrollada como TFG, ya que combina simplicidad de despliegue, robustez funcional y una estructura defendible a nivel técnico.

## 12. Modelo de datos

El modelo de datos de LeveLife se ha diseñado para soportar el funcionamiento offline de la aplicación, garantizar la persistencia local del progreso del usuario y mantener una estructura relacional clara, escalable y coherente con las necesidades funcionales del sistema.

La persistencia se apoya en SQLite mediante la librería Room, lo que permite modelar las entidades del dominio como clases Java anotadas y establecer relaciones entre ellas de forma segura. El diseño actual parte de cuatro elementos principales: User, Task, Furniture y UserFurnitureCrossRef.

### 12.1 Entidad User

La entidad User representa el perfil global del usuario dentro de la aplicación. En ella se almacena la información necesaria para la autenticación local y para el seguimiento del progreso gamificado.

Sus atributos principales son:

- id: identificador único autogenerado
- user_name: nombre de usuario
- password: contraseña del usuario
- level: nivel actual
- experience: experiencia acumulada en el nivel actual
- berries: saldo de moneda virtual disponible

Además de almacenar estos datos, la clase incorpora lógica de negocio relacionada con la progresión del usuario. Entre sus métodos destacan el cálculo de experiencia necesaria para subir de nivel, la suma de experiencia, la gestión de bayas y el cálculo del porcentaje de progreso. Esta decisión permite encapsular reglas del dominio directamente en el modelo y evita dispersarlas por la interfaz.

### 12.2 Entidad Task

La entidad Task representa una tarea o hábito asociado a un usuario concreto. Cada tarea contiene tanto información descriptiva como los datos necesarios para integrarse en el sistema de recompensas.

Sus atributos principales son:

- id: identificador único autogenerado
- user_id: clave foránea que referencia al usuario propietario
- title: título de la tarea
- description: descripción breve
- category: categoría de organización
- reward_xp: experiencia que se obtiene al completarla
- reward_berries: cantidad de bayas asociada
- isCompleted: estado de completado
- frequency: frecuencia o tipo de repetición

La relación entre User y Task es de tipo 1:N, ya que un usuario puede tener múltiples tareas, mientras que cada tarea pertenece únicamente a un usuario. Esta relación se implementa mediante clave foránea con borrado en cascada, de forma que si un usuario desaparece también se eliminan sus tareas asociadas, evitando registros huérfanos.

### 12.3 Entidad Furniture

La entidad Furniture representa el catálogo de objetos que pueden adquirirse en la tienda virtual de la aplicación.

Sus atributos principales son:

- id: identificador único autogenerado
- name: nombre del mueble
- price: coste en bayas
- image_ref: referencia al recurso gráfico asociado
- category: categoría visual o funcional
- description: descripción del objeto
- type: tipo de mueble

Esta entidad no pertenece inicialmente a ningún usuario concreto, ya que actúa como catálogo general disponible para todos los perfiles registrados en la aplicación.

### 12.4 Relación de inventario: UserFurnitureCrossRef

Para modelar el inventario se utiliza una relación N:M entre usuarios y muebles. Esta decisión responde a una necesidad funcional clara: un usuario puede adquirir varios muebles y un mismo mueble puede existir en el inventario de varios usuarios distintos.

En lugar de duplicar información dentro de la tabla de usuarios o de muebles, se ha diseñado una tabla intermedia denominada UserFurnitureCrossRef, cuya función es enlazar ambos extremos de la relación.

Sus atributos principales son:

- userId: identificador del usuario
- furnitureId: identificador del mueble

La clave primaria es compuesta, formada por ambos campos. Esto impide registrar dos veces el mismo mueble para el mismo usuario y protege la integridad del inventario. Además, esta estructura resulta adecuada para futuras ampliaciones, como añadir atributos extra relacionados con la colocación o el estado visual del objeto dentro de la habitación.

### 12.5 Relaciones del modelo

El modelo relacional de LeveLife puede resumirse en dos relaciones principales:

- User -> Task: relación 1:N
- User -> Furniture: relación N:M a través de UserFurnitureCrossRef

Esta combinación permite cubrir de forma directa los dos ejes funcionales del sistema:

- gestión personalizada de tareas y progreso
- inventario personalizado de recompensas visuales

### 12.6 Integridad y consistencia del modelo

El diseño del modelo de datos no solo busca almacenar información, sino también proteger la coherencia del sistema. Para ello se aplican varios mecanismos:

- claves primarias autogeneradas para identificar registros de forma única
- claves foráneas para asegurar vínculos válidos entre entidades
- borrado en cascada en relaciones dependientes
- clave compuesta en la tabla intermedia del inventario
- validaciones de dominio en métodos del modelo, como el control de saldo o la gestión de experiencia

Además, las operaciones críticas que afectan a varias tablas, como completar una tarea o comprar un mueble, se ejecutan mediante transacciones en la capa repositorio. De este modo, el estado del usuario, el inventario y la progresión permanecen sincronizados.

### 12.7 Justificación del diseño elegido

El modelo de datos actual resulta adecuado para una aplicación Android de hábitos con gamificación por varios motivos.

En primer lugar, mantiene una estructura simple y fácil de mantener, lo que favorece el desarrollo iterativo del proyecto. En segundo lugar, está alineado con las funcionalidades principales del sistema: autenticación, tareas, recompensas, tienda e inventario. En tercer lugar, deja preparada la base para futuras ampliaciones sin necesidad de rehacer el esquema principal.

Gracias a este diseño, la aplicación puede operar completamente en local, conservar el progreso del usuario y garantizar que cada acción importante tenga persistencia real en el dispositivo.

## 13. Implementación del sistema

La implementación de LeveLife se ha desarrollado de forma incremental, partiendo primero de la base de persistencia local y la autenticación, para avanzar después hacia la gestión de tareas, la gamificación y el sistema de tienda e inventario. Esta estrategia ha permitido validar cada bloque de forma progresiva y mantener la estabilidad del proyecto mientras se incorporaban nuevas funcionalidades.

### 13.1 Persistencia local y base de datos

La base de datos de la aplicación se ha implementado mediante Room sobre SQLite, utilizando una clase AppDatabase como punto central de acceso a la persistencia. Esta base de datos contiene las entidades principales del sistema: User, Task, Furniture y UserFurnitureCrossRef.

AppDatabase se ha configurado siguiendo el patrón Singleton para garantizar la existencia de una única instancia de base de datos en toda la aplicación. Además, incorpora un ExecutorService para ejecutar operaciones en segundo plano y un callback de inicialización que actúa como seeder, cargando usuarios, tareas y muebles de prueba durante el desarrollo.

Esta parte del sistema constituye el núcleo de FASE 2, donde se consolidó la estructura física de la base de datos, la normalización de entidades y el funcionamiento del acceso local a datos.

### 13.2 Implementación de DAOs y acceso a datos

El acceso a la base de datos se organiza mediante DAOs específicos que encapsulan las operaciones necesarias sobre cada entidad.

- UserDao gestiona operaciones relacionadas con usuarios, autenticación y consulta del perfil activo.
- TaskDao permite insertar, actualizar, eliminar y consultar tareas, así como recuperar información útil para el estado de progreso.
- FurnitureDao gestiona tanto el catálogo de muebles como la recuperación del inventario del usuario mediante consultas con JOIN sobre la tabla intermedia.

Gracias a Room, las consultas se validan en compilación, lo que reduce errores SQL y mejora la robustez del sistema. Además, varias consultas se exponen como LiveData, permitiendo que la interfaz reciba automáticamente cambios en los datos sin necesidad de refresco manual.

### 13.3 Repositorio y centralización de la lógica

La aplicación utiliza MainRepository como capa intermedia entre el almacenamiento local y la interfaz. Esta clase centraliza operaciones de lectura y escritura, coordina el acceso a los DAOs y encapsula la lógica crítica que afecta a varias entidades a la vez.

Durante el desarrollo, esta capa ha ganado protagonismo especialmente en operaciones sensibles, como:

- completar una tarea y aplicar sus recompensas
- comprar un mueble y descontar bayas
- insertar relaciones de inventario entre usuario y mueble

La centralización de esta lógica ha permitido reforzar el principio de fuente única de verdad y proteger la coherencia del sistema mediante transacciones atómicas, en línea con la arquitectura defendida en la documentación técnica del proyecto.

### 13.4 Autenticación local y preservación de sesión

La autenticación se implementa de forma completamente local, validando el nombre de usuario y la contraseña contra la tabla users de la base de datos. Esta elección evita dependencia de red y se ajusta al planteamiento offline-first del proyecto.

Una vez autenticado, el sistema guarda el identificador del usuario activo mediante SharedPreferences. Gracias a ello, el usuario no necesita volver a iniciar sesión cada vez que abre la aplicación. SplashActivity actúa como punto de entrada inicial y decide si debe mostrar el flujo de login o permitir el acceso directo a la aplicación.

Esta parte del sistema fue una de las piezas clave de FASE 2, junto con las pruebas de persistencia local realizadas con herramientas como Database Inspector.

### 13.5 Gestión de tareas e interfaz de hábitos

La gestión de tareas constituye uno de los bloques funcionales principales de la aplicación. Cada usuario dispone de una colección personalizada de tareas almacenadas en la base de datos y mostradas en la interfaz mediante RecyclerView.

TaskActivity y su adapter permiten visualizar la lista de tareas, registrar el estado de completado y mantener una interacción ágil dentro del flujo principal de la aplicación. La implementación también incorpora patrones de usabilidad como empty states y feedback visual inmediato, apoyándose en componentes ligeros y reciclables de Android.

Este bloque se consolidó especialmente durante FASE 3, en la que se conectaron las vistas con el repositorio y se implementó la lógica de hábitos diarios en la app Android.

### 13.6 Sistema de gamificación

La gamificación de LeveLife se apoya en dos elementos principales: experiencia (XP) y bayas como moneda virtual. Cada tarea contiene valores de recompensa que se aplican cuando el usuario la completa correctamente.

La lógica de progresión se implementa en el modelo User mediante métodos como addExperience(), addBerries(), spendBerries() y getProgressPercentage(). Esto permite que el cálculo del progreso y de los niveles no dependa de la interfaz y quede encapsulado en el dominio del sistema.

En la interfaz principal, el usuario visualiza su nivel actual, su progreso y su saldo. Además, el sistema genera feedback visual cuando se completan tareas o cuando se produce una subida de nivel, reforzando así la sensación de avance y recompensa inmediata. Esta parte está alineada con las historias de usuario y criterios de aceptación definidos desde FASE 1, donde ya se estableció que el progreso debía actualizarse de forma reactiva y visible.

### 13.7 Implementación de la tienda virtual

La tienda se implementa como un catálogo de muebles persistido en la base de datos local y mostrado al usuario en formato de cuadrícula. Cada mueble incluye nombre, precio, categoría y referencia gráfica.

ShopActivity, junto con FurnitureAdapter, permite visualizar el catálogo y aplicar una validación visual previa en función del saldo actual del usuario y de los muebles ya adquiridos. De este modo, los objetos no asequibles o ya comprados aparecen deshabilitados, reduciendo interacciones erróneas y mejorando la claridad de la interfaz.

Además, la operación de compra se ha reforzado mediante lógica transaccional en el repositorio, evitando pérdidas de saldo en situaciones inconsistentes y garantizando que la compra solo se complete si se cumplen todas las validaciones necesarias. Esta parte conecta directamente con la fase de gamificación e inventario prevista en la planificación del proyecto.

### 13.8 Inventario del usuario

El inventario permite visualizar los muebles que el usuario ha adquirido y que han quedado asociados a su perfil mediante la relación N:M implementada en UserFurnitureCrossRef.

InventoryActivity recupera estos datos desde el ViewModel y los muestra utilizando un adapter específico reutilizando parte de la estructura visual de la tienda. Esta reutilización reduce duplicación de layouts y mantiene coherencia gráfica entre catálogo e inventario.

En el estado actual del proyecto, el inventario ya es persistente y funcional. El usuario puede comprobar que los muebles comprados permanecen disponibles tras cerrar y reabrir la aplicación. La parte relacionada con una habitación completamente interactiva o con colocación persistente de objetos todavía debe definirse como cierre de FASE 4 o como ampliación futura, según el alcance final del TFG.

### 13.9 Validaciones y protección de consistencia

Durante la implementación se detectó la necesidad de reforzar varias operaciones críticas para evitar inconsistencias de datos o regresiones funcionales.

Entre las validaciones más importantes aplicadas destacan:

- impedir recompensas duplicadas al completar tareas ya marcadas
- impedir compras repetidas del mismo mueble
- evitar descuento de bayas si la operación de compra no puede completarse
- garantizar que usuario, inventario y progreso se actualicen de forma sincronizada

Estas mejoras se resolvieron desplazando la lógica sensible hacia el repositorio y encapsulándola en transacciones. Con ello, la interfaz deja de asumir responsabilidades que no le corresponden y el estado persistente del sistema queda mejor protegido.

### 13.10 Estado actual de la implementación

En el momento actual del proyecto, la implementación cubre ya los bloques principales del sistema:

- persistencia local mediante Room
- autenticación y recuperación de sesión
- gestión de tareas
- sistema de experiencia, niveles y bayas
- tienda virtual
- inventario persistente
- feedback visual principal para acciones relevantes

A partir de este punto, el trabajo restante se centra principalmente en tres líneas: consolidar las pruebas, decidir el alcance final de la interacción con el inventario y trasladar toda la implementación validada a la documentación definitiva del TFG.

## 14. Pruebas y validación

La fase de pruebas de LeveLife se ha planteado con un enfoque progresivo y práctico, adaptado a una aplicación Android con arquitectura offline-first y persistencia local. En lugar de depender de pruebas sobre red o servicios externos, la validación del sistema se ha centrado en comprobar la consistencia del estado local, el comportamiento funcional de los flujos principales y la robustez de las operaciones críticas del sistema.

Este enfoque está alineado con la planificación general del proyecto, donde se contemplan pruebas de persistencia local, pruebas funcionales integradas y validación completa del sistema en las fases finales del TFG.

### 14.1 Estrategia de validación

La validación del proyecto se ha apoyado en tres niveles principales:

- comprobación de persistencia local y estructura de datos
- pruebas funcionales de flujo completo dentro de la aplicación
- revisión de incidencias detectadas durante el desarrollo y corrección de regresiones

En las primeras fases, la base de datos y las consultas se validaron con herramientas como Database Inspector y mediante datos semilla cargados desde el seeder de Room, tal y como se recoge en la documentación de FASE 2.

Posteriormente, conforme el sistema fue creciendo, se añadieron pruebas funcionales manuales centradas en los escenarios reales de uso: completar tareas, obtener recompensas, subir de nivel, comprar muebles, comprobar el inventario y verificar la persistencia de la sesión del usuario.

### 14.2 Pruebas de persistencia local

Dado que LeveLife funciona sin conexión a internet y almacena toda la información en local, uno de los primeros objetivos de validación fue comprobar que la persistencia resultara estable y coherente.

Durante FASE 2 se validaron los siguientes aspectos:

- creación correcta de tablas
- inserción de usuarios, tareas y muebles iniciales
- funcionamiento del seeder
- ejecución correcta de operaciones CRUD
- acceso seguro a la base de datos fuera del hilo principal

Estas comprobaciones se realizaron utilizando Room, DAO's y Database Inspector, en coherencia con la estrategia de persistencia local definida en la fase de backend y autenticación.

Además, también se verificó la preservación de sesión local mediante SharedPreferences, asegurando que el usuario activo pudiera recuperarse automáticamente en el arranque de la aplicación, tal y como ya se había definido en FASE 1 y FASE 2.

### 14.3 Pruebas funcionales del sistema

Una vez consolidado el núcleo de persistencia y autenticación, se realizaron pruebas manuales sobre los principales flujos funcionales de la aplicación.

#### Prueba 1. Subida de nivel
Se accedió con un usuario válido y se completaron tareas hasta alcanzar el siguiente umbral de experiencia.

Resultados observados:
- la experiencia aumentó correctamente
- el nivel cambió cuando correspondía
- la barra de progreso se animó de forma adecuada
- el sistema mostró el feedback visual esperado

Esta validación confirma que el sistema de gamificación cumple el criterio definido desde FASE 1, donde se establecía que el progreso del usuario debía actualizarse de forma automática y reactiva.

#### Prueba 2. Flujo de usuario nuevo
Se registró un usuario nuevo y se verificó el comportamiento inicial del sistema.

Resultados observados:
- el login fue correcto
- el saldo inicial del usuario fue correcto
- el inventario apareció vacío
- el usuario pudo completar tareas
- el usuario recibió experiencia y bayas
- el usuario pudo comprar un mueble al alcanzar saldo suficiente

Esta prueba valida el flujo básico de entrada al sistema y la coherencia entre registro, progreso, economía virtual y tienda.

#### Prueba 3. Persistencia de sesión, saldo e inventario
Se realizó una compra en la tienda, se cerró completamente la aplicación y se volvió a abrir.

Resultados observados:
- SplashActivity recuperó correctamente la sesión activa
- el saldo del usuario se mantuvo tras reinicio
- el mueble comprado siguió apareciendo en el inventario

Esta prueba confirma la persistencia real del estado del usuario y del inventario, en línea con los criterios funcionales planteados para tienda e inventario.

#### Prueba 4. Inventario vacío
Se accedió con un usuario sin compras previas y se revisó el comportamiento del inventario.

Resultados observados:
- la pantalla resultó comprensible
- el sistema informó del estado vacío mediante feedback visual
- no se detectó comportamiento confuso ni ruptura de interfaz

Esta prueba se relaciona con los patrones de empty states y feedback visual defendidos en la documentación técnica del proyecto.

### 14.4 Incidencias detectadas y correcciones aplicadas

Durante el proceso de validación se detectaron varias incidencias funcionales relevantes que fueron corregidas durante el desarrollo.

#### Compra duplicada con pérdida de saldo
Se detectó una situación en la que podía descontarse saldo al usuario incluso si la compra no llegaba a consolidarse correctamente. Esta incidencia se resolvió desplazando toda la lógica de compra a una transacción controlada desde el repositorio, validando propiedad previa del mueble, saldo disponible e inserción correcta del inventario en una única operación atómica.

#### Riesgo de recompensas duplicadas en tareas
Se reforzó la lógica de completado de tareas para impedir que una misma tarea pudiera otorgar recompensas más de una vez. La operación pasó a ejecutarse de forma centralizada en el repositorio, dentro de una transacción y con validación previa del estado real de la tarea.

#### Regresión del feedback visual de recompensa
Tras el refactor de completeTask(), se perdió temporalmente el Toast que informaba de la experiencia y las bayas obtenidas al completar una tarea. Esta regresión se corrigió añadiendo un evento observable en el ViewModel, de forma que la Activity vuelve a mostrar el mensaje sin recuperar lógica de negocio que ya no le corresponde.

Estas correcciones refuerzan la coherencia de la arquitectura aplicada y demuestran una evolución del proyecto hacia un modelo más robusto, con mejor separación de responsabilidades y mayor protección frente a inconsistencias.

### 14.5 Validación de la arquitectura mediante pruebas

Las pruebas no solo han servido para confirmar que las pantallas funcionan, sino también para validar indirectamente la arquitectura elegida.

El comportamiento observado durante las pruebas confirma que:

- la base de datos actúa como fuente única de verdad
- la interfaz se actualiza reactivamente mediante LiveData
- las operaciones sensibles se ejecutan fuera del hilo principal
- las transacciones del repositorio protegen la consistencia del sistema
- la persistencia local mantiene el estado real del usuario entre reinicios

Todo ello respalda técnicamente la elección de Room, Repository, ViewModel y LiveData como base del sistema, tal y como se justificó en FASE 1, FASE 2 y en el manual de defensa.

### 14.6 Estado actual de validación

En el estado actual del proyecto puede considerarse validado el núcleo funcional de LeveLife:

- autenticación local
- preservación de sesión
- gestión de tareas
- sistema de recompensas
- subida de nivel
- tienda virtual
- inventario persistente
- feedback visual principal

A partir de este punto, las pruebas restantes se orientan sobre todo a formalizar casos de prueba, completar la documentación definitiva y decidir el alcance final de las interacciones avanzadas del inventario dentro de la última fase del proyecto.

## 15. Conclusiones y trabajo futuro

### 15.1 Conclusiones

El desarrollo de LeveLife ha permitido construir una aplicación móvil Android centrada en la organización personal y la motivación del usuario mediante mecánicas de gamificación. A lo largo del proyecto se ha pasado de una fase inicial de análisis y diseño a una implementación funcional basada en persistencia local, arquitectura desacoplada y validación progresiva de los principales flujos del sistema.

Uno de los principales logros del proyecto ha sido demostrar que es posible diseñar una aplicación de hábitos útil y coherente sin depender de infraestructura remota. La combinación de Room, Repository, ViewModel y LiveData ha permitido mantener una arquitectura clara, reactiva y alineada con el enfoque offline-first definido desde las primeras fases del trabajo.

Desde el punto de vista funcional, el sistema ya permite cubrir el núcleo de valor de la aplicación:

- autenticación local
- preservación de sesión
- gestión de tareas
- sistema de experiencia, niveles y bayas
- tienda virtual
- inventario persistente
- feedback visual en acciones clave

Además, el proceso de desarrollo ha servido para reforzar conocimientos técnicos relevantes en Android nativo con Java, especialmente en aspectos como modelado de datos, persistencia local, concurrencia, separación de responsabilidades, uso de RecyclerView, gestión del ciclo de vida y diseño de una arquitectura mantenible.

Otro aspecto importante del proyecto ha sido la evolución del propio código. Durante el desarrollo no solo se añadieron funcionalidades, sino que también se detectaron y corrigieron incidencias de consistencia, especialmente en operaciones sensibles como la compra de muebles o la entrega de recompensas por completar tareas. Esto ha permitido que el sistema gane robustez y se aproxime a una implementación más sólida y defendible a nivel técnico.

En conjunto, puede concluirse que LeveLife cumple de forma satisfactoria el objetivo general del proyecto: desarrollar una aplicación Android gamificada que ayude al usuario a gestionar tareas y mantener su motivación mediante progreso visible, recompensas y personalización virtual.

### 15.2 Limitaciones actuales

A pesar de los avances logrados, el proyecto todavía presenta algunas limitaciones propias de su alcance actual y del tiempo disponible dentro del TFG.

En primer lugar, la aplicación funciona exclusivamente en local, por lo que el progreso del usuario no se sincroniza entre dispositivos ni existe copia de seguridad automática en la nube. Esta decisión fue adecuada para priorizar simplicidad, privacidad y viabilidad técnica, pero limita la portabilidad del sistema.

En segundo lugar, aunque el inventario ya es persistente y funcional, la parte de habitación virtual interactiva y la colocación avanzada de muebles todavía no está completamente desarrollada como experiencia final cerrada. Esta funcionalidad se mantiene como uno de los puntos de evolución más claros del proyecto.

También debe considerarse que gran parte de la validación se ha realizado mediante pruebas funcionales manuales y controladas sobre los flujos principales. Aunque este enfoque ha sido suficiente para consolidar el núcleo del sistema, una versión futura del producto debería ampliar la cobertura de pruebas automatizadas y pruebas de interfaz.

### 15.3 Trabajo futuro

A partir del estado actual del proyecto, pueden identificarse varias líneas de evolución que ampliarían el valor funcional de LeveLife.

#### Habitación virtual más avanzada
La evolución más natural del sistema consiste en ampliar el inventario actual hacia una habitación interactiva real, donde el usuario pueda colocar muebles de forma persistente y visualizar una personalización más rica de su espacio virtual.

#### Persistencia ampliada del inventario
Relacionado con lo anterior, una mejora futura consistiría en añadir atributos adicionales al inventario, por ejemplo el estado de colocación del objeto, su posición dentro de la habitación o variantes visuales según el progreso del usuario.

#### Sincronización externa o copias de seguridad
Aunque el proyecto se ha diseñado como aplicación offline-first, una versión más avanzada podría incorporar sincronización opcional en la nube o un sistema de exportación/importación local para conservar el progreso entre dispositivos.

#### Sistema de logros y metas avanzadas
Otra línea de mejora sería incorporar insignias, hitos o recompensas especiales por mantener hábitos durante varios días, alcanzar determinados niveles o completar retos de categoría concreta.

#### Estadísticas y analítica personal
También resultaría interesante añadir una capa de visualización de progreso a medio y largo plazo, mostrando métricas sobre tareas completadas, constancia semanal, evolución de experiencia o hábitos por categoría.

#### Mejora del sistema de pruebas
Como ampliación técnica, el proyecto podría reforzarse mediante una estrategia de testing más completa, integrando más pruebas unitarias, pruebas instrumentadas y automatización de validaciones de interfaz.

### 15.4 Valoración final

LeveLife no se limita a ser un ejercicio técnico aislado, sino que constituye una propuesta funcional con una identidad clara, un enfoque de usuario definido y una arquitectura suficientemente sólida como para crecer más allá del alcance académico inicial.

El proyecto ha permitido combinar diseño, lógica de negocio, persistencia local, experiencia de usuario y documentación técnica en un único desarrollo coherente. Por ello, más allá del resultado actual, el trabajo realizado deja una base realista y ampliable para seguir evolucionando la aplicación en futuras iteraciones.