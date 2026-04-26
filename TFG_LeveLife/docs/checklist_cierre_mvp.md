# Checklist Final De Validacion MVP - LeveLife

Esta checklist sirve para congelar desarrollo con un MVP estable antes de pasar a:
- APK firmada
- pruebas completas
- memoria
- manual de usuario
- demo y defensa

Fecha de referencia: 2026-04-26

## Criterio de salida

El MVP se considera listo si:
- no hay crashes en el flujo principal
- el usuario entiende el bucle `tareas -> recompensa -> tienda -> inventario -> habitacion`
- la sesion funciona bien
- la economia se actualiza correctamente
- la colocacion de muebles persiste
- los errores visibles al usuario son asumibles para defensa

## Como usar esta checklist

Marca cada caso como:
- `OK`
- `KO`
- `NA`

Y anota evidencia breve:
- dispositivo o emulador
- usuario usado
- pasos si fallo
- captura si aplica

---

## Bloque 1 - Acceso y sesion

### MVP-01 Registro de usuario nuevo
- Prioridad: Bloqueante
- Pasos:
  1. Abrir la app sin sesion previa
  2. Registrar un usuario nuevo
  3. Entrar al `Home`
- Esperado:
  - no hay crash
  - el usuario entra directamente en la app
  - aparece con saldo inicial correcto
  - aparecen tareas iniciales
- Resultado:
- Evidencia:

### MVP-02 Login con usuario existente
- Prioridad: Bloqueante
- Pasos:
  1. Cerrar sesion
  2. Entrar con un usuario existente valido
- Esperado:
  - login correcto
  - se conserva progreso previo
  - no se duplican datos iniciales
- Resultado:
- Evidencia:

### MVP-03 Login fallido
- Prioridad: Alta
- Pasos:
  1. Introducir contrasena incorrecta
- Esperado:
  - aparece error
  - no entra a la app
- Resultado:
- Evidencia:

### MVP-04 Persistencia de sesion
- Prioridad: Bloqueante
- Pasos:
  1. Entrar con usuario valido
  2. Cerrar y reabrir la app
- Esperado:
  - entra directo sin pedir login
  - no pierde estado
- Resultado:
- Evidencia:

### MVP-05 Logout desde todas las pantallas
- Prioridad: Bloqueante
- Pasos:
  1. Probar `Salir` desde `Home`
  2. Probar `Salir` desde `Tareas`
  3. Probar `Salir` desde `Tienda`
  4. Probar `Salir` desde `Inventario`
- Esperado:
  - se muestra confirmacion
  - se limpia sesion
  - vuelve a `Login`
  - no permite volver atras a la app abierta
- Resultado:
- Evidencia:

---

## Bloque 2 - Tareas y recompensas

### MVP-06 Visualizacion inicial de tareas
- Prioridad: Bloqueante
- Pasos:
  1. Entrar en `Tareas` con usuario nuevo
- Esperado:
  - se ven tareas iniciales
  - el orden es `Una vez -> Diaria -> Semanal -> Mensual`
  - las recompensas se muestran correctamente
- Resultado:
- Evidencia:

### MVP-07 Crear tarea nueva
- Prioridad: Alta
- Pasos:
  1. Crear una tarea desde el `BottomSheet`
  2. Cambiar categoria, dificultad y frecuencia
- Esperado:
  - el preview de recompensa cambia bien
  - la tarea se guarda
  - aparece en la lista con el orden esperado
- Resultado:
- Evidencia:

### MVP-08 Completar tarea de una vez
- Prioridad: Bloqueante
- Pasos:
  1. Completar una tarea `Una vez`
- Esperado:
  - suma XP y monedas
  - la tarea pasa a completada
  - no permite volver a completarla
- Resultado:
- Evidencia:

### MVP-09 Completar tarea diaria
- Prioridad: Bloqueante
- Pasos:
  1. Completar una tarea `Diaria`
- Esperado:
  - suma XP y monedas
  - queda bloqueada en el periodo actual
  - se muestra el tiempo restante de reinicio
- Resultado:
- Evidencia:

### MVP-10 Tareas semanales y mensuales
- Prioridad: Alta
- Pasos:
  1. Completar una `Semanal`
  2. Completar una `Mensual`
- Esperado:
  - se aplica el mismo bloqueo por periodo
  - el contador o estado es coherente
- Resultado:
- Evidencia:

### MVP-11 Recompensa segun dificultad
- Prioridad: Alta
- Pasos:
  1. Crear tareas `Facil`, `Media`, `Dificil`
  2. Comparar preview y resultado al completar
- Esperado:
  - las recompensas cambian segun dificultad
- Resultado:
- Evidencia:

### MVP-12 Categoria eco
- Prioridad: Alta
- Pasos:
  1. Completar una tarea de `Sostenibilidad`
- Esperado:
  - suma `EcoCoins`
  - el usuario entiende que esa moneda sirve para muebles eco
- Resultado:
- Evidencia:

### MVP-13 Borrado de tareas
- Prioridad: Media
- Pasos:
  1. Eliminar una tarea deslizando
- Esperado:
  - aparece confirmacion
  - se elimina correctamente
  - el feedback visual sigue el estilo de la app
- Resultado:
- Evidencia:

---

## Bloque 3 - Progreso y gamificacion

### MVP-14 Barra de XP
- Prioridad: Bloqueante
- Pasos:
  1. Ganar XP sin subir de nivel
  2. Ganar XP suficiente para subir de nivel
- Esperado:
  - el texto muestra `XP actual / XP necesaria`
  - la barra se anima correctamente
  - en level-up se completa, reinicia y muestra el sobrante
- Resultado:
- Evidencia:

### MVP-15 Subida de nivel
- Prioridad: Alta
- Pasos:
  1. Completar tareas hasta subir de nivel
- Esperado:
  - cambia el nivel
  - la barra queda en el valor correcto del nuevo nivel
  - el dialogo de subida aparece
- Resultado:P
- Evidencia:

---

## Bloque 4 - Tienda y economia

### MVP-16 Catalogo visible y comprensible
- Prioridad: Bloqueante
- Pasos:
  1. Entrar en `Tienda`
- Esperado:
  - se entiende que muebles usan `Bayas` y cuales `EcoCoins`
  - hay catalogo suficiente para probar ambos tipos
- Resultado:
- Evidencia:

### MVP-17 Compra con Bayas
- Prioridad: Bloqueante
- Pasos:
  1. Comprar un mueble cozy
- Esperado:
  - descuenta `Bayas`
  - el item pasa a inventario
  - el boton cambia a comprado o se desactiva
- Resultado:
- Evidencia:

### MVP-18 Compra con EcoCoins
- Prioridad: Bloqueante
- Pasos:
  1. Comprar un mueble eco
- Esperado:
  - descuenta `EcoCoins`
  - el item pasa a inventario
- Resultado:
- Evidencia:

### MVP-19 Compra sin saldo suficiente
- Prioridad: Alta
- Pasos:
  1. Intentar comprar sin moneda suficiente
- Esperado:
  - el boton no permite compra o el flujo la bloquea bien
  - no hay saldos negativos
- Resultado:
- Evidencia:

### MVP-20 No duplicar compra
- Prioridad: Alta
- Pasos:
  1. Comprar un mueble
  2. Intentar volver a comprarlo
- Esperado:
  - no se duplica
  - el inventario no recibe copias no deseadas
- Resultado:
- Evidencia:

---

## Bloque 5 - Inventario y habitacion

### MVP-21 Inventario vacio y no vacio
- Prioridad: Media
- Pasos:
  1. Entrar sin muebles
  2. Entrar con muebles comprados
- Esperado:
  - el empty state es correcto
  - el inventario muestra muebles comprados
- Resultado:
- Evidencia:

### MVP-22 Colocar mueble desde inventario
- Prioridad: Bloqueante
- Pasos:
  1. Elegir un mueble
  2. Colocarlo en la habitacion
- Esperado:
  - solo se ofrecen slots compatibles
  - se coloca sin crash
  - vuelve a `Home` mostrando el cambio
- Resultado:
- Evidencia:

### MVP-23 Mover mueble
- Prioridad: Alta
- Pasos:
  1. Mover un mueble ya colocado a otro slot valido
- Esperado:
  - cambia de posicion
  - el inventario refleja el nuevo slot
- Resultado:
- Evidencia:

### MVP-24 Reemplazo de slot ocupado
- Prioridad: Alta
- Pasos:
  1. Intentar colocar un mueble en un slot ya ocupado
- Esperado:
  - aparece confirmacion
  - si aceptas, reemplaza correctamente
- Resultado:
- Evidencia:

### MVP-25 Retirar mueble
- Prioridad: Alta
- Pasos:
  1. Retirar un mueble desde `Home` o `Inventario`
- Esperado:
  - desaparece de la habitacion
  - sigue existiendo en inventario
- Resultado:
- Evidencia:

### MVP-26 Persistencia de habitacion
- Prioridad: Bloqueante
- Pasos:
  1. Colocar uno o varios muebles
  2. Salir de la app
  3. Volver a entrar
- Esperado:
  - la habitacion conserva la colocacion
- Resultado:
- Evidencia:

---

## Bloque 6 - Navegacion general

### MVP-27 Navegacion inferior
- Prioridad: Alta
- Pasos:
  1. Moverse entre `Home`, `Tareas`, `Tienda` e `Inventario`
- Esperado:
  - cambia bien la pantalla
  - el tab activo queda marcado correctamente
  - volver a `Home` funciona desde todas
- Resultado:
- Evidencia:

### MVP-28 Boton atras
- Prioridad: Media
- Pasos:
  1. Navegar varias pantallas
  2. Probar atras del sistema
- Esperado:
  - no deja estados rotos
  - desde `Home` no rompe la sesion
- Resultado:
- Evidencia:

---

## Bloque 7 - Estados de borde

### MVP-29 Usuario existente tras cambios de version
- Prioridad: Alta
- Pasos:
  1. Abrir un usuario antiguo con datos ya creados
- Esperado:
  - no hay crash por migracion
  - mantiene inventario, tareas y colocacion
- Resultado:
- Evidencia:

### MVP-30 Usuario con varias monedas y varios muebles
- Prioridad: Media
- Pasos:
  1. Probar un usuario avanzado
- Esperado:
  - los contadores y listados siguen siendo coherentes
- Resultado:
- Evidencia:

### MVP-31 Textos y consistencia visual
- Prioridad: Media
- Pasos:
  1. Recorrer todas las pantallas principales
- Esperado:
  - no hay textos rotos
  - no hay acentos raros ni placeholders visibles
  - no hay feedbacks con estilo inconsistente
- Resultado:
- Evidencia:

---

## Incidencias Encontradas

### INC-01
- Caso:
- Severidad:
- Descripcion:
- Pasos:
- Resultado esperado:
- Resultado real:
- Captura:

### INC-02
- Caso:
- Severidad:
- Descripcion:
- Pasos:
- Resultado esperado:
- Resultado real:
- Captura:

### INC-03
- Caso:
- Severidad:
- Descripcion:
- Pasos:
- Resultado esperado:
- Resultado real:
- Captura:

---

## Recomendacion De Ejecucion

Orden minimo recomendado:
1. `MVP-01` a `MVP-05`
2. `MVP-06` a `MVP-15`
3. `MVP-16` a `MVP-26`
4. `MVP-27` a `MVP-31`

Si los bloques 1, 2, 4 y 5 pasan sin incidencias graves, el MVP ya esta en condiciones razonables para congelar desarrollo y pasar a documentacion, pruebas finales y defensa.
