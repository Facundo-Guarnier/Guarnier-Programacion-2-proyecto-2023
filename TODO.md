**Preguntas**:

- Una orden debe tener asociado un cliente y una acción de una compañía. Se debe verificar que el Id de cliente y el Id de la acción sean válidos. Para esto se debe consultar el servicio cátedra buscando por Id de ambos.
  Problema: No se puede buscar por id (?id=98) devuelve cualquier cosa. Pasa con todo (acciones, clientes)

- Una orden no puede tener un número de acciones <=0. Para verificar este punto se deberá hacer una consulta a servicios de la cátedra.
  Problema: tengo que hacer un "if (cantidad > 0)" pero ¿Qué tiene que ver la consulta a la cátedra?

- Que funcione el .env

- ¿Qué hay que hacer cuando se programa una orden?

- En "ProcesamientoDeOrdenesService" tal vez conviene usar this.orden antes que ordenObj y tener que pasarlo por todas las funciones. tener en cuenta las ordenes programadas que puede pasar.

- Al hacer las listas de ordenes procesadas y fallidas, ¿tengo que guardar toda la orden on algún ID?

**TODO**:

[ ] Hacer que un hilo/proceso que arranque a las 9 o 18 para procesar las ordenes pendientes.
[ ] Hacer que el Servicio ReportarOperaciones envíe los resultados de las ordenes al endpoint de la cátedra.
[ ] Hacer en vez de un endpoint para procesar las ordenes, que se ejecute automáticamente en el main (Prog2App.java)

[x] Hacer que las ordenes se guarden en una BD y que de ahi las lea para procesar.
[x] Hacer un endpoint que se ejecute como si fuera el main que tengo ahora.
