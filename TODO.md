**Preguntas**:

- "Una orden no puede tener un número de acciones <=0. Para verificar este punto se deberá hacer una consulta a servicios de la cátedra."
  Problema: tengo que hacer un "if (cantidad > 0)" pero ¿Qué tiene que ver la consulta a la cátedra?
  Solución: hacer literalmente un servicio "service" que devuelva true para comprar. Para vender, creo que falta un endpoint del profe para saber la cantidad de acciones que tiene un cliente.

- En Reporte hay que hacer un miapi que se pueda obtener las ordenes procesadas y poder filtrarlas (por IDs). Tener en cuenta el formato reporte-operaciones.

- Ver si puedo cambiar el estado de formato STRING a un INTEGER (0: pendiente, 1: programado, 2: vendido, 3: comprado)

- ✅ "Una orden debe tener asociado un cliente y una acción de una compañía."
  Se debe verificar que el Id de cliente y el Id de la acción sean válidos. Para esto se debe consultar el servicio cátedra buscando por Id de ambos."
  Problema: No se puede buscar por id (?id=98) devuelve cualquier cosa. Pasa con todo (acciones, clientes)
  Solución: Hay que traer todos los clientes y acciones y luego busco las Ids.

- ✅ ¿Que hay que hacer con los logs? Un txt, ELK, otro?
  Solución: Que aparezca el log por consola, pero que no sea un "System.out.println".

- ✅ Para las ordenes programadas tenemos que realizar la accion con los precios de FINDIA o PRINCIPIODIA. Hay que descartar el precio que viene con la orden.
  /api/acciones/ultimovalor/AAPL

- ✅ Hacer que el análisis de ordenes automatico (cada 10 segundos).

- ✅ Cambiar los POST de miapi a GET.

- ✅ Intentar separar lo mas posible los servicios. El servicio de procesamiento lo deberia separar en un procesamiento genérico (para AHORA y PROGRAMADO), un procesamiento de AHORA, y un procesamiento PROGRAMADO. El validar orden tal vez hay que separarlo en un servicio nuevo.

- ✅ Simplificar los endpoint para quitarle cualquier tipo de lógica de negocio que tengan, eso deberia estar en los servicios.

**TODO**:

[ ] Hacer que el Servicio ReportarOperaciones envíe los resultados de las ordenes al endpoint de la cátedra (Tener en cuenta el formato reporte-operaciones).

[ ] Hacer que verifica la cantidad de acciones para poder vender, tanto en AHORA como en PROGRAMADAS.
[ ] Miapi con JWT-
[ ] Hacer que se ejecuten los test, "mvn test" no los ejecuta.
[ ] Arreglar para que el JWT de la cátedra esté en un .env.
[ ] Hacer un endpoint para obtener reportes en base a filtros (cliente, accion, fecha, etc.)
[ ] Test
[ ] Seguridad

[x] Corregir en "ProcesamientoDeOrdenesService" que busca las acciones y clientes por "nombre" y no por "id".
[x] Hacer en vez de un endpoint para procesar las ordenes, que se ejecute automáticamente en el main (Prog2App.java)
[x] Hacer diagrama para endender mas fácil.
[x] Hacer que un hilo/proceso que arranque a las 9 o 18 para procesar las ordenes pendientes.
[x] Corregir el método getJWT de la catedraAPI.
[x] Hacer que las ordenes se guarden en una BD y que de ahi las lea para procesar.
[x] Hacer un endpoint que se ejecute como si fuera el main que tengo ahora.
