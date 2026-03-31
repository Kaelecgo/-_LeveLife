# Log de Implementación Técnica - LeveLife

Este documento resume los principales bloques de implementación técnica incorporados al proyecto LeveLife, así como la justificación de las decisiones adoptadas. Su objetivo es servir como apoyo para la memoria del TFG y como trazabilidad del proceso de maduración técnica de la aplicación.

---

## Resumen general de la sesión (29/03/2026)

Durante esta iteración se realizó una revisión global del proyecto con un doble objetivo: reforzar la base técnica de la aplicación y evolucionar el sistema de tareas hacia un modelo de hábitos más rico, gamificado y alineado con la identidad ecológica de LeveLife.

La jornada no se centró únicamente en añadir nuevas funciones, sino en estabilizar el proyecto sobre `master`, corregir inconsistencias entre código y base de datos, recuperar trabajo funcional avanzado y consolidar una base más robusta para las siguientes fases.

Como resultado, el proyecto pasó de un estado de prototipo funcional a una versión más madura en seguridad, persistencia, dominio de tareas, validación técnica y experiencia de usuario.

---

## 1. Refuerzo de seguridad y persistencia

### Implementación realizada

- Se sustituyó el almacenamiento y validación de contraseñas en texto plano por un sistema de hashing seguro mediante `PasswordUtils.java`, utilizando PBKDF2 con salt aleatoria.
- Se incorporó una lógica de actualización transparente para usuarios antiguos, permitiendo convertir contraseñas heredadas al nuevo formato protegido durante el flujo de login.
- Se eliminó la dependencia de migraciones destructivas como estrategia principal de evolución de base de datos.
- `AppDatabase.java` quedó alineada con la versión 7 del esquema mediante migraciones explícitas.
- Se añadió `fallbackToDestructiveMigrationOnDowngrade()` como medida de protección en entorno de desarrollo ante desajustes locales de versión.

### Problema que resolvía

- El almacenamiento en texto plano suponía una debilidad de seguridad importante.
- El uso de migraciones destructivas implicaba pérdida de progreso, inventario y saldo ante cambios de esquema.
- La desalineación entre entidades y versión de Room llegó a provocar fallos de arranque durante la integración.

### Justificación técnica

Este bloque mejora dos atributos de calidad esenciales del sistema: seguridad e integridad de datos. El uso de hashing protege la privacidad del usuario incluso ante acceso local no autorizado a la base de datos, mientras que las migraciones explícitas permiten evolucionar la app sin sacrificar el estado acumulado del usuario.

---

## 2. Estabilización del esquema y recuperación del estado funcional

### Implementación realizada

- Se revisó la integración de cambios sobre `master`.
- Se recuperó trabajo funcional desde una copia temporal de seguridad tras la sincronización del repositorio.
- Se corrigieron inconsistencias entre el esquema real de la base de datos y las entidades de Room.
- Se completó la migración `6 -> 7`, incorporando correctamente los nuevos campos:
    - `eco_coins`
    - `eco_reward`
    - `difficulty`
    - `frequency`
    - `last_completed_at`
- Se ajustaron `defaultValue` donde fue necesario para que Room validase correctamente la migración.
- Se corrigió el repoblado del catálogo para que la tienda vuelva a cargar muebles automáticamente si la tabla `furniture` está vacía.

### Problema que resolvía

Tras la sincronización del proyecto, la aplicación llegó a crashear por incompatibilidades entre esquema, versión de base de datos y entidades del modelo.

### Justificación técnica

Este bloque fue clave para recuperar la estabilidad del proyecto. No se trató solo de añadir nuevas capacidades, sino de garantizar que la aplicación volviera a arrancar correctamente y que la persistencia local mantuviera coherencia real con el código fuente.

---

## 3. Evolución del modelo de tareas hacia hábitos enriquecidos

### Implementación realizada

- La entidad `Task` evolucionó desde un modelo básico hacia una estructura más rica, incorporando:
    - `difficulty`
    - `frequency`
    - `eco_reward`
    - `is_eco_task`
    - `last_completed_at`
- Se integró una lógica de recurrencia básica mediante `TaskRecurrenceUtils`.
- Se restauró e integró sobre `master` un nuevo flujo de creación de tareas basado en formulario enriquecido.
- Se añadieron clases auxiliares como:
    - `TaskDraft`
    - `TaskReward`
    - `TaskRewardCalculator`
    - `TaskRecurrenceUtils`

### Problema que resolvía

El modelo anterior era suficiente para tareas simples, pero se quedaba corto para representar hábitos recurrentes, dificultad variable y mecánicas ecológicas con valor propio dentro del sistema.

### Justificación técnica

Este cambio permite que LeveLife deje de comportarse como una lista básica de tareas y avance hacia un sistema de hábitos más coherente con su propuesta de valor. También mejora la escalabilidad del dominio y deja preparada la base para futuras ampliaciones.

---

## 4. Lógica centralizada de recompensas y economía ecológica

### Implementación realizada

- Se introdujo una segunda moneda virtual: `EcoCoins`.
- Se añadió `eco_coins` a la entidad `User`.
- El flujo de recompensas quedó gestionado desde `MainRepository` y `MainViewModel`, notificando al usuario mediante `rewardMessage`.
- Se creó `TaskRewardCalculator.java` para centralizar el cálculo de recompensas de:
    - XP
    - Berries
    - EcoCoins
- Las recompensas pasaron a depender del esfuerzo (dificultad) y del impacto ecológico de la tarea.

### Problema que resolvía

La lógica de recompensas estaba demasiado acoplada al flujo visible de la aplicación y la dimensión ecológica todavía no tenía una mecánica propia suficientemente tangible.

### Justificación técnica

Centralizar esta lógica mejora la mantenibilidad, reduce duplicación y protege la coherencia del sistema. Además, la introducción de EcoCoins refuerza la identidad ecológica del proyecto y convierte la sostenibilidad en una mecánica real, no solo estética.

---

## 5. Normalización de datos y robustez de la lógica interna

### Implementación realizada

- Se detectó un bug importante causado por comparar la lógica interna con textos visibles de UI que incluían emojis o variantes de idioma.
- Se eliminó la dependencia directa entre lógica de negocio y textos mostrados en dropdowns.
- Se reforzó la normalización de categorías, dificultades y frecuencias en `Task.java`.
- Se amplió la normalización para eliminar también diacríticos y corregir el cálculo cuando la dificultad llega como `Fácil` o `Difícil`.
- Se añadieron pruebas específicas para cubrir estos casos de normalización.
- Se ajustó la vista previa de recompensas para recalcularse automáticamente al cambiar los selectores del formulario.

### Problema que resolvía

Los valores visibles de la interfaz podían no coincidir exactamente con las constantes internas del sistema, lo que provocaba errores de cálculo, pérdida de recompensas ecológicas y desajustes en el flujo de completado.

### Justificación técnica

Este bloque demuestra una mejora clara de robustez. La lógica del sistema deja de depender de cómo se renderiza el texto en pantalla y pasa a apoyarse en reglas internas más estables, compatibles con internacionalización, tildes, cambios visuales y futuras iteraciones de interfaz.

---

## 6. Mejora de la experiencia de creación de tareas

### Implementación realizada

- Se sustituyó el flujo mínimo de creación de tareas por un `BottomSheetDialog`.
- El nuevo formulario permite introducir:
    - título
    - descripción
    - categoría
    - dificultad
    - frecuencia
- Se añadió una vista previa de recompensa en tiempo real antes de confirmar la creación.
- Se integró el nuevo layout `bottom_sheet_create_task.xml` y los recursos asociados.

### Problema que resolvía

El flujo anterior era demasiado básico y no reflejaba la evolución real del modelo de tareas. Además, ofrecía poco feedback al usuario sobre el impacto de sus elecciones.

### Justificación técnica

La nueva interfaz mejora la claridad, reduce fricción y alinea la experiencia de usuario con la complejidad real del sistema de hábitos y recompensas.

---

## 7. Corrección del flujo de completado y recurrencia MVP

### Implementación realizada

- Se abordó un bug por el cual una tarea recién creada podía otorgar recompensa sin quedar marcada ni deshabilitada correctamente.
- Se ajustó el completado de tareas puntuales para que queden:
    - marcadas como completadas
    - deshabilitadas
    - reordenadas al final de la lista
- Las tareas recurrentes se controlan mediante `TaskRecurrenceUtils`, tomando como referencia el periodo actual.
- `TaskAdapter` pasó a ordenar según el estado de completado del periodo vigente y no únicamente por un booleano persistido.

### Estado actual del bloque

La recurrencia queda funcional en estado MVP. El sistema ya soporta bloqueo por periodo actual, aunque todavía no existe un historial completo de completados mediante una entidad separada.

### Justificación técnica

Este ajuste era necesario para cerrar un exploit funcional y mantener la coherencia entre recompensa, estado visible y progreso del usuario.

---

## 8. Organización de recursos, internacionalización y theming compartido

### Implementación realizada

- Se modularizaron los recursos de texto en archivos separados:
    - `strings_core.xml`
    - `strings_home.xml`
    - `strings_navigation.xml`
    - `strings_inventory.xml`
    - `strings_auth.xml`
    - `strings_tasks.xml`
    - `strings_shop.xml`
    - `strings_gamification.xml`
- Se consolidó el soporte bilingüe en español e inglés.
- Se limpió `themes.xml` para centralizar mejor los estilos Material 3 y eliminar residuos heredados.
- Se refactorizó `DialogUtils` para apoyarse en el tema global de diálogos.
- Se sustituyó un selector roto por `app_button_primary_selector.xml`.
- Se eliminó un `values-night/themes.xml` vacío que no aportaba overrides reales.

### Problema que resolvía

El crecimiento del archivo de recursos dificultaba el mantenimiento y aumentaba el acoplamiento entre áreas funcionales. Además, la dispersión de estilos y algunos residuos heredados complicaban la estabilidad visual del proyecto.

### Justificación técnica

Esta reorganización mejora la mantenibilidad del proyecto, facilita la localización de textos, ordena el sistema visual y prepara la base para seguir ampliando la aplicación con menor fricción.

---

## 9. Validación técnica realizada

### Comprobaciones efectuadas

- Se ejecutó en varias ocasiones `.\gradlew.bat testDebugUnitTest`.
- Se ejecutó `.\gradlew.bat assembleDebug`.
- Se ejecutó `.\gradlew.bat installDebug`.
- El proyecto compila correctamente tras la integración de cambios.
- Los tests unitarios asociados a recompensas y recurrencia continúan pasando.
- Se añadieron casos de prueba para cubrir la normalización de etiquetas y dificultades acentuadas.
- La aplicación vuelve a arrancar sin crashear.
- La tienda recupera correctamente su catálogo cuando la tabla está vacía.

### Valor de esta validación

Estas comprobaciones no solo confirman el funcionamiento de nuevas features, sino también la estabilidad del sistema tras una iteración técnica especialmente delicada.

---

## 10. Resultado consolidado de la iteración

Tras esta fase, LeveLife queda en un estado significativamente más maduro:

- mayor seguridad en autenticación
- persistencia más fiable y evolutiva
- esquema de base de datos estabilizado en versión 7
- modelo de tareas enriquecido
- economía dual con EcoCoins
- lógica de recompensas desacoplada y centralizada
- recurrencia básica funcional
- creación de tareas más clara y completa
- mejor robustez ante cambios de idioma, textos y presentación
- theming global más consistente y mantenible
- `DialogUtils` más limpio y desacoplado
- validación técnica reforzada mediante pruebas unitarias

En conjunto, esta iteración no solo añadió funcionalidad, sino que elevó la calidad interna del proyecto en seguridad, consistencia, mantenibilidad y capacidad de crecimiento.

---

## 11. Preparación de la base para historial real de completados

### Implementación realizada
- Se creó la entidad `TaskCompletion.java` como soporte inicial para un historial persistido de completados.
- Se definió un modelo mínimo con:
  - `id`
  - `taskId`
  - `userId`
  - `completedAt`
- Se añadieron claves foráneas hacia las entidades de tarea y usuario para mantener coherencia relacional.
- Se prepararon índices orientados a consultas futuras por tarea y por rango temporal.
- Se creó `TaskCompletionDao.java` con operaciones base de inserción y lectura del historial.

### Problema que empieza a resolver
El enfoque MVP basado en `lastCompletedAt` permite bloquear completados por periodo, pero se queda corto para auditar historial real, calcular rachas o construir estadísticas más ricas.

### Estado real de esta iteración
Esta iteración no modifica todavía la lógica de negocio existente ni sustituye el flujo actual de recurrencia.
La nueva capa se ha preparado como base técnica compilable para su integración posterior en Room y en el caso de uso de completado.

### Próximo paso técnico
- Registrar la nueva entidad y su DAO en `AppDatabase`.
- Integrar el historial en `MainRepository`.
- Evolucionar la recurrencia desde el MVP actual hacia un modelo apoyado en completados persistidos.

---

## 12. Evolución del esquema a versión 8 para historial real de completados

### Implementación realizada
- Se integró `TaskCompletion` dentro del conjunto de entidades gestionadas por `AppDatabase`.
- Se declaró `TaskCompletionDao` como nuevo punto de acceso a la tabla de historial.
- Se elevó la versión del esquema de Room de `7` a `8`.
- Se creó la migración explícita `MIGRATION_7_8`.
- Se registró la nueva migración dentro del builder de Room mediante `.addMigrations(...)`.

### Problema que empieza a resolver
La solución MVP basada en `lastCompletedAt` permite bloquear repetición por periodo, pero no deja trazabilidad histórica real de cada completado. Sin una tabla específica, el sistema no puede evolucionar de forma sólida hacia estadísticas, rachas o auditoría temporal detallada.

### Justificación técnica
Este cambio desacopla la futura lógica de historial del modelo simplificado actual y prepara una evolución controlada del dominio sin romper la persistencia existente. Además, mantiene la política de migraciones explícitas como estrategia principal de evolución del esquema.

### Estado real tras esta iteración
- **Implementado:** infraestructura de persistencia del historial dentro de Room.
- **Validado:** pendiente de verificación práctica de compilación y migración real.
- **Pendiente:** integración del historial en la lógica de completado, recurrencia y presentación.