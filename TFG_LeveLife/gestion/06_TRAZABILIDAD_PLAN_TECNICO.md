# Trazabilidad del Plan Técnico de Evolución

Este documento detalla el estado de implementación del plan estratégico para transformar LeveLife en una plataforma de hábitos gamificada con identidad ecológica.

---

## ✅ Fase 1: Nuevo flujo del FAB (Finalizado)
**Objetivo:** Sustituir la creación simple de tareas por un formulario enriquecido.

- [x] Crear layout `bottom_sheet_create_task.xml`.
- [x] Implementar `BottomSheetDialog` en `DialogUtils.java`.
- [x] Captura de campos: Título, descripción, categoría, dificultad y frecuencia.
- [x] Modelo `TaskDraft` para transporte de datos temporales.
- [x] Integración en `TaskActivity.java`.
- [x] **Vista previa de recompensas en tiempo real** dentro del formulario.

## ✅ Fase 2: Ampliar el modelo de datos (Finalizado)
**Objetivo:** Dotar a las entidades de los atributos necesarios para la nueva lógica.

- [x] Nuevos campos en `Task.java` (`difficulty`, `taskType`, `isEcoTask`, `ecoReward`, `createdAt`, `lastCompletedAt`).
- [x] Nuevo campo en `User.java` (`ecoCoins`).
- [x] Implementación de **Migración 6 -> 7** en `AppDatabase.java`.
- [x] Soporte para `fallbackToDestructiveMigrationOnDowngrade()` para estabilidad en desarrollo.

## ✅ Fase 3: Centralización del cálculo de recompensas (Finalizado)
**Objetivo:** Desacoplar la lógica de premios de la interfaz de usuario.

- [x] Creación de `TaskRewardCalculator.java`.
- [x] Lógica de escalado: Fácil (10 XP), Medio (20 XP), Difícil (40 XP).
- [x] Bonus por tareas ecológicas (EcoCoins).
- [x] Integración en `MainRepository` y `MainViewModel`.

## 🔄 Fase 4: Hábitos recurrentes (En progreso)
**Objetivo:** Implementar la recurrencia real mediante historial, no solo booleanos.

- [x] Lógica MVP: Validación de periodo actual en `TaskRecurrenceUtils.java`.
- [ ] **Pendiente**: Crear entidad `TaskCompletion` (id, taskId, completedAt).
- [ ] **Pendiente**: Crear `TaskCompletionDao`.
- [ ] **Pendiente**: Refactorizar Repositorio para registrar completados en la nueva tabla.
- [ ] **Pendiente**: Lógica de cálculo de rachas (streaks) basada en el historial.

## 🔄 Fase 5: Reflejo en la UI (Parcialmente realizado)
**Objetivo:** Mostrar la riqueza de información de las tareas en la lista.

- [x] Actualizar `item_task.xml` para mostrar categoría, dificultad y marca eco.
- [x] Sincronización de colores (naranja para berries, verde para eco).
- [x] Header de `MainActivity` con contador de EcoCoins.
- [ ] **Pendiente**: Implementar filtros en `TaskActivity` (Todas, Eco, Hábitos, Pendientes).

## 📅 Fase 6: Mecánica ecológica real (Planificado)
**Objetivo:** Que la sostenibilidad tenga un impacto tangible en el juego.

- [x] Sembrado inicial de objetos eco en la tienda (`furn_fan_eco`).
- [ ] **Pendiente**: Lógica de compra con EcoCoins (objetos exclusivos).
- [ ] **Pendiente**: Sistema de logros verdes (Insignias).
- [ ] **Pendiente**: Visualización de "Nivel Ecológico" independiente.

## ✅ Fase 7: Tests (Finalizado)
**Objetivo:** Garantizar la fiabilidad de las nuevas reglas de negocio.

- [x] `TaskRewardCalculatorTest`: Validación de premios.
- [x] `TaskRecurrenceUtilsTest`: Validación de periodos diarios/semanales/mensuales.
- [x] Verificación de compilación y ejecución de `testDebugUnitTest`.

---

## 🛠️ Resumen de Métodos Clave Implementados
- `DialogUtils.showCreateTaskDialog(...)`
- `TaskRewardCalculator.calculateRewards(...)`
- `Task.normalizeCategory/Difficulty/Frequency(...)`
- `TaskRecurrenceUtils.isTaskAvailable(...)`
- `MainRepository.completeTask(...)` (Actualizado para moneda dual)
