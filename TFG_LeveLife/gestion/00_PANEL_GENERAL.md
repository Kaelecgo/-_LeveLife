# Panel General - LeveLife

## Estado actual
- Arquitectura MVVM con Room, Repository, ViewModel y LiveData operativa.
- Autenticación local reforzada con contraseñas protegidas mediante hash PBKDF2.
- Recuperación de sesión estabilizada desde `SplashActivity`.
- Base de datos Room alineada con migraciones explícitas hasta la versión 7.
- Sistema de tareas enriquecido con dificultad, frecuencia, recompensa ecológica y control temporal básico.
- Economía dual operativa con Berries y EcoCoins.
- Cálculo de recompensas desacoplado en `TaskRewardCalculator`.
- Recurrencia MVP implementada mediante `TaskRecurrenceUtils`.
- Creación de tareas mediante BottomSheet con vista previa de recompensa.
- Theming global unificado con Material 3 y estilos compartidos `LeveLife.*`.
- `DialogUtils` refactorizado para centralizar BottomSheet y diálogos de confirmación.
- Recursos de texto modularizados por dominio en español e inglés.
- Normalización de etiquetas endurecida para soportar traducciones, emojis y diacríticos.

## Bloque actual
Estabilización final del núcleo técnico, cierre documental de FASE 3 y preparación del paquete de evidencias para tutoría y defensa.

## Avances recientes
- Se corrigió el crash de `LoginActivity` provocado por la inflación del layout y la mezcla entre parámetros de layout y estilos.
- Se limpió `themes.xml`, manteniendo una jerarquía de estilos compatible con la herencia implícita de Android (`LeveLife`, `LeveLife.Text`, `LeveLife.Card`).
- Se centralizó el estilo de diálogos con `MaterialAlertDialogBuilder` y tema global de aplicación.
- Se refactorizó `DialogUtils` para separar mejor orquestación, binding de dropdowns y renderizado del reward preview.
- Se renombró el flujo de creación a `showCreateTaskBottomSheet(...)`, alineándolo con el comportamiento real.
- Se sustituyó un selector de color roto por `app_button_primary_selector.xml`.
- Se eliminó un `values-night/themes.xml` vacío que no aportaba overrides reales.
- Se limpió `bottom_sheet_create_task.xml` para usar `dimens`, colores semánticos y estilos compartidos.
- Se amplió la normalización de `Task` para eliminar diacríticos y corregir el cálculo de recompensas cuando la dificultad llega como `Fácil` o `Difícil`.
- Se añadió una prueba unitaria específica para esa normalización con tildes.
- Se consolidó la modularización de recursos de texto en `strings_core`, `strings_home`, `strings_navigation`, `strings_inventory`, `strings_auth`, `strings_tasks`, `strings_shop` y `strings_gamification`.

## Validaciones realizadas
- `.\gradlew.bat assembleDebug` correcto.
- `.\gradlew.bat installDebug` correcto.
- `.\gradlew.bat testDebugUnitTest` correcto.
- Arranque estable de la app tras la limpieza del tema.
- `LoginActivity` deja de crashear al inflar su layout.
- Cálculo de recompensas correcto para dificultad acentuada (`Difícil`).
- Recompensas ecológicas y economía dual cubiertas por pruebas unitarias.
- BottomSheet de tareas compilando y enlazado con `TaskActivity`.

## Próxima sesión
Completar validación manual de extremo a extremo del flujo de tareas, cerrar la tabla de casos de prueba, preparar evidencias visuales y decidir el alcance final de la acción `Colocar`.

## Pendientes mayores
- Formalizar la tabla completa de casos de prueba con resultado esperado y real.
- Preparar evidencias visuales reutilizables para memoria y defensa.
- Definir el comportamiento funcional definitivo de `Colocar` en inventario/habitación.
- Decidir si la colocación persistente entra en esta versión o queda como ampliación futura.
- Revalidar manualmente reward preview, EcoCoins y recurrencia MVP tras las últimas correcciones.
- Mantener coherencia entre panel, bitácora, backlog, memoria y trazabilidad.
