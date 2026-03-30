# Backlog Técnico - LeveLife

## Crítico
- [ ] Cerrar documento definitivo de FASE 3 para tutoría.
- [ ] Montar tabla de evidencias de FASE 3.
- [ ] Formalizar tabla completa de casos de prueba con resultado esperado y resultado real.
- [ ] Preparar evidencias visuales del flujo principal para memoria y defensa.
- [ ] Definir el comportamiento funcional de la acción "Colocar".
- [ ] Decidir si la colocación de muebles será persistente en esta versión del TFG.
- [ ] Revisar mensajes de error y feedback visual del flujo de login y registro tras el refactor de autenticación.
- [ ] Probar login y registro sobre escenarios de datos migrados.
- [ ] Validar manualmente el nuevo flujo de creación de tareas con BottomSheet.
- [ ] Verificar reward preview en tiempo real al cambiar dificultad y frecuencia.
- [ ] Validar EcoCoins en tareas ecológicas dentro del flujo real de uso.
- [ ] Validar recurrencia básica por periodo actual en tareas recurrentes.

## Alto
- [ ] Crear un empty state sólido para inventario vacío.
- [ ] Revisar comportamiento con varios usuarios de prueba.
- [ ] Revisar manejo de errores y feedback visual en ShopActivity y TaskActivity.
- [ ] Verificar migraciones reales sobre dispositivos o bases con datos previos, si se dispone de ellas.
- [ ] Documentar en memoria la eliminación de fallbackToDestructiveMigration() como estrategia principal y su impacto.
- [ ] Documentar en memoria la migración a contraseñas protegidas mediante hash.
- [ ] Documentar en memoria el sistema enriquecido de Task: dificultad, frecuencia y recompensa ecológica.
- [ ] Documentar TaskRewardCalculator, EcoCoins y la lógica centralizada de recompensas.
- [ ] Documentar la normalización de etiquetas y el bug resuelto entre UI y lógica interna.
- [ ] Documentar que la recurrencia actual es un MVP basado en el periodo vigente y no en historial completo separado.
- [ ] Consolidar el documento final de FASE 3 con el estado real del proyecto.

## Medio
- [ ] Añadir pruebas manuales de flujo real para login, sesión persistente y registro.
- [ ] Documentar en memoria la validación del flujo extremo a extremo.
- [ ] Justificar en memoria el uso de datos seed para pruebas.
- [ ] Revisar seeder con IDs asumidos.
- [ ] Revisar datos seed heredados y su justificación como entorno de pruebas controlado.
- [ ] Revisar si el alta debe mostrar un mensaje específico de auto-login exitoso.
- [ ] Mover a strings.xml cualquier literal pendiente del feedback de recompensa.
- [ ] Mejorar la separación de responsabilidades de inventario en DAO.
- [ ] Limpiar usuarios demo heredados en bases antiguas si realmente existen.
- [ ] Mantener coherencia entre backlog, panel, bitácora, memoria, fase y defensa.

## Bajo
- [ ] Limpiar configuración heredada de gradle.properties.
- [ ] Separar ViewModel y Repository por features cuando el núcleo esté completamente estabilizado.
- [ ] Mejorar documentación de arquitectura.
- [ ] Preparar material de defensa asociado al refactor técnico.
- [x] Limpiar nombres y consistencia de clases.
- [x] Validar subida de nivel tras completar varias tareas con acumulación de XP.
- [x] Probar flujo con usuario nuevo desde registro hasta primera compra.
- [x] Verificar estado de sesión recuperado correctamente desde SplashActivity.
- [x] Verificar compilación completa tras el refactor de completeTask().
- [x] Probar compra de mueble con saldo suficiente.
- [x] Probar compra de mueble sin saldo suficiente.
- [x] Probar compra de mueble ya adquirido.
- [x] Verificar actualización reactiva del inventario tras compra.
- [x] Revisar InventoryActivity.
- [x] Validar que el inventario se actualiza correctamente tras compra.
- [x] Comprobar persistencia de inventario y saldo tras cerrar y reabrir la app.
- [x] Confirmar que el inventario sigue mostrando los muebles tras reinicio.
- [x] Endurecer autenticación y gestión de usuarios.
- [x] Corregir recuperación de sesión desde SplashActivity.
- [x] Sustituir contraseñas en texto plano por hash.
- [x] Reemplazar migración destructiva por migraciones explícitas.
- [x] Dejar de sembrar usuarios demo en instalaciones nuevas.
- [x] Restaurar el Toast de recompensa al completar tarea.
- [x] Confirmar que no hay recompensas dobles por pulsaciones repetidas.
- [x] Documentar el uso de SplashActivity y sesión persistente en memoria.
- [x] Consolidar el refactor de transacciones y validaciones en la documentación técnica.