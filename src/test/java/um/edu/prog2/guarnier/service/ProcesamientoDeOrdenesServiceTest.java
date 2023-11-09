package um.edu.prog2.guarnier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@SpringBootTest
public class ProcesamientoDeOrdenesServiceTest {

    @Mock
    private ProcesamientoDeOrdenesService pos;

    @Mock
    private VerificadorDeOrdenesService vos;

    @Mock
    private ReportarOperacionesService ros;

    @Mock
    private OrdenService ordenService;

    @Mock
    private CatedraAPIService cs;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void procesarOrdenes_ordenMalaTest() throws Exception {
        //! Orden mala
        OrdenDTO ordenPendiente2 = new OrdenDTO();
        ordenPendiente2.setCliente(26363);
        ordenPendiente2.setAccionId(1);
        ordenPendiente2.setAccion("APPL");
        ordenPendiente2.setOperacion("COMPRA");
        ordenPendiente2.setModo("AHORA");
        ordenPendiente2.setFechaOperacion("2023-01-01T11:00:00Z");
        ordenPendiente2.setCantidad(0);

        //! Mockear el resultado de buscar clientes
        when(ordenService.findPendientes()).thenReturn(List.of(ordenPendiente2));
        when(vos.puedeRealizarOperacion(ordenPendiente2)).thenReturn(false);

        //! Mocker para métodos void
        doNothing().when(ros).reportarOperaciones(anyList(), anyList());

        List<List<OrdenDTO>> resultado = pos.procesarOrdenes();
        System.out.println(resultado);

        //! Verifica que el método fue llamado con la ordenPendiente2
        InOrder inOrder = inOrder(ordenService, vos, ros);
        inOrder.verify(ordenService).findPendientes();
        inOrder.verify(vos).puedeRealizarOperacion(ordenPendiente2);
        inOrder.verify(ros).reportarOperaciones(anyList(), anyList());

        assertEquals(1, resultado.get(1).size());
        assertEquals(0, resultado.get(0).size());
    }
    // @Test
    // public void PuedeRealizarOperacion_HoraFueraDeRangoTest() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setFechaOperacion("2023-01-01T08:00:00Z");
    //     boolean resultado = pos.puedeRealizarOperacion(orden);
    //     assertFalse(resultado);
    //     assertEquals("FALLIDO - HORA FUERA DE RANGO", orden.getEstado());
    // }

    // @Test
    // public void testPuedeRealizarOperacion_SinClienteAsociado() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setFechaOperacion("2023-01-01T10:00:00Z"); // Hora válida dentro del rango
    //     orden.setCliente(null);
    //     orden.setAccionId(4534);
    //     boolean resultado = pos.puedeRealizarOperacion(orden);
    //     // assertFalse(resultado);
    //     assertTrue(resultado);
    //     assertEquals("FALLIDO - SIN CLIENTE O ACCION ASOCIADA", orden.getEstado());
    // }

    // @Test
    // public void testPuedeRealizarOperacion_SinAccionAsociada() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setFechaOperacion("2023-01-01T10:00:00Z"); // Hora válida dentro del rango
    //     orden.setCliente(168);
    //     orden.setAccionId(null);
    //     boolean resultado = pos.puedeRealizarOperacion(orden);
    //     assertFalse(resultado);
    //     assertEquals("FALLIDO - SIN CLIENTE O ACCION ASOCIADA", orden.getEstado());
    // }

    // @Test
    // public void testPuedeRealizarOperacion_ClienteInvalido() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setFechaOperacion("2023-01-01T11:00:00Z"); // Hora válida dentro del rango
    //     orden.setAccionId(15);

    //     orden.setCliente(168); //! Cliente inválido

    //     //! Mockear el resultado de buscar clientes
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/clientes/buscar?nombre=Corvalan")).thenReturn(jsonClientes);

    //     boolean resultado = pos.puedeRealizarOperacion(orden);
    //     assertFalse(resultado);
    //     assertEquals("FALLIDO - CLIENTE NO VALIDO", orden.getEstado());
    // }

    // @Test
    // public void testPuedeRealizarOperacion_AccionInvalida1() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setFechaOperacion("2023-01-01T11:00:00Z");
    //     orden.setCliente(26363);

    //     //! Mockear el resultado de buscar clientes
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/clientes/buscar?nombre=Corvalan")).thenReturn(jsonClientes);

    //     orden.setAccionId(82); //! Acción inválida
    //     orden.setAccion("APPL");

    //     //! Mockear el resultado de buscar acciones
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/acciones/buscar?codigo=" + orden.getAccion())).thenReturn(jsonAcciones);

    //     boolean resultado = pos.puedeRealizarOperacion(orden);
    //     assertFalse(resultado);
    //     assertEquals("FALLIDO - ACCION NO VALIDA", orden.getEstado());
    // }

    // @Test
    // public void testPuedeRealizarOperacion_AccionInvalida2() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setFechaOperacion("2023-01-01T11:00:00Z");
    //     orden.setCliente(26363);
    //     orden.setAccionId(1);

    //     //! Mockear el resultado de buscar clientes
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/clientes/buscar?nombre=Corvalan")).thenReturn(jsonClientes);

    //     orden.setAccion("XYZ"); //! Acción inválida

    //     //! Mockear el resultado de buscar acciones
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/acciones/buscar?codigo=" + orden.getAccion())).thenReturn(jsonAcciones);

    //     boolean resultado = pos.puedeRealizarOperacion(orden);
    //     assertFalse(resultado);
    //     assertEquals("todo joya", orden.getEstado());
    // }

    // @Test
    // public void testPuedeRealizarOperacion_CantidadInvalida() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setFechaOperacion("2023-01-01T11:00:00Z");
    //     orden.setCliente(26363);
    //     orden.setAccionId(1);
    //     orden.setAccion("APPL");
    //     //! Mockear el resultado de buscar clientes
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/clientes/buscar?nombre=Corvalan")).thenReturn(jsonClientes);
    //     //! Mockear el resultado de buscar acciones
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/acciones/buscar?codigo=" + orden.getAccion())).thenReturn(jsonAcciones);

    //     orden.setCantidad(0); //! Cantidad inválida

    //     boolean resultado = pos.puedeRealizarOperacion(orden);
    //     assertFalse(resultado);
    //     assertEquals("FALLIDO - CANTIDAD DE ACCIONES MENOR O IGUAL A 0", orden.getEstado());
    // }

    // @Test
    // public void testPuedeRealizarOperacion_ModoInvalido() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setFechaOperacion("2023-01-01T11:00:00Z");
    //     orden.setCliente(26363);
    //     orden.setAccionId(1);
    //     orden.setAccion("APPL");
    //     orden.setCantidad(0);
    //     //! Mockear el resultado de buscar clientes
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/clientes/buscar?nombre=Corvalan")).thenReturn(jsonClientes);
    //     //! Mockear el resultado de buscar acciones
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/acciones/buscar?codigo=" + orden.getAccion())).thenReturn(jsonAcciones);

    //     orden.setModo("cualquiercosa"); //! Modo inválido

    //     boolean resultado = pos.puedeRealizarOperacion(orden);
    //     assertFalse(resultado);
    //     assertEquals("FALLIDO - MODO NO VALIDO", orden.getEstado());
    // }

    // @Test
    // public void AnalizarOrdenes_OperacionInvalidaTest() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setFechaOperacion("2023-01-01T11:00:00Z");
    //     orden.setCliente(26363);
    //     orden.setAccionId(1);
    //     orden.setAccion("APPL");
    //     orden.setCantidad(0);
    //     //! Mockear el resultado de buscar clientes
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/clientes/buscar?nombre=Corvalan")).thenReturn(jsonClientes);
    //     //! Mockear el resultado de buscar acciones
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/acciones/buscar?codigo=" + orden.getAccion())).thenReturn(jsonAcciones);

    //     orden.setOperacion("cualquiercosa"); //! Operacion inválida

    //     boolean resultado = pos.puedeRealizarOperacion(orden);
    //     assertFalse(resultado);
    //     assertEquals("FALLIDO - OPERACION NO VALIDA", orden.getEstado());
    // }

    // @Test
    // public void testPuedeRealizarOperacion_Valido() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setCliente(26363);
    //     orden.setAccionId(1);
    //     orden.setAccion("APPL");
    //     orden.setOperacion("COMPRA");
    //     orden.setModo("AHORA");
    //     orden.setFechaOperacion("2023-01-01T11:00:00Z");
    //     orden.setCantidad(2);
    //     //! Mockear el resultado de buscar clientes
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/clientes/buscar?nombre=Corvalan")).thenReturn(jsonClientes);
    //     //! Mockear el resultado de buscar acciones
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/acciones/buscar?codigo=" + orden.getAccion())).thenReturn(jsonAcciones);

    //     boolean resultado = pos.puedeRealizarOperacion(orden);
    //     assertTrue(resultado);
    //     assertEquals("PUEDE OPERAR", orden.getEstado());
    // }

    // @Test
    // public void testNoEsPosibleOperar() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     pos.ordenesFallidas.clear();
    //     pos.noEsPosibleOperar(orden);

    //     assert (pos.ordenesFallidas.contains(orden));

    //     //? No sé si estará bien este para "ordenService.update(orden);"
    //     Mockito.verify(ordenService).update(orden);
    // }

    // @Test
    // public void testEsPosibleOperar_Compra() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setOperacion("COMPRA");
    //     pos.esPosibleOperar(orden);
    //     assertEquals("COMPLETADO", orden.getEstado());
    // }

    // @Test
    // public void testEsPosibleOperar_Venta() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setOperacion("VENTA");
    //     pos.esPosibleOperar(orden);
    //     assertEquals("COMPLETADO", orden.getEstado());
    // }

    // @Test
    // public void testProgramarOrden() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     pos.programarOrden(orden);
    //     assertEquals("PROGRAMADO", orden.getEstado());

    //     //? No sé si estará bien este para "ordenService.update(orden);"
    //     Mockito.verify(ordenService).update(orden);
    // }

    // @Test
    // public void testVenderOrden() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     boolean resultado = pos.venderOrden(orden);
    //     assertTrue(resultado);
    //     assertEquals("COMPLETADO", orden.getEstado());

    //     //? No sé si estará bien este para "ordenService.update(orden);"
    //     Mockito.verify(ordenService).update(orden);
    // }

    // @Test
    // public void testComprarOrden() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     boolean resultado = pos.comprarOrden(orden);
    //     assertTrue(resultado);
    //     assertEquals("COMPLETADO", orden.getEstado());

    //     //? No sé si estará bien este para "ordenService.update(orden);"
    //     Mockito.verify(ordenService).update(orden);
    // }

    // @Test
    // public void testAnalizarOrdenes() throws Exception {
    //     OrdenDTO ordenPendiente1 = new OrdenDTO();
    //     OrdenDTO ordenPendiente2 = new OrdenDTO();
    //     List<OrdenDTO> ordenesPendientes = new ArrayList<>();

    //     //! Orden buena
    //     ordenPendiente1.setCliente(26363);
    //     ordenPendiente1.setAccionId(1);
    //     ordenPendiente1.setAccion("APPL");
    //     ordenPendiente1.setOperacion("COMPRA");
    //     ordenPendiente1.setModo("AHORA");
    //     ordenPendiente1.setFechaOperacion("2023-01-01T11:00:00Z");
    //     ordenPendiente1.setCantidad(2);
    //     ordenesPendientes.add(ordenPendiente1);

    //     //! Orden mala
    //     ordenPendiente2.setCliente(26363);
    //     ordenPendiente2.setAccionId(1);
    //     ordenPendiente2.setAccion("APPL");
    //     ordenPendiente2.setOperacion("COMPRA");
    //     ordenPendiente2.setModo("AHORA");
    //     ordenPendiente2.setFechaOperacion("2023-01-01T11:00:00Z");
    //     ordenPendiente2.setCantidad(0);
    //     ordenesPendientes.add(ordenPendiente2);

    //     //! Mockear el resultado de buscar clientes
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/clientes/buscar?nombre=Corvalan")).thenReturn(jsonClientes);
    //     //! Mockear el resultado de buscar acciones
    //     when(cs.getConJWT("http://192.168.194.254:8000/api/acciones/buscar?codigo=APPL")).thenReturn(jsonAcciones);

    //     //! Mockear el resultado de buscar ordenes pendientes
    //     when(ordenService.findPendientes()).thenReturn(ordenesPendientes);

    //     List<List<OrdenDTO>> resultado = pos.analizarOrdenes();

    //     List<OrdenDTO> ordenesProcesadas = resultado.get(0);
    //     List<OrdenDTO> ordenesFallidas = resultado.get(1);

    //     assertEquals(1, ordenesProcesadas.size());
    //     assertEquals(1, ordenesFallidas.size());
    // }
}
