# Backlog Técnico - LeveLife

## Crítico
- [ ] Cerrar documento definitivo de FASE 3 para tutoría
- [ ] Montar tabla de evidencias de FASE 3
- [ ] Limpiar usuarios demo heredados en bases antiguas si procede
- [ ] Verificar migraciones reales sobre dispositivos con bases previas
- [ ] Probar login y registro sobre escenarios de datos migrados
- [ ] Revisar mensajes de error del registro y login tras el refactor
- [x] Validar subida de nivel tras completar varias tareas con acumulación de XP
- [x] Probar flujo con usuario nuevo desde registro hasta primera compra
- [x] Verificar estado de sesión recuperado correctamente desde SplashActivity
- [x] Verificar compilación completa tras el refactor de completeTask()
- [x] Probar compra de mueble con saldo suficiente
- [x] Probar compra de mueble sin saldo suficiente
- [x] Probar compra de mueble ya adquirido
- [x] Verificar actualización reactiva del inventario tras compra
- [x] Revisar InventoryActivity
- [x] Validar que el inventario se actualiza correctamente tras compra
- [x] Comprobar persistencia de inventario y saldo tras cerrar y reabrir la app
- [x] Confirmar que el inventario sigue mostrando los muebles tras reinicio
- [x] Endurecer autenticación y gestión de usuarios
- [x] Corregir recuperación de sesión desde SplashActivity
- [x] Sustituir contraseñas en texto plano por hash
- [x] Reemplazar migración destructiva por migraciones explícitas
- [x] Dejar de sembrar usuarios demo en instalaciones nuevas

## Alto
- [ ] Crear empty state más sólido para inventario vacío
- [ ] Revisar visualmente feedback de login, registro y errores tras el refactor de auth
- [x] Restaurar el Toast de recompensa al completar tarea
- [ ] Formalizar mensaje visual del inventario vacío más allá del Toast actual
- [ ] Revisar mensajes de error y feedback visual en UI
- [ ] Verificar comportamiento con varios usuarios de prueba
- [x] Preparar tabla formal de casos de prueba
- [ ] Revisar feedback visual cuando el inventario está vacío
- [ ] Definir comportamiento funcional de la acción "Colocar"
- [ ] Decidir si la colocación de muebles será persistente en esta versión del TFG
- [ ] Preparar evidencias visuales del flujo completo para memoria y defensa
- [ ] Revisar manejo de errores en ShopActivity y TaskActivity
- [x] Revisar InventoryActivity cuando el inventario está vacío
- [x] Validar subida de nivel tras completar tarea
- [x] Probar persistencia tras cerrar y reabrir app
- [x] Confirmar que no hay recompensas dobles por pulsaciones repetidas

## Medio
- [x] Documentar en memoria el paso de texto plano a hash de contraseñas
- [x] Documentar el uso de SplashActivity y sesión persistente en memoria
- [x] Añadir tabla de casos de prueba con resultado esperado y resultado real
- [x] Consolidar el refactor de transacciones y validaciones en la documentación técnica
- [x] Documentar refactors técnicos recientes en memoria- 
- [ ] Añadir pruebas de flujo real para login, sesión persistente y registro- 
- [ ] Documentar la eliminación de fallbackToDestructiveMigration() y su impacto
- [ ] Revisar si el alta debe mostrar mensaje específico de auto-login exitoso
- [ ] Mover el texto del feedback de recompensa a strings.xml si aún queda algún literal pendiente
- [ ] Documentar en memoria la validación del flujo extremo a extremo
- [ ] Justificar en memoria el uso de datos seed para pruebas
- [ ] Revisar seeder con IDs asumidos
- [ ] Mejorar separación de responsabilidades de inventario en DAO
- [ ] Revisar datos seed y justificación de entorno de pruebas
- [ ] Consolidar documentación técnica para memoria

## Bajo
- [x] Limpiar nombres y consistencia de clases
- [ ] Limpiar configuración heredada de gradle.properties
- [ ] Separar ViewModel y Repository por features cuando el núcleo esté completamente estabilizado
- [ ] Preparar tablas de casos de prueba
- [ ] Mejorar documentación de arquitectura
- [ ] Preparar material de defensa asociado al refactor

