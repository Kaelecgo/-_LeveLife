# Backlog Técnico - LeveLife

## Crítico
- [ ] Cerrar documento definitivo de FASE 3 para tutoría.
- [ ] Formalizar tabla completa de casos de prueba con resultado esperado y resultado real.
- [ ] Preparar evidencias visuales del flujo principal para memoria y defensa.
- [ ] Definir el comportamiento funcional final de la acción `Colocar`.
- [ ] Decidir si la colocación de muebles será persistente en esta versión del TFG.
- [ ] Probar login y registro sobre escenarios de datos migrados.
- [ ] Revalidar manualmente el reward preview al cambiar dificultad y categoría tras la corrección de normalización.
- [ ] Validar manualmente EcoCoins en tareas ecológicas dentro del flujo real de uso.
- [ ] Validar manualmente la recurrencia básica por periodo actual en tareas recurrentes.

## Alto
- [ ] Crear un empty state sólido para inventario vacío.
- [ ] Revisar comportamiento con varios usuarios de prueba.
- [ ] Revisar manejo de errores y feedback visual en `LoginActivity`, `ShopActivity` y `TaskActivity`.
- [ ] Verificar migraciones reales sobre dispositivos o bases con datos previos, si se dispone de ellas.
- [ ] Revisar la modularización final de recursos de texto en `values` y `values-en`.
- [ ] Revisar en dispositivo la consistencia visual tras la limpieza de `themes.xml` y `DialogUtils`.
- [ ] Confirmar si existen caracteres mal codificados visibles en recursos o documentación y corregirlos si afloran en runtime.

## Medio
## En progreso
- [~] Integrar historial real de completados mediante `TaskCompletion`.
- [~] Registrar `TaskCompletion` dentro del esquema Room con versión 8 y migración explícita `7 -> 8`.
- [~] Exponer `TaskCompletionDao` desde `AppDatabase`.
- [ ] Refactorizar `MainRepository` para registrar completados en historial real.
- [ ] Adaptar `TaskRecurrenceUtils` para apoyarse progresivamente en historial persistido.
- [ ] Revisar impacto en `TaskAdapter` y `MainViewModel`.
- [ ] Validar migración real `7 -> 8` sobre base existente.
- [~] Preparar historial real de completados con nueva entidad `TaskCompletion`.
- [~] Preparar `TaskCompletionDao` con consultas base por tarea y por periodo.
- [ ] Registrar `TaskCompletion` y `TaskCompletionDao` en `AppDatabase`.
- [ ] Refactorizar `MainRepository` para registrar completados en historial real.
- [ ] Adaptar `TaskRecurrenceUtils` para apoyarse progresivamente en historial persistido.
- [ ] Revisar impacto en `TaskAdapter` y `MainViewModel`.
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
- [x] Documentar el endurecimiento de autenticación y migraciones explícitas.
- [x] Documentar el sistema enriquecido de `Task`, `TaskRewardCalculator` y EcoCoins.
- [x] Documentar la normalización de etiquetas y el desacoplamiento entre UI y lógica interna.
- [x] Documentar la recurrencia MVP basada en periodo actual.
- [x] Limpiar el flujo visual del BottomSheet de creación de tareas.
- [x] Refactorizar `DialogUtils` para centralizar BottomSheet y diálogos Material.
- [x] Corregir el crash de `LoginActivity` por theming/inflado.
- [x] Corregir el cálculo de recompensas con dificultades acentuadas.
