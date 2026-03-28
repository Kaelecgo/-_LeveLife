🔴 Error grave 2 — se puede descontar bayas aunque la compra no se inserte

Ahora mismo en purchaseFurniture() del ViewModel hacemos:

if (currentUser != null && currentUser.spendBerries(furniture.getPrice())) {
    repository.purchaseFurniture(currentUser, furniture.getId(), onSuccess);
}

Y en el repositorio insertamos el cruce con IGNORE

Eso significa que si el usuario ya tiene ese mueble:

spendBerries() sí resta saldo
insertUserFurnitureCrossRef(...) puede ser ignorado
resultado: pierde dinero y no obtiene nada nuevo

!!! Eso es un bug funcional bastante importante !!!

Qué hay que hacer

La compra debe ser atómica y validada dentro de la transacción.

No debemos restar bayas antes de comprobar si la compra realmente procede.

Solución correcta

Necesitamos algo así:

- comprobar si el usuario ya posee el mueble
- comprobar si tiene saldo
- solo entonces insertar relación y actualizar saldo
- todo eso dentro de una transacción


He corregido el error de lógica en la compra de muebles. Ahora la operación es atómica y se valida íntegramente dentro de una transacción de base de datos.
Cambios realizados:
1. FurnitureDao: He añadido el método countUserFurniture(userId, furnitureId) para verificar si el usuario ya posee el objeto antes de intentar comprarlo.
2. MainRepository: He rediseñado purchaseFurniture. 
Ahora:
    ◦ Se ejecuta dentro de una transacción (db.runInTransaction).
    ◦ Primero comprueba si el usuario ya tiene el mueble.
    ◦ Obtiene los datos más recientes del usuario desde la DB para validar el saldo real.
    ◦ Si todo es correcto, resta las bayas y guarda la relación. Si algo falla, la transacción no se completa y no se restan bayas.
3. MainViewModel: He eliminado la deducción de bayas local (spendBerries) antes de llamar al repositorio. Ahora simplemente delega la responsabilidad al repositorio y maneja el resultado a través de un nuevo MutableLiveData de errores.
4. ShopActivity: Ahora observa los mensajes de error del ViewModel para informar al usuario (por ejemplo, si intenta comprar algo que ya tiene o si se quedó sin saldo en ese instante).
Con esto, el "Bug grave 2" queda resuelto: ya no es posible perder bayas si la inserción del mueble falla o es ignorada.


🔴 Error grave 3 — completeTask() permite recompensar tareas ya completadas
He mejorado completeTask() agregando if (task.isCompleted()) return;
Pero quiero mejorarlo moviendo la lógica de recompensa a una operación controlada en repositorio.

He refactorizado la lógica de completeTask() para que sea una operación atómica controlada por el repositorio, similar a como hicimos con la compra de muebles.
Cambios realizados:
1. TaskDao: He añadido el método getTaskById(id) para poder recuperar la versión más reciente de la tarea desde la base de datos antes de procesar la recompensa.
2. MainRepository: He implementado el nuevo método completeTask(taskId, userId, callback).
    ◦ Todo ocurre dentro de una transacción (runInTransaction).
    ◦ Se valida que la tarea exista y no esté ya completada.
    ◦ Se obtienen los datos frescos del usuario.
    ◦ Se aplican las recompensas (XP y bayas) y se marca la tarea como completada en un solo paso.
3. MainViewModel: He simplificado el método completeTask(Task task).
    ◦ Ahora solo recibe el objeto Task (ya no se necesita que se le pase el User desde el controlador/actividad).
    ◦ Realiza una validación previa rápida y delega la lógica pesada al repositorio.
    ◦ Maneja posibles errores a través del LiveData errorMessages.
Ventaja principal: Con este cambio, es imposible que un usuario reciba recompensas dobles por la misma tarea (por ejemplo, pulsando muy rápido el botón) o que el estado de la tarea y el saldo del usuario queden desincronizados si hay un fallo de red o de escritura.

🟠 Problema 4 — seeder con IDs asumidos
Aquí haces:

taskDao.insertTask(new Task(1, ...));
taskDao.insertTask(new Task(2, ...));

Asumes que:

admin = ID 1
irena = ID 2

Eso solo te funciona por casualidad controlada mientras destruyes DB y recreas.

Para fase de desarrollo vale, pero debes saber que no es robusto.

Recomendación

Para seguir desarrollando, puedes dejarlo temporalmente.
Pero para memoria/defensa, debes explicarlo como:

- datos semilla de pruebas
- entorno controlado de desarrollo
- recreación destructiva usada solo durante prototipado

🟠 Problema 5 — UserDao tiene responsabilidad mezclada
Tienes esto en UserDao:
@Insert(onConflict = OnConflictStrategy.IGNORE)
void insertUserFurnitureCrossRef(UserFurnitureCrossRef crossRef);
Eso conceptualmente está raro.

Por qué

UserDao debería gestionar usuarios.
La tabla intermedia de inventario debería vivir en:
    ◦ FurnitureDao, o mejor
    ◦ un InventoryDao / UserFurnitureDao

No rompe el proyecto, pero sí es mala separación de responsabilidades.
Mi consejo

No lo rehagas todo ahora.
Haz una mejora controlada:
    ◦ mover ese método a FurnitureDao o a un DAO específico más adelante
    ◦ documentarlo como refactor pendiente

🟡 Problema 7 — demasiada lógica de negocio en Activity/ViewModel sin blindaje de repositorio

Ahora mismo:
    ◦ la Activity decide parte del flujo de compra
    ◦ el ViewModel modifica estado del usuario
    ◦ el repositorio solo persiste

Funciona, pero no está completamente protegido.

Para un TFG está aceptable, pero el punto débil es que la lógica de negocio queda repartida.



Al cambiar la firma del método completeTask en el MainViewModel (ahora solo acepta una Task), el código antiguo en TaskActivity.java intentaba llamar al método con dos argumentos: viewModel.completeTask(task, currentUser). Esto hacía que la aplicación no pudiera compilar o fallara al intentar ejecutar un método que ya no existe.
Ya he actualizado TaskActivity.java con la nueva lógica:
1. Llamada corregida: Ahora usa viewModel.completeTask(task).
2. Lógica movida: He eliminado la suma de XP y bayas que se hacía manualmente en la Activity. Ahora todo eso ocurre de forma segura dentro del repositorio.
3. Detección de Subida de Nivel: He añadido un sistema en el Observer del usuario. Si el nivel guardado en la DB sube respecto al que tiene la Activity en memoria, se dispara automáticamente el diálogo de "¡Has subido de nivel!". Esto es mucho más robusto que hacerlo manualmente antes de la transacción.
4. Manejo de Errores: He añadido el observador para getErrorMessages() para que, si algo falla en la base de datos, el usuario reciba un Toast con la explicación.
Con estos cambios, la aplicación debería volver a ser estable y la lógica de recompensas es ahora a prueba de fallos.

Proyecto: LeveLife
Tipo: TFG Android en Java

Estado actual:
- Arquitectura MVVM con Room, Repository, ViewModel y LiveData
- Login y sesión persistente implementados
- CRUD de tareas funcional
- Tienda funcional
- Inventario funcional
- Refactor de compra y recompensas completado

Documentación de trabajo:
- 00_PANEL_GENERAL.md
- 01_BITACORA_SESIONES.md
- 02_BACKLOG_TECNICO.md

Últimos avances:
- compra validada con persistencia tras reinicio
- inventario persistente
- SplashActivity recupera sesión activa

Objetivo actual:
- [pon aquí el bloque siguiente]

Pendientes:
- [pega el backlog o resumen]


Observación importante

Aquí hay un detalle que luego tendremos que decidir bien en la narrativa final:

En tu documentación inicial de FASE 1 aparece la idea de que la tabla de inventario pueda tener un campo isPlaced, mientras que en el estado actual que hemos venido trabajando el foco real está en la relación UserFurnitureCrossRef básica y en un inventario funcional persistente.
Eso no es un problema ahora. Solo significa que, cuando cerremos FASE 4, tendremos que dejar claro si:

esa parte quedó como ampliación futura
o si finalmente la implementáis

Y eso lo controlaremos juntos para que no haya contradicciones entre memoria, fase y defensa.