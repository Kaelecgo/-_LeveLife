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

## Próxima sesión
Corregir la regresión del feedback de recompensa al completar tareas y trasladar las pruebas funcionales a la memoria técnica.

## Pendientes mayores
- Validar subida de nivel tras varias tareas con acumulación de XP.
- Probar flujo completo con usuario nuevo desde registro hasta primera compra.
- Preparar tabla formal de casos de prueba.
- Consolidar memoria técnica y evidencias.
- Definir el alcance final de la acción "Colocar" dentro del inventario.

