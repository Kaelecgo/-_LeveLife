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
Cierre de consistencia del núcleo y validación funcional del flujo principal.

## Avances recientes
- La compra de muebles se ejecuta ahora en una transacción del repositorio.
- Se valida dentro de la transacción si el usuario ya posee el mueble.
- Se valida saldo suficiente antes de descontar bayas.
- Se evita la deducción local de bayas desde el ViewModel.
- La finalización de tareas se ejecuta de forma atómica en el repositorio.
- Se centralizan mensajes de error en el ViewModel mediante LiveData.
- Se restauró el feedback visual de recompensa al completar tareas mediante un evento observable desde el ViewModel.

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

## Próxima sesión
Trasladar el estado actual del proyecto a la memoria técnica base y empezar los apartados de requisitos, arquitectura y modelo de datos.

## Pendientes mayores
- Validar subida de nivel tras varias tareas con acumulación de XP.
- Probar flujo completo con usuario nuevo desde registro hasta primera compra.
- Preparar tabla formal de casos de prueba.
- Consolidar memoria técnica y evidencias.
- Definir el alcance final de la acción "Colocar" dentro del inventario.

