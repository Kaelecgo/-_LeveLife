# Decisiones Técnicas - LeveLife

## DT-01 - Seguridad local mediante hash PBKDF2
- Estado: Adoptada.
- Contexto: El sistema original almacenaba credenciales en texto plano.
- Decisión: Sustituir el almacenamiento directo por hashes PBKDF2 y mover la verificación de credenciales al dominio.
- Consecuencia: Mejora la seguridad del proyecto y hace defendible la autenticación en memoria y TFG.

## DT-02 - Migraciones explícitas de Room como estrategia principal
- Estado: Adoptada y ampliada.
- Contexto: `fallbackToDestructiveMigration()` comprometía el progreso del usuario y dificultaba una evolución segura del esquema.
- Decisión: Evolucionar la base de datos mediante migraciones explícitas de Room hasta la versión 8, incorporando también la tabla `task_completions` y sus índices como parte del modelo persistente.
- Consecuencia: Se preserva el progreso del usuario en cambios de esquema, se facilita la evolución controlada del dominio y el downgrade destructivo solo se tolera como salvaguarda de desarrollo.

## DT-03 - Creación de tareas mediante BottomSheet y modelo intermedio `TaskDraft`
- Estado: Adoptada.
- Contexto: El flujo antiguo de creación era demasiado simple para el nuevo modelo de hábitos.
- Decisión: Implementar un BottomSheet con campos enriquecidos y transportar el estado temporal mediante `TaskDraft`.
- Consecuencia: La UI queda desacoplada del modelo persistente y el flujo de creación gana claridad y extensibilidad.

## DT-04 - Lógica de recompensas centralizada en `TaskRewardCalculator`
- Estado: Adoptada.
- Contexto: El cálculo de XP, Berries y EcoCoins no debía dispersarse por Activities o adapters.
- Decisión: Concentrar el cálculo en una utilidad de dominio reutilizable.
- Consecuencia: Se mejora la consistencia, la testabilidad y la trazabilidad de la economía del juego.

## DT-05 - Normalización interna de etiquetas en `Task`
- Estado: Adoptada y ampliada.
- Contexto: Se detectaron errores por depender de textos visibles de UI, traducciones, emojis y tildes.
- Decisión: Normalizar categoría, dificultad y frecuencia dentro del modelo, eliminando símbolos, ruido visual y diacríticos antes de comparar.
- Consecuencia: El sistema deja de depender del texto exacto mostrado al usuario y gana robustez frente a internacionalización y cambios de interfaz.

## DT-06 - Recurrencia MVP por periodo actual
- Estado: Adoptada como solución intermedia y posteriormente superada de forma parcial.
- Contexto: En una fase anterior del proyecto, implementar una entidad `TaskCompletion` completa era más costoso que el alcance inmediato de la versión.
- Decisión: Resolver inicialmente la recurrencia básica con `lastCompletedAt` y comprobación del periodo actual.
- Consecuencia: La app pudo soportar hábitos diarios, semanales y mensuales en una fase MVP, sirviendo como puente técnico hasta la incorporación posterior de historial persistido real por tarea y periodo.

## DT-06B - Transición de la recurrencia MVP a historial persistido por tarea y periodo
- Estado: Adoptada.
- Contexto: El enfoque inicial basado en `lastCompletedAt` resolvía el bloqueo básico por periodo, pero mezclaba demasiado lógica heredada, compatibilidad visual y persistencia simplificada.
- Decisión: Desplazar la base real de la recurrencia hacia un historial persistido en `task_completions`, haciendo que `MainRepository.completeTask(...)` consulte completados previos por tarea y periodo antes de permitir nuevas recompensas.
- Consecuencia: La recurrencia deja de depender principalmente de señales débiles como `isCompleted` o de una única marca temporal simplificada. `TaskRecurrenceUtils` se mantiene como utilidad temporal pura para cálculo de periodos y `lastCompletedAt` permanece solo como compatibilidad visual transitoria en la UI actual.

## DT-07 - Theming centralizado con Material 3 y estilos `LeveLife.*`
- Estado: Adoptada y refinada.
- Contexto: Había inconsistencias visuales, estilos mezclados con parámetros de layout y un crash al inflar `LoginActivity`.
- Decisión: Centralizar estilos visuales en `themes.xml`, mantener la jerarquía `LeveLife`, `LeveLife.Text` y `LeveLife.Card` para respetar la herencia implícita de Android, y mover parámetros de layout al XML de cada pantalla.
- Consecuencia: El tema queda más coherente, el inflado es más estable y la interfaz resulta más mantenible.

## DT-08 - Diálogos Material controlados desde tema global
- Estado: Adoptada.
- Contexto: Había mezcla entre estilo pasado manualmente y configuración global del tema.
- Decisión: Usar `MaterialAlertDialogBuilder` apoyándose en `materialAlertDialogTheme` definido en el tema de aplicación.
- Consecuencia: Menos acoplamiento visual dentro de `DialogUtils` y mayor consistencia entre confirmaciones y avisos.

## DT-09 - Modularización de recursos de texto por dominio funcional
- Estado: Adoptada.
- Contexto: El crecimiento de `strings.xml` dificultaba mantenimiento, revisión y traducción.
- Decisión: Separar recursos en `strings_core`, `strings_home`, `strings_navigation`, `strings_inventory`, `strings_auth`, `strings_tasks`, `strings_shop` y `strings_gamification`, manteniendo paridad en `values` y `values-en`.
- Consecuencia: El mantenimiento e internacionalización resultan más escalables y más fáciles de documentar.

## DT-10 - Reconciliación idempotente del catálogo de tienda
- Estado: Adoptada.
- Contexto: Al actualizar la app, los nuevos ítems de la tienda no se insertaban si ya existían registros previos en la base de datos heredada.
- Decisión: Sustituir la comprobación global por un algoritmo de reconciliación basado en caché de memoria (`HashSet`) e identificadores inmutables (`image_ref`), agrupado en una única transacción atómica en `onOpen`.
- Consecuencia: Se garantiza que el catálogo siempre esté completo e íntegro sin importar la versión de origen, se minimiza el I/O del disco y se protege el sistema contra inserciones duplicadas ante futuros cambios de idioma (i18n).

## DT-11 - Nombramiento explícito de índices en Room
- Estado: Adoptada.
- Contexto: Riesgo de crash (`IllegalStateException`) si Room autogeneraba nombres de índices distintos a los de la migración manual en SQL.
- Decisión: Forzar el atributo `name` en las anotaciones `@Index` de las entidades (ej. `TaskCompletion`) para obligar a que coincidan exactamente con la migración.
- Consecuencia: Migraciones de esquema 100% estables y sin falsos positivos en la validación de integridad de Room.

## DT-12 - Validación de formularios basada en valores de dominio válidos
- Estado: Adoptada.
- Contexto: La validación basada únicamente en comprobar que un texto no estuviera vacío permitía combinaciones inválidas en categoría, dificultad y frecuencia.
- Decisión: Endurecer la validación del formulario para exigir valores válidos de dominio en lugar de aceptar cualquier texto no vacío.
- Consecuencia: Se reduce la entrada de estados inconsistentes desde la UI, se protege mejor la lógica de recompensas y recurrencia, y el flujo de creación queda más coherente con el modelo interno.

## DT-13 - Estrategia de Autoreparación de Datos (Self-healing) en `onOpen`
- Estado: Adoptada.
- Contexto: Durante la evolución del esquema (v6 -> v8), algunos registros de tareas quedaron con frecuencias nulas o valores heredados ("Normal") que rompían la nueva lógica de recurrencia y recompensas.
- Decisión: Implementar `repairLegacyTaskFrequencies` en el callback `onOpen` de Room para detectar y corregir proactivamente registros inconsistentes en cada arranque de la aplicación.
- Consecuencia: Se garantiza la estabilidad del sistema sin recurrir a migraciones destructivas, mejorando la experiencia de usuario y la robustez del modelo de datos frente a cambios de versiones.
