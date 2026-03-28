# Bitácora de Sesiones

## Sesión 0 - Saneamiento crítico del núcleo

### Objetivo
Corregir errores de consistencia en la lógica de compra de muebles y en la entrega de recompensas por completar tareas.

### Problemas detectados
- La lógica de compra necesitaba validación atómica de saldo y propiedad.
- Existía riesgo de inconsistencias si se repartía la lógica entre UI, ViewModel y Repository.
- La lógica de completar tareas debía blindarse para evitar dobles recompensas.
- Había que adaptar la UI a los nuevos contratos del ViewModel.

### Tareas realizadas
- Se añadió una referencia persistente a AppDatabase dentro de MainRepository.
- Se implementó purchaseFurniture(int userId, Furniture furniture, PurchaseCallback callback) en el repositorio.
- Se validó propiedad previa del mueble mediante countUserFurniture().
- Se validó saldo dentro de la transacción antes de actualizar berries e inventario.
- Se refactorizó completeTask() para ejecutarse en el repositorio de forma atómica.
- Se añadió canal de errores con MutableLiveData en MainViewModel.
- Se actualizó ShopActivity para observar mensajes de error.
- Se actualizó TaskActivity para trabajar con el nuevo flujo de finalización de tareas.

### Archivos afectados
- MainRepository.java
- MainViewModel.java
- ShopActivity.java
- TaskActivity.java
- FurnitureDao.java
- UserFurnitureCrossRef.java

### Resultado
El sistema de compra queda protegido frente a compras duplicadas y validaciones inconsistentes.
La finalización de tareas no vuelve a aplicar recompensas a tareas ya completadas.
La UI responde correctamente a estados de compra bloqueada y a tareas ya completadas.

### Pruebas realizadas
- Completar tarea normal: correcto.
- Completar tarea ya completada: no se vuelve a completar ni a recompensar.
- Comprar mueble con saldo: correcto.
- Comprar mueble sin saldo: botón deshabilitado.
- Comprar mueble repetido: botón deshabilitado con texto "Purchased".

### Próximo paso
Revisar InventoryActivity y validar el flujo completo de usuario.

## Sesión 1 - Revisión de inventario y validación de interfaz

### Objetivo
Comprobar que el inventario muestra correctamente los muebles comprados y que la interfaz distingue entre catálogo de tienda e inventario del usuario.

### Tareas realizadas
- Se revisó InventoryActivity para validar la carga del inventario desde MainViewModel.
- Se confirmó el uso de GridLayoutManager para mostrar los muebles en formato rejilla.
- Se comprobó que InventoryAdapter reutiliza el layout de tienda y adapta el botón de acción al contexto de inventario.
- Se validó que la acción actual de "Colocar" está implementada como MVP con feedback mediante Toast.
- Se revisó FurnitureAdapter para confirmar el bloqueo visual de compra por saldo insuficiente y por muebles ya adquiridos.

### Archivos revisados
- InventoryActivity.java
- InventoryAdapter.java
- FurnitureAdapter.java

### Resultado
El inventario se comporta correctamente como listado de objetos comprados.
La lógica de colocación todavía no persiste estado, pero el flujo actual es coherente con un MVP funcional.
La tienda distingue correctamente entre objetos disponibles, no asequibles y ya comprados.

Además, se validó la persistencia real del flujo de compra:
tras adquirir una planta con un coste de 30 bayas desde un saldo inicial de 50, el usuario conservó correctamente 20 bayas después de cerrar y reabrir la aplicación.
El mueble siguió figurando como comprado y permaneció visible en el inventario, confirmando la persistencia local de sesión, saldo e inventario.

### Pruebas realizadas
- Se compró un mueble desde la tienda con saldo suficiente.
- El sistema mostró correctamente el Toast de compra realizada.
- El mueble comprado apareció en el inventario del usuario.
- Se cerró completamente la aplicación.
- Al volver a abrirla, SplashActivity recuperó la sesión activa sin solicitar nuevo login.
- El saldo del usuario se mantuvo correctamente actualizado tras reinicio.
- El mueble comprado siguió apareciendo en el inventario tras reinicio.

## Sesión 2 - Pruebas funcionales y evidencias

### Objetivo
Validar formalmente los flujos principales del sistema y recopilar evidencias reutilizables para memoria y defensa.

### Tareas realizadas
- Se validó la subida de nivel completando tareas hasta alcanzar el siguiente umbral de experiencia.
- Se comprobó el incremento correcto de XP y el cambio de nivel.
- Se verificó la animación de la barra de progreso y el feedback visual asociado a la subida de nivel.
- Se probó el flujo de usuario nuevo desde registro hasta primeras acciones dentro de la app.
- Se confirmó el saldo inicial correcto en un usuario recién registrado.
- Se verificó que el inventario aparece vacío en usuarios sin compras previas.
- Se validó la obtención de bayas y experiencia al completar tareas.
- Se comprobó que un usuario nuevo puede comprar un mueble cuando alcanza saldo suficiente.
- Se validó la persistencia de sesión mediante SplashActivity tras cerrar y reabrir la aplicación.
- Se confirmó la persistencia de saldo e inventario tras reinicio.
- Se recopilaron capturas del flujo principal para documentación.

### Archivos revisados
- MainActivity.java
- SplashActivity.java
- InventoryActivity.java
- flujo de registro/login
- flujo de tienda e inventario

### Resultado
Los flujos críticos del sistema quedan validados de extremo a extremo.
La persistencia local de sesión, saldo e inventario funciona correctamente.
El flujo de usuario nuevo también se comporta correctamente desde el registro hasta la primera compra.

### Incidencias detectadas

### Corrección aplicada
- Se restauró el feedback visual de recompensa al completar tareas.
- MainRepository devuelve ahora los valores reales de XP y bayas obtenidos tras una operación exitosa.
- MainViewModel expone un LiveData específico de mensaje de recompensa.
- TaskActivity observa ese mensaje, muestra el Toast correspondiente y limpia el evento para evitar repeticiones tras cambios de configuración.

### Resultado tras corrección
- El usuario vuelve a recibir feedback inmediato al completar una tarea.
- El mensaje mostrado refleja los valores reales confirmados por la operación de base de datos.
- El diálogo de subida de nivel sigue funcionando correctamente sin interferencias.

### Pruebas realizadas
- Caso 1: Subida de nivel -> correcto ✅
- Caso 2: Usuario nuevo completo -> correcto ✅
- Caso 3: Sesión persistente -> correcto ✅
- Caso 4: Inventario vacío -> correcto y comprensible ✅
- Empty state adicional: actualmente se muestra un Toast indicando "El inventario está vacío".

### Evidencias
- Se realizaron capturas del flujo principal para memoria y defensa.

### Próximo paso
Corregir la regresión del feedback de recompensa al completar tareas y trasladar las pruebas validadas a la memoria técnica.
