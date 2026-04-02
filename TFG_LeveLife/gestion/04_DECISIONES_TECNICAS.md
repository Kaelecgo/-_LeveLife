# Decisiones Técnicas - LeveLife

## DT-01 - Seguridad local mediante hash PBKDF2
- Estado: Adoptada.
- Contexto: El sistema original almacenaba credenciales en texto plano.
- Decisión: Sustituir el almacenamiento directo por hashes PBKDF2 y mover la verificación de credenciales al dominio.
- Consecuencia: Mejora la seguridad del proyecto y hace defendible la autenticación en memoria y TFG.

## DT-02 - Migraciones explícitas de Room como estrategia principal
- Estado: Adoptada.
- Contexto: `fallbackToDestructiveMigration()` comprometía el progreso del usuario.
- Decisión: Evolucionar la base de datos mediante migraciones explícitas hasta la versión 7.
- Consecuencia: Se preserva el progreso del usuario en cambios de esquema; el downgrade destructivo solo se tolera como salvaguarda de desarrollo.

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
- Estado: Adoptada como solución intermedia.
- Contexto: Implementar una entidad `TaskCompletion` completa era más costoso que el alcance inmediato de la versión.
- Decisión: Resolver la recurrencia básica con `lastCompletedAt` y comprobación del periodo actual.
- Consecuencia: La app soporta hábitos diarios, semanales y mensuales en modo MVP, pero las rachas e historial detallado quedan como trabajo futuro.

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
