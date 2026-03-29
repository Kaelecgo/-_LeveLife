# Panel General - LeveLife

## Estado actual
- Arquitectura MVVM con Room, Repository, ViewModel y LiveData operativa.
- Login local y preservación de sesión implementados.
- CRUD de tareas funcional.
- Sistema base de gamificación implementado.
- Tienda conectada a base de datos y operativa.
- Inventario conectado a base de datos.
- Se ha completado un refactor de saneamiento crítico sobre compras y recompensas.

## Bloque actual
Refuerzo de autenticación, consistencia de persistencia local y consolidación documental del núcleo técnico.

## Avances recientes
- La compra de muebles se ejecuta ahora en una transacción del repositorio.
- Se valida dentro de la transacción si el usuario ya posee el mueble.
- Se valida saldo suficiente antes de descontar bayas.
- Se evita la deducción local de bayas desde el ViewModel.
- La finalización de tareas se ejecuta de forma atómica en el repositorio.
- Se centralizan mensajes de error en el ViewModel mediante LiveData.
- Se restauró el feedback visual de recompensa al completar tareas mediante un evento observable desde el ViewModel.
- Se reforzó el sistema de autenticación sustituyendo la validación en texto plano por contraseñas protegidas mediante hash.
- Se mejoró el flujo de registro para hacerlo más fiable y coherente con la entrada automática del usuario tras el alta.
- Se corrigió la recuperación de sesión desde SplashActivity unificando la clave persistida del usuario activo.
- Se eliminó la dependencia de fallbackToDestructiveMigration() y se sustituyó por migraciones explícitas de Room.
- En instalaciones nuevas ya no se generan usuarios demo hardcodeados; solo se mantiene el catálogo inicial de muebles para pruebas controladas.

## Validaciones realizadas
- Completar tarea normal: correcto ✅
- Completar tarea ya completada: no reaplica recompensa.
- Comprar mueble con saldo: correcto ✅
- Comprar mueble sin saldo: bloqueado desde UI.
- Comprar mueble repetido: bloqueado desde UI con estado Purchased.
- Persistencia de saldo tras reinicio: correcta ✅
- Persistencia de inventario tras reinicio: correcta ✅
- Subida de nivel con animación y feedback visual: correcta ✅
- Flujo de usuario nuevo desde registro hasta primera compra: correcto ✅
- Recuperación de sesión desde SplashActivity: correcta ✅
- Inventario vacío: comportamiento comprensible y funcional.
- Toast de recompensa al completar tarea: restaurado y funcional.
- Registro reforzado, auto-login y autenticación con hash: correctos ✅

## Próxima sesión
Cerrar el documento de FASE 3 con evidencias, formalizar la tabla de casos de prueba y definir el alcance final de la acción "Colocar" antes de consolidar FASE 4.

## Pendientes mayores
- Formalizar tabla de casos de prueba.
- Preparar evidencias visuales del flujo principal para memoria y defensa.
- Definir el alcance final de la acción "Colocar" dentro del inventario.
- Verificar migraciones reales sobre bases antiguas si procede.
- Limpiar posibles usuarios demo heredados en instalaciones antiguas.
- Consolidar el documento final de FASE 3 para tutoría.

