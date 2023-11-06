**Preguntas**:

- "Una orden debe tener asociado un cliente y una acción de una compañía. Se debe verificar que el Id de cliente y el Id de la acción sean válidos. Para esto se debe consultar el servicio cátedra buscando por Id de ambos."
  Problema: No se puede buscar por id (?id=98) devuelve cualquier cosa. Pasa con todo (acciones, clientes)

- "Una orden no puede tener un número de acciones <=0. Para verificar este punto se deberá hacer una consulta a servicios de la cátedra."
  Problema: tengo que hacer un "if (cantidad > 0)" pero ¿Qué tiene que ver la consulta a la cátedra?

- ¿Qué hay que hacer cuando se programa una orden?

- ¿Como hago para simular la consulta a un endpoint del profe? ¿Con mockito?

**TODO**:

[ ] Arreglar para que el JWT de la cátedra esté en un .env.
[ ] Hacer que el Servicio ReportarOperaciones envíe los resultados de las ordenes al endpoint de la cátedra.
[ ] Hacer un endpoint para obtener reportes en base a filtros (cliente, accion, fecha, etc.)
[ ] Hacer en vez de un endpoint para procesar las ordenes, que se ejecute automáticamente en el main (Prog2App.java)
[ ] Test
[ ] Seguridad
[ ] Corregir en "ProcesamientoDeOrdenesService" que busca las acciones y clientes por "nombre" y no por "id".

[~] Hacer que un hilo/proceso que arranque a las 9 o 18 para procesar las ordenes pendientes.
No puedo hacer que empiece despues del retraso en milisegundos.

[x] Corregir el método getJWT de la catedraAPI.
[x] Hacer que las ordenes se guarden en una BD y que de ahi las lea para procesar.
[x] Hacer un endpoint que se ejecute como si fuera el main que tengo ahora.
