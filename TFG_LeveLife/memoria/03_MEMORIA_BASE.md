# Memoria Base - LeveLife

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