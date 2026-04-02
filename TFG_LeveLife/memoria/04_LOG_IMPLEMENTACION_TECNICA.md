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

- Se detectó un bug de catálogo parcial en bases antiguas.
- La lógica previa solo repoblaba el catálogo si la tabla `furniture` estaba completamente vacía.
- Se sustituyó por una reparación idempotente en `onOpen`, capaz de insertar únicamente los elementos faltantes.
- Se adoptó `image_ref` como identificador técnico estable del catálogo para detectar ausencias sin duplicar los elementos ya existentes.

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

## 7. Transición de la recurrencia MVP a historial persistido

### Implementación realizada

- Se sustituyó la base lógica de recurrencia apoyada únicamente en `lastCompletedAt` por un historial persistido en `task_completions`.
- `MainRepository.completeTask(...)` pasó a consultar el historial por tarea y periodo antes de permitir un nuevo completado.
- El bloqueo deja de depender de comprobaciones heredadas en capas superiores y vuelve a decidirse en el repositorio.
- Se mantuvo `TaskRecurrenceUtils` como utilidad temporal pura para cálculo de periodos y compatibilidad transitoria.
- Se amplió la cobertura de `TaskRecurrenceUtils`.
- Se validaron periodos diario, semanal y mensual.
- Se validó el cálculo de inicio de periodo.
- Se mantuvo compatibilidad con frecuencias normalizadas desde textos de UI.
- Las tareas con el mismo título siguen tratándose como entidades independientes.
- `lastCompletedAt` permanece temporalmente como compatibilidad visual para la UI actual.

### Problema que resolvía

El enfoque MVP inicial permitía bloquear tareas recurrentes por periodo, pero mezclaba demasiado lógica heredada, estado visual y persistencia simplificada. Eso dificultaba evolucionar la recurrencia de forma coherente y dejaba demasiado acoplamiento entre modelo viejo y flujo nuevo.

### Estado actual del bloque

La recurrencia real ya se apoya en historial persistido por tarea y periodo. `TaskRecurrenceUtils` queda consolidada como utilidad temporal pura y `lastCompletedAt` se mantiene únicamente como compatibilidad visual transitoria.

### Justificación técnica

Este refactor reduce la mezcla entre el modelo antiguo basado en `isCompleted` y el sistema nuevo de historial real de completados. Con ello, el bloqueo de recurrencia pasa a estar soportado por datos persistidos y no por señales débiles o estados visuales heredados.

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
- La reparación idempotente del catálogo detecta elementos faltantes en bases antiguas sin depender de una tabla `furniture` completamente vacía.
- Se amplió la cobertura de pruebas sobre `TaskRecurrenceUtils`.
- Se validaron periodos diario, semanal y mensual.
- Se validó el cálculo de inicio de periodo.
- `.\gradlew.bat testDebugUnitTest` sigue pasando tras el cierre de `dev/feature-task-completion-history`.
- `.\gradlew.bat assembleDebug` sigue pasando tras la limpieza final de validaciones heredadas, DAO, adapter y formulario.

### Valor de esta validación

Estas comprobaciones no solo confirman el funcionamiento de nuevas features, sino también la estabilidad del sistema tras una iteración técnica especialmente delicada.

---

## 10. Resultado consolidado de la iteración

Tras el cierre de este bloque, LeveLife queda en un estado significativamente más coherente a nivel técnico:

- mayor seguridad en autenticación
- persistencia más fiable y evolutiva
- esquema de base de datos extendido hasta la versión 8
- historial real de completados integrado en la base lógica de recurrencia
- modelo de tareas enriquecido y más robusto
- economía dual con EcoCoins
- lógica de recompensas desacoplada y centralizada
- formulario de creación endurecido con validación de valores válidos
- reward preview más limpio y mejor acotado
- mejor separación entre estado persistido y compatibilidad visual temporal
- theming global más consistente y mantenible
- recursos de texto más legibles y ordenados
- validación técnica reforzada mediante pruebas unitarias y compilación correcta

En conjunto, esta iteración no solo añadió infraestructura y refactors puntuales, sino que cerró de forma suficientemente limpia la transición desde la recurrencia MVP hacia un modelo apoyado en historial persistido, dejando pendiente únicamente la retirada futura de `lastCompletedAt` como compatibilidad visual temporal.

---

## 11. Cierre técnico del bloque `dev/feature-task-completion-history`

### Implementación realizada
- Se eliminó en `MainViewModel` la validación heredada basada en `task.isCompleted()`, devolviendo el bloqueo real de recurrencia al repositorio.
- Se corrigió en `TaskAdapter` el estado visual de las tareas recurrentes, aplicando tachado cuando corresponde y limpiándolo cuando la tarea vuelve a estar disponible.
- Se eliminó en `TaskDao` la ordenación por `isCompleted`, al no describir correctamente el comportamiento real de hábitos recurrentes.
- Se simplificó `TaskCompletionDao` para conservar solo los métodos realmente utilizados por el proyecto en esta iteración.
- Se endureció la validación del formulario en `Task.java` y `DialogUtils.java`, exigiendo categoría, dificultad y frecuencia válidas.
- Se limpió el reward preview para que solo reaccione a factores que afectan realmente a la recompensa.
- Se reescribió `strings_tasks.xml` para eliminar texto roto y mejorar legibilidad.

### Validación realizada
- `.\gradlew.bat testDebugUnitTest` correcto.
- `.\gradlew.bat assembleDebug` correcto.

### Estado actual
El flujo de completado deja de mezclar tanto la lógica heredada de `isCompleted` con el modelo nuevo apoyado en historial persistido. El bloque queda técnicamente más coherente y suficientemente limpio para considerarse cerrado dentro del alcance actual.

### Límite actual
La retirada completa de `lastCompletedAt` como compatibilidad visual de la UI queda fuera de este cierre y pasa a considerarse mejora futura, no bug urgente del bloque actual.

---

## 12. Reconciliación de datos y estabilización de migraciones

### Implementación realizada
- Se rediseñó la población inicial del catálogo de recompensas (`seedFurnitureCatalog`).
- Se implementó una comprobación en memoria (O(1)) frente a los identificadores de imagen (`image_ref`) existentes.
- Se forzó el nombramiento explícito de índices de base de datos en la entidad `TaskCompletion`.

### Problema que resolvía
Las bases de datos heredadas (Legacy) no recibían las actualizaciones del catálogo mercantil tras una migración de esquema. Asimismo, existía un alto riesgo de fallo de validación interno de Room por discrepancias en la nomenclatura de los índices autogenerados frente a los manuales.

### Justificación técnica
La reparación idempotente del catálogo reduce riesgos sobre bases antiguas o incompletas, evita duplicados innecesarios y mejora la resiliencia del proceso de apertura de la base de datos. Además, el nombramiento explícito de índices garantiza una validación estable del esquema físico de Room durante la migración a la versión 8.