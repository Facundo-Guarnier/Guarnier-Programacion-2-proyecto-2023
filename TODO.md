**Preguntas**:

- ✅ "Una orden no puede tener un número de acciones <=0. Para verificar este punto se deberá hacer una consulta a servicios de la cátedra."
  Problema: tengo que hacer un "if (cantidad > 0)" pero ¿Qué tiene que ver la consulta a la cátedra?
  Para vender, creo que falta un endpoint en la cátedra para saber la cantidad de acciones que tiene un cliente.

- ✅ Problema: Zona horaria. Hasta el punto en donde se guarda en la DB (OrdenService.save()) la orden mantiene la UTC. El problema está en OrdenRepository, el JPA parece que transforma de UTC a UTC-3 al momento de leer en la DB. Es un problema de lectura y no de escritura en la DB.
  Solución: Cambiar el tipo de fecha a "Instant".

- ✅ Problema: ¿Hay que reportar las ordenes fallidas al POST de la cátedra? El POST de reportar creo que tiene que ser solo con las ordenes buenas, ya que las fallidas no son necesarias para actualizar la cantidad de acciones que tiene cada cliente.

- ✅ Ver si puedo cambiar el estado de formato STRING a un INTEGER (0: pendiente, 1: programado, 2: vendido, 3: comprado)

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

[ ]

[x] Mejorar los tests. "doNothing" solo funciona con metodos void.
[x] Borrar todos los prints.
[x] Corregir los tipos de logs. error, debug, info
[x] Hacer reportes a la cátedra.
[x] Consultar cantidad de acciones a la cátedra.
[x] Hacer que las ordenes programadas no revisen la cantidad de la accion hasta el momento de iniciodia o findia.
[x] Arreglar la busqueda por filtro de fecha de los reportes locales.
[x] Seguridad: Miapi con JWT.
[x] Logs en la parte nueva de reportes.
[x] Hacer un endpoint para obtener reportes en base a filtros (cliente, accion, fecha, etc.)
[x] Hacer uso del espejo
[x] Hacer que el Servicio ReportarOperaciones envíe los resultados de las ordenes al endpoint de la cátedra (Tener en cuenta el formato reporte-operaciones).
[x] Corregir en "ProcesamientoDeOrdenesService" que busca las acciones y clientes por "nombre" y no por "id".
[x] Hacer en vez de un endpoint para procesar las ordenes, que se ejecute automáticamente en el main (Prog2App.java)
[x] Hacer diagrama para endender mas fácil.
[x] Hacer que un hilo/proceso que arranque a las 9 o 18 para procesar las ordenes pendientes.
[x] Corregir el método getJWT de la catedraAPI.
[x] Hacer que las ordenes se guarden en una BD y que de ahi las lea para procesar.
[x] Hacer un endpoint que se ejecute como si fuera el main que tengo ahora.

npm run app:start
mvn test
mvn -Dtest=ProcesamientoDeOrdenesServiceTest test

0. PENDIENTE
1. FALLIDO
2. PROGRAMADO
3. COMPLETADO

{
"cliente": 26370,
"accionId": 3,
"accion": "INTC",
"operacion": "COMPRA",
"precio": null,
"cantidad": 0,
"fechaOperacion": "2023-11-08T13:00:00Z",
"modo": "AHORA"
},
{
"cliente": 999999,
"accionId": 3,
"accion": "INTC",
"operacion": "COMPRA",
"precio": null,
"cantidad": 10,
"fechaOperacion": "2023-11-08T13:00:00Z",
"modo": "AHORA"
},
{
"cliente": 26370,
"accionId": 1,
"accion": "CualquierCosa",
"operacion": "VENTA",
"precio": null,
"cantidad": 5,
"fechaOperacion": "2023-11-09T03:00:00Z",
"modo": "FINDIA"
},
{
"cliente": 26370,
"accionId": 4,
"accion": "KO",
"operacion": "COMPRA",
"precio": null,
"cantidad": 80,
"fechaOperacion": "2023-11-10T20:30:00Z",
"modo": "AHORA"
},
{
"cliente": 26370,
"accionId": 6,
"accion": "YPF",
"operacion": "VENTA",
"precio": null,
"cantidad": 999999,
"fechaOperacion": "2023-11-10T12:00:00Z",
"modo": "AHORA"
},
{
"cliente": 26370,
"accionId": 6,
"accion": "YPF",
"operacion": "COMPRA",
"precio": null,
"cantidad": 2,
"fechaOperacion": "2023-11-10T12:00:00Z",
"modo": "AHORA"
},
{
"cliente": 26370,
"accionId": 6,
"accion": "YPF",
"operacion": "VENTA",
"precio": null,
"cantidad": 1,
"fechaOperacion": "2023-11-10T12:00:00Z",
"modo": "AHORA"
},
{
"cliente": 26370,
"accionId": 6,
"accion": "YPF",
"operacion": "COMPRA",
"precio": null,
"cantidad": 1,
"fechaOperacion": "2023-11-10T17:00:00Z",
"modo": "FINDIA"
},
{
"cliente": 26370,
"accionId": 6,
"accion": "YPF",
"operacion": "COMPRA",
"precio": null,
"cantidad": 5,
"fechaOperacion": "2023-11-12T03:00:00Z",
"modo": "PRINCIPIODIA"
}
