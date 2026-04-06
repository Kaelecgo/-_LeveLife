# Backlog Técnico - LeveLife

## Crítico
- [ ] Validar compra transaccional de muebles.
- [ ] Validar manualmente el flujo de recurrencia apoyado en historial persistido por tarea y periodo en tareas recurrentes.
- [ ] Cerrar documento definitivo de FASE 3 para tutoría.
- [ ] Formalizar tabla completa de casos de prueba con resultado esperado y resultado real.
- [ ] Preparar evidencias visuales del flujo principal para memoria y defensa.
- [ ] Definir el comportamiento funcional final de la acción `Colocar`.
- [ ] Decidir si la colocación de muebles será persistente en esta versión del TFG.
- [ ] Probar login y registro sobre escenarios de datos migrados.
- [ ] Revalidar manualmente el reward preview al cambiar dificultad y categoría tras la corrección de normalización.
- [ ] Validar manualmente EcoCoins en tareas ecológicas dentro del flujo real de uso.

## Alto
- [ ] Crear un empty state sólido para inventario vacío.
- [ ] Revisar comportamiento con varios usuarios de prueba.
- [ ] Revisar manejo de errores y feedback visual en `LoginActivity`, `ShopActivity` y `TaskActivity`.
- [ ] Revisar la modularización final de recursos de texto en `values` y `values-en`.
- [ ] Revisar en dispositivo la consistencia visual tras la limpieza de `themes.xml` y `DialogUtils`.
- [ ] Confirmar si existen caracteres mal codificados visibles en recursos o documentación y corregirlos si afloran en runtime.

### En progreso
- [ ] Validar migración real `7 -> 8` sobre base existente.
- [ ] Probar autenticación con escenarios legacy.
- [ ] Decidir en una iteración futura cuándo retirar `lastCompletedAt` como compatibilidad visual temporal de la UI.
- [ ] Alinear la UI de recurrencia con el mismo origen de verdad persistido que ya usa el repositorio.

## Medio
- [ ] Añadir pruebas manuales de flujo real para login, sesión persistente, registro y creación de tareas.
- [ ] Documentar en memoria la validación extremo a extremo con evidencias.
- [ ] Justificar en memoria el uso de datos seed para pruebas.
- [ ] Revisar el seeder con IDs asumidos.
- [ ] Revisar datos demo heredados y su papel como entorno de pruebas controlado.
- [ ] Revisar si el alta debe mostrar un mensaje específico de auto-login exitoso.
- [ ] Mejorar la separación de responsabilidades del inventario si se amplía la acción `Colocar`.
- [ ] Limpiar usuarios demo heredados en bases antiguas si realmente existen.
- [ ] Mantener coherencia entre backlog, panel, bitácora, memoria, trazabilidad y defensa.

## Bajo
- [ ] Limpiar configuración heredada de `gradle.properties`.
- [ ] Separar `Repository` y `ViewModel` por features cuando el núcleo esté completamente estabilizado.
- [ ] Mejorar la documentación de arquitectura para la defensa.
- [ ] Preparar material de defensa asociado al refactor técnico y a la evolución del sistema de hábitos.

## Cerrado recientemente
- [x] Verificar migraciones reales entre versiones de base de datos (v7 -> v8).
- [x] Verificar migraciones reales sobre dispositivos o bases con datos previos, si se dispone de ellas.
- [x] Documentar el endurecimiento de autenticación y migraciones explícitas.
- [x] Documentar el sistema enriquecido de `Task`, `TaskRewardCalculator` y EcoCoins.
- [x] Documentar la normalización de etiquetas y el desacoplamiento entre UI y lógica interna.
- [x] Documentar la recurrencia MVP basada en periodo actual.
- [x] Limpiar el flujo visual del BottomSheet de creación de tareas.
- [x] Refactorizar `DialogUtils` para centralizar BottomSheet y diálogos Material.
- [x] Corregir el crash de `LoginActivity` por theming/inflado.
- [x] Corregir el cálculo de recompensas con dificultades acentuadas.
- [x] Integrar historial real de completados mediante `TaskCompletion`.
- [x] Registrar `TaskCompletion` dentro del esquema Room con versión 8 y migración explícita `7 -> 8`.
- [x] Exponer `TaskCompletionDao` desde `AppDatabase`.
- [x] Refactorizar `MainRepository.completeTask(...)` para registrar y consultar historial real por tarea y periodo.
- [x] Consolidar `TaskRecurrenceUtils` como utilidad temporal pura dentro del nuevo modelo de recurrencia.
- [x] Revisar `TaskAdapter` y `MainViewModel` para reducir la mezcla con la lógica antigua basada en `isCompleted`.
- [x] Endurecer la validación del formulario de tareas para exigir valores válidos.
- [x] Limpiar el reward preview para que solo reaccione a factores que alteran la recompensa.
- [x] Reescribir `strings_tasks.xml` para eliminar texto roto y mejorar legibilidad.
- [x] Aplicar hotfix de reconciliación idempotente del catálogo en bases antiguas usando `image_ref` como identificador técnico estable.
- [x] Validar integración de `MainRepository.completeTask(...)` sobre Room.
- [x] Confirmar el bloqueo de recurrencia por tarea y periodo mediante historial persistido.
- [x] Ampliar la cobertura de `TaskRecurrenceUtils` con validación de periodos diario, semanal y mensual.
- [x] Validar el cálculo de inicio de periodo con zona horaria controlada y helpers reutilizables.
- [x] Sustituir temporizadores por fila en `TaskAdapter` por un ticker compartido más seguro.
- [x] Limpiar el popup diario de reinicio automático y sus recursos asociados.
- [x] Corregir textos y problemas de codificación del bloque diario en `strings_tasks.xml`.
- [x] Ampliar `TaskRecurrenceUtilsTest` con cobertura de tiempo hasta el siguiente periodo diario y formato con prefijo de días.
- [x] Validar `testDebugUnitTest` y `clean assembleDebug` tras la mejora de UX de recurrencia.
