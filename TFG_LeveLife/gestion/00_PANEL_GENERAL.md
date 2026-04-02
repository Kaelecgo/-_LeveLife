# Panel General - LeveLife

## Estado actual
- Arquitectura MVVM con Room, Repository, ViewModel y LiveData operativa.
- Autenticación local reforzada con contraseñas protegidas mediante hash PBKDF2.
- Recuperación de sesión estabilizada desde `SplashActivity`.
- Base de datos Room alineada con migraciones explícitas hasta la versión 8.
- Sistema de tareas enriquecido con dificultad, frecuencia, recompensa ecológica e historial real de completados por tarea y periodo.
- Economía dual operativa con Berries y EcoCoins.
- Cálculo de recompensas desacoplado en `TaskRewardCalculator`.
- Recurrencia real apoyada en historial persistido por tarea y periodo mediante `task_completions`.
- `TaskRecurrenceUtils` y `lastCompletedAt` se mantienen de forma temporal como apoyo de compatibilidad visual y utilidades auxiliares.
- Creación de tareas mediante BottomSheet con vista previa de recompensa.
- Theming global unificado con Material 3 y estilos compartidos `LeveLife.*`.
- `DialogUtils` refactorizado para centralizar BottomSheet y diálogos de confirmación.
- Recursos de texto modularizados por dominio en español e inglés.
- Normalización de etiquetas endurecida para soportar traducciones, emojis y diacríticos.

## Bloque actual
Cierre técnico del bloque de historial real de completados, estabilización documental del estado actual y preparación del paquete de evidencias para tutoría y defensa.

## Avances recientes
- Se estabilizó el historial real de completados mediante `TaskCompletion`, `TaskCompletionDao` y migración Room `7 -> 8`.
- `MainRepository.completeTask(...)` pasó a validar recurrencia por tarea y periodo usando historial persistido.
- Se eliminó la validación heredada basada en `task.isCompleted()` desde `MainViewModel`, devolviendo el bloqueo real al repositorio.
- `TaskAdapter` corrigió el estado visual de tareas recurrentes, aplicando y limpiando correctamente el tachado según disponibilidad real.
- `TaskDao` dejó de ordenar por `isCompleted`, al no describir correctamente el estado de hábitos recurrentes.
- Se endureció la validación del formulario de tareas para exigir categoría, dificultad y frecuencia válidas.
- Se limpió el `reward preview` para que solo reaccione a factores que alteran realmente la recompensa.
- Se reescribió `strings_tasks.xml` para eliminar texto roto y mejorar legibilidad.
- Se aplicó un hotfix de reparación idempotente del catálogo en bases antiguas mediante detección de elementos faltantes por `image_ref`.
- Se consolidó la limpieza visual y estructural de `themes.xml`, `DialogUtils` y los recursos de texto.

## Validaciones realizadas
- `.\gradlew.bat assembleDebug` correcto.
- `.\gradlew.bat installDebug` correcto.
- `.\gradlew.bat testDebugUnitTest` correcto.
- Arranque estable de la app tras la limpieza del tema.
- `LoginActivity` deja de crashear al inflar su layout.
- Cálculo de recompensas correcto para dificultad acentuada (`Difícil`).
- Recompensas ecológicas, normalización y recurrencia cubiertas por pruebas unitarias.
- BottomSheet de tareas compilando y enlazado con `TaskActivity`.


## Próxima sesión
Cerrar la revisión documental del bloque actual, completar la validación manual de extremo a extremo del flujo de tareas y decidir cuándo retirar la compatibilidad visual temporal basada en `lastCompletedAt`.

## Pendientes mayores
- Formalizar la tabla completa de casos de prueba con resultado esperado y real.
- Preparar evidencias visuales reutilizables para memoria y defensa.
- Definir el comportamiento funcional definitivo de `Colocar` en inventario/habitación.
- Decidir si la colocación persistente entra en esta versión o queda como ampliación futura.
- Revalidar manualmente reward preview, EcoCoins y flujo de recurrencia apoyado en historial persistido tras las últimas correcciones.
- Decidir en una iteración futura cuándo retirar `lastCompletedAt` como caché visual temporal de la UI.
- Mantener coherencia entre panel, bitácora, backlog, memoria y trazabilidad.
