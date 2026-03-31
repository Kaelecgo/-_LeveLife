# Trazabilidad del Plan Técnico de Evolución

Este documento relaciona el plan técnico planteado para LeveLife con su estado real de implementación a fecha 31/03/2026.

---

## ✅ Fase 1: Nuevo flujo del FAB (Finalizado)
**Objetivo:** Sustituir la creación simple de tareas por un formulario enriquecido.

- [x] Crear `bottom_sheet_create_task.xml`.
- [x] Implementar BottomSheet en `DialogUtils`.
- [x] Captura de título, descripción, categoría, dificultad y frecuencia.
- [x] Modelo `TaskDraft` para transportar el estado temporal.
- [x] Integración en `TaskActivity.java`.
- [x] Vista previa de recompensas en tiempo real.
- [x] Refactor final del flujo a `DialogUtils.showCreateTaskBottomSheet(...)`.

## ✅ Fase 2: Ampliar el modelo de datos (Finalizado)
**Objetivo:** Dotar a las entidades de los atributos necesarios para la nueva lógica.

- [x] Nuevos campos en `Task.java`: `difficulty`, `frequency`, `ecoReward`, `isEcoTask`, `lastCompletedAt`.
- [x] Nuevo campo `ecoCoins` en `User.java`.
- [x] Migración Room `6 -> 7` implementada en `AppDatabase.java`.
- [x] Alineación del esquema real con los nuevos campos del sistema enriquecido.

## ✅ Fase 3: Centralización del cálculo de recompensas (Finalizado)
**Objetivo:** Desacoplar la lógica de premios de la interfaz.

- [x] Crear `TaskRewardCalculator.java`.
- [x] Escalado de recompensas por dificultad.
- [x] Bonus ecológico con EcoCoins.
- [x] Integración en creación y completado de tareas.
- [x] Corrección del cálculo ante valores acentuados (`Fácil`, `Difícil`) mediante normalización reforzada.

## 🔄 Fase 4: Hábitos recurrentes (En progreso)
**Objetivo:** Implementar recurrencia real mediante historial, no solo booleanos.

- [x] Lógica MVP de periodo actual en `TaskRecurrenceUtils.java`.
- [x] Integración inicial de `lastCompletedAt` en el flujo de completado.
- [x] Crear entidad `TaskCompletion`.
- [x] Crear `TaskCompletionDao`.
- [x] Integrar `TaskCompletion` en `AppDatabase.java`.
- [x] Exponer `TaskCompletionDao` desde `AppDatabase.java`.
- [x] Crear migración Room `7 -> 8` para soportar la nueva tabla de historial.
- [x] Refactorizar `MainRepository.completeTask(...)` para registrar historial real de completados.
- [x] Validar el bloqueo de recurrencia por instancia de tarea y periodo.
- [x] Consolidar que tareas con el mismo título siguen siendo entidades independientes.
- [~] Mantener `lastCompletedAt` como compatibilidad visual temporal en la UI actual.
- [~] Mantener `TaskRecurrenceUtils` como utilidad temporal pura mientras convive la compatibilidad visual actual.
- [ ] Revisar impacto final en `TaskAdapter` y `MainViewModel`.
- [ ] Retirar `lastCompletedAt` cuando deje de ser necesario para compatibilidad visual.
- [ ] Implementar rachas y estadísticas basadas en historial real.

**Nota de estado:** la recurrencia real ya se apoya en historial persistido por tarea mediante `task_completions`. `TaskRecurrenceUtils` queda consolidada como utilidad temporal pura y la UI sigue usando `lastCompletedAt` como compatibilidad visual temporal.

## 🔄 Fase 5: Reflejo en la UI (Parcialmente realizado)
**Objetivo:** Mostrar la riqueza de información de las tareas y estabilizar la experiencia visual.

- [x] `item_task.xml` actualizado para mostrar metadatos de tarea.
- [x] Cabecera de `MainActivity` con EcoCoins.
- [x] BottomSheet de creación alineado con estilos globales.
- [x] Limpieza de `themes.xml` y eliminación de hardcodes visuales en el formulario.
- [x] Estabilización del login tras corregir el crash de inflado.
- [ ] Implementar filtros en `TaskActivity` (Todas, Eco, Hábitos, Pendientes).
- [ ] Revisar empty states y feedback visual final en inventario y tienda.

## 📝 Fase 6: Mecánica ecológica real (Planificado)
**Objetivo:** Que la sostenibilidad tenga un impacto tangible en el juego.

- [x] Economía dual con EcoCoins incorporada al dominio.
- [x] Recompensa ecológica asociada a tareas sostenibles.
- [x] Elementos eco ya presentes en el catálogo.
- [ ] Compra con EcoCoins de objetos exclusivos.
- [ ] Logros o hitos ecológicos.
- [ ] Progreso o nivel ecológico independiente.

## ✅ Fase 7: Tests (Finalizado)
**Objetivo:** Garantizar la fiabilidad de las nuevas reglas de negocio.

- [x] `TaskRewardCalculatorTest`.
- [x] `TaskRecurrenceUtilsTest`.
- [x] Caso de prueba específico para dificultad acentuada.
- [x] Validación repetida de `testDebugUnitTest`.

## 🔧 Trabajo transversal de estabilización (Realizado parcialmente)
**Objetivo:** Reducir deuda técnica visual y mejorar mantenibilidad.

- [x] Centralización de theming con Material 3.
- [x] Refactor de `DialogUtils`.
- [x] Modularización de recursos de texto por dominio.
- [x] Sustitución de selector roto por `app_button_primary_selector.xml`.
- [ ] Revisión final de recursos y posibles caracteres mal codificados.

---

## 🛠️ Resumen de métodos clave implementados
- `DialogUtils.showCreateTaskBottomSheet(...)`
- `TaskRewardCalculator.calculateRewards(...)`
- `Task.normalizeCategory(...)`
- `Task.normalizeDifficulty(...)`
- `Task.normalizeFrequency(...)`
- `TaskRecurrenceUtils.isCompletedForCurrentPeriod(...)`
- `MainRepository.completeTask(...)`
- `TaskCompletionDao.insert(...)`
