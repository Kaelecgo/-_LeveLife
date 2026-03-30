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
- Se implementó `purchaseFurniture(int userId, Furniture furniture, PurchaseCallback callback)` en el repositorio.
- Se validó propiedad previa del mueble mediante `countUserFurniture()`.
- Se validó saldo dentro de la transacción antes de actualizar berries e inventario.
- Se refactorizó `completeTask()` para ejecutarse en el repositorio de forma atómica.
- Se añadió canal de errores con `MutableLiveData` en MainViewModel.
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

---

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

### Próximo paso
Validar formalmente los flujos principales del sistema y recopilar evidencias reutilizables.

---

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
- Flujo de registro/login
- Flujo de tienda e inventario

### Resultado
Los flujos críticos del sistema quedan validados de extremo a extremo.
La persistencia local de sesión, saldo e inventario funciona correctamente.
El flujo de usuario nuevo también se comporta correctamente desde el registro hasta la primera compra.

### Incidencias detectadas
- Se detectó una regresión temporal en el feedback visual de recompensa al completar tareas.

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
Reforzar autenticación y persistencia local.

---

## Sesión 3 - Refuerzo de autenticación y persistencia de datos

### Objetivo
Reducir riesgos técnicos del proyecto reforzando el sistema de autenticación, el flujo de registro y la persistencia de datos ante cambios de esquema.

### Problemas detectados
- Las contraseñas se almacenaban y validaban en texto plano.
- El flujo de registro dependía demasiado de validaciones desde la capa de aplicación.
- La recuperación de sesión presentaba una incoherencia entre la clave guardada por login y la clave consultada por SplashActivity.
- La base de datos seguía dependiendo de migración destructiva, con riesgo de pérdida de progreso del usuario.
- Se seguían sembrando usuarios demo en la base de datos para instalaciones nuevas.

### Tareas realizadas
- Se implementó `PasswordUtils.java` con hashing de contraseñas mediante PBKDF2 y salt aleatoria.
- Se añadió lógica de actualización transparente para usuarios antiguos durante el login.
- Se reforzó el registro para evitar inconsistencias y mejorar la fiabilidad del alta.
- Se corrigió SplashActivity para que use la misma clave persistida por el login (`saved_user_id`).
- Se reemplazó la migración destructiva por migraciones explícitas de Room.
- Se añadió `fallbackToDestructiveMigrationOnDowngrade()` como protección en entorno de desarrollo.
- Se dejó de generar usuarios demo hardcodeados en instalaciones nuevas.
- Se añadió una prueba unitaria para el helper de contraseñas.

### Archivos afectados
- User.java
- UserDao.java
- PasswordUtils.java
- MainRepository.java
- LoginActivity.java
- SplashActivity.java
- AppDatabase.java
- PasswordUtilsTest.java

### Resultado
El sistema de autenticación queda reforzado, la sesión persistente funciona de forma coherente desde el arranque y la persistencia del progreso deja de depender de una estrategia destructiva como mecanismo principal.

### Pruebas realizadas
- Verificación del login con hash de contraseña.
- Verificación del auto-login tras registro.
- Validación del acceso directo desde SplashActivity con sesión persistida.
- Ejecución de tests unitarios del módulo (`testDebugUnitTest`).

### Próximo paso
Estabilizar la nueva versión del esquema e integrar el sistema avanzado de hábitos sobre master.

---

## Sesión 4 - Consolidación técnica del sistema de hábitos y migración v7

### Objetivo
Estabilizar la aplicación tras la sincronización con master, alinear el esquema de Room con el modelo real y consolidar el nuevo sistema de hábitos enriquecidos.

### Problemas detectados
- La aplicación llegó a crashear por inconsistencias entre el esquema real de la base de datos y las entidades de Room.
- Parte del trabajo funcional avanzado no estaba presente en el estado remoto principal y tuvo que recuperarse.
- El sistema avanzado de tareas presentaba desajustes entre los valores visibles en UI y la lógica interna.
- Una tarea recién creada podía otorgar recompensa sin quedar correctamente marcada o deshabilitada.
- La categoría ecológica y el cálculo por dificultad no siempre recalculaban las recompensas de forma correcta.

### Tareas realizadas
- Se estabilizó la base de datos en versión 7.
- Se completó la migración `6 -> 7`, añadiendo correctamente los nuevos campos:
    - `eco_coins`
    - `eco_reward`
    - `difficulty`
    - `frequency`
    - `last_completed_at`
- Se ajustaron `defaultValue` para que Room validase correctamente la migración.
- Se corrigió el repoblado automático del catálogo cuando la tabla `furniture` está vacía.
- Se recuperó e integró sobre master el sistema avanzado de creación de tareas.
- Se restauró el flujo del FAB para abrir un `BottomSheetDialog`.
- Se incorporó el layout `bottom_sheet_create_task.xml`.
- Se integraron clases auxiliares como:
    - `TaskDraft`
    - `TaskReward`
    - `TaskRewardCalculator`
    - `TaskRecurrenceUtils`
- Se amplió `Task` con atributos de dificultad, frecuencia y recompensa ecológica.
- Se añadió `eco_coins` al modelo de usuario y a la interfaz principal.
- Se reforzó la normalización de categorías, dificultad y frecuencia para desacoplar la lógica de negocio de textos visibles, emojis o traducciones.
- Se hizo que la vista previa de recompensas se recalcule automáticamente al cambiar selectores.
- Se ajustó el comportamiento de completado para que las tareas puntuales queden marcadas, deshabilitadas y movidas al final de la lista.
- Se dejó la recurrencia básica resuelta como MVP mediante control por periodo actual.
- Se actualizó TaskAdapter para ordenar según el estado de completado en el periodo vigente.
- Se añadieron y ejecutaron pruebas unitarias para recompensas, recurrencia y normalización.

### Archivos afectados
- AppDatabase.java
- User.java
- Task.java
- MainRepository.java
- MainViewModel.java
- MainActivity.java
- TaskActivity.java
- TaskAdapter.java
- DialogUtils.java
- bottom_sheet_create_task.xml
- TaskDraft.java
- TaskReward.java
- TaskRewardCalculator.java
- TaskRecurrenceUtils.java

### Resultado
La aplicación vuelve a arrancar de forma estable.
El sistema de hábitos queda enriquecido con dificultad, frecuencia, recompensa ecológica y recurrencia básica.
EcoCoins pasa a formar parte real del flujo de gamificación.
La creación de tareas mejora notablemente en claridad y feedback.
Se corrige el exploit funcional relacionado con el completado de tareas.

### Pruebas realizadas
- Ejecución repetida de `.\gradlew.bat testDebugUnitTest`.
- Validación de recompensas dinámicas según dificultad.
- Validación de EcoCoins en tareas ecológicas.
- Validación de bloqueo por periodo en tareas recurrentes.
- Validación del reward preview en el BottomSheet.
- Verificación del arranque correcto tras alinear esquema y migración.
- Verificación del repoblado automático del catálogo de tienda.

### Estado actual del bloque
La recurrencia queda funcional en estado MVP, basada en el periodo actual. Todavía no existe un historial completo de completados mediante una entidad separada.

### Próximo paso
Formalizar casos de prueba, preparar evidencias visuales y decidir el alcance final de la acción "Colocar" antes de cerrar documentalmente FASE 3.