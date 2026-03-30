# Panel General - LeveLife

## Estado actual
- Arquitectura MVVM con Room, Repository, ViewModel y LiveData operativa.
- Autenticación local reforzada con contraseñas protegidas mediante hash PBKDF2.
- Preservación de sesión implementada y recuperación correcta desde SplashActivity.
- Base de datos estabilizada con migraciones explícitas de Room hasta la versión 7.
- Gestión de tareas e interfaz principal funcionales.
- El sistema ha evolucionado de tareas básicas a un modelo de hábitos enriquecido.
- Task soporta dificultad, frecuencia, recompensa ecológica y control temporal básico.
- Se ha introducido una economía dual con Berries y EcoCoins.
- La lógica de recompensas está centralizada en TaskRewardCalculator.
- La recurrencia básica se gestiona mediante TaskRecurrenceUtils.
- La creación de tareas se realiza mediante BottomSheetDialog con vista previa de recompensas en tiempo real.
- Tienda operativa con compra validada de forma atómica en repositorio.
- Inventario persistente y conectado a base de datos.
- Modularización de recursos de texto e internacionalización en progreso consolidado.
- Se ha reforzado la robustez desacoplando lógica interna y textos visibles de la UI mediante normalización de etiquetas.

## Bloque actual
Cierre del núcleo técnico enriquecido, validación funcional del nuevo sistema de hábitos y consolidación documental de FASE 3 con criterio realista de transición hacia FASE 4.

## Avances recientes
- La compra de muebles se ejecuta dentro de una transacción del repositorio.
- Se valida dentro de la transacción si el usuario ya posee el mueble.
- Se valida saldo suficiente antes de descontar bayas.
- Se evita la deducción local de bayas desde el ViewModel.
- La finalización de tareas se ejecuta de forma atómica en el repositorio.
- Se centralizan mensajes de error y recompensa en el ViewModel mediante LiveData.
- Se restauró el feedback visual de recompensa al completar tareas mediante un evento observable.
- Se reforzó la autenticación sustituyendo la validación en texto plano por contraseñas protegidas mediante hash.
- Se mejoró el flujo de registro para hacerlo más fiable y coherente con el acceso automático del usuario tras el alta.
- Se corrigió la recuperación de sesión desde SplashActivity unificando la clave persistida del usuario activo.
- Se eliminó la dependencia de fallbackToDestructiveMigration() como estrategia principal y se sustituyó por migraciones explícitas de Room.
- Se completó la migración 6 -> 7 añadiendo correctamente los nuevos campos del sistema enriquecido.
- Se recuperó sobre master el sistema avanzado de creación de tareas mediante BottomSheet.
- Se integraron EcoCoins en el modelo de usuario, en la interfaz principal y en el flujo de recompensas.
- Se centralizó el cálculo de premios en TaskRewardCalculator.
- Se integró recurrencia básica por periodo actual mediante TaskRecurrenceUtils.
- Se reforzó la normalización de categorías, dificultad y frecuencia para evitar errores causados por traducciones, emojis o textos visibles de UI.
- Se corrigió el exploit por el que una tarea podía recompensar sin quedar correctamente marcada.
- Se ajustó el orden visual de tareas según estado completado en el periodo actual.
- La tienda vuelve a repoblar el catálogo automáticamente si la tabla furniture está vacía.

## Validaciones realizadas
- Completar tarea normal: correcto ✅
- Completar tarea ya completada: no reaplica recompensa ✅
- Comprar mueble con saldo: correcto ✅
- Comprar mueble sin saldo: bloqueado desde UI ✅
- Comprar mueble repetido: bloqueado desde UI con estado Purchased ✅
- Persistencia de saldo tras reinicio: correcta ✅
- Persistencia de inventario tras reinicio: correcta ✅
- Subida de nivel con animación y feedback visual: correcta ✅
- Flujo de usuario nuevo desde registro hasta primera compra: correcto ✅
- Recuperación de sesión desde SplashActivity: correcta ✅
- Toast de recompensa al completar tarea: restaurado y funcional ✅
- Registro reforzado, auto-login y autenticación con hash: correctos ✅
- Reward preview del formulario enriquecido: funcional ✅
- Recompensas dinámicas según dificultad: correctas ✅
- EcoCoins aplicadas correctamente en tareas ecológicas ✅
- Recurrencia básica con bloqueo por periodo: funcional ✅
- Tests unitarios de recompensas y recurrencia: correctos ✅
- Arranque estable tras alinear esquema y migración 6 -> 7: correcto ✅
- Repoblado automático del catálogo de tienda si está vacío: correcto ✅

## Próxima sesión
Formalizar la tabla de casos de prueba, preparar evidencias visuales del flujo principal, cerrar documentalmente FASE 3 y decidir el alcance final real de la acción "Colocar" antes de fijar qué parte de FASE 4 entra en la versión final del TFG.

## Pendientes mayores
- Formalizar tabla completa de casos de prueba.
- Preparar evidencias visuales reutilizables para memoria y defensa.
- Definir el alcance final de la acción "Colocar" dentro del inventario.
- Cerrar el documento final de FASE 3 para tutoría.
- Decidir qué elementos de FASE 4 se implementan realmente y cuáles quedan como ampliación futura.
- Mantener coherencia entre panel, bitácora, backlog, memoria y defensa.