package um.edu.prog2.guarnier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
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
    private OperadorDeOrdenesService oos;

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
        OrdenDTO ordenPendiente = new OrdenDTO();
        ordenPendiente.setCliente(26363);
        ordenPendiente.setAccionId(1);
        ordenPendiente.setAccion("APPL");
        ordenPendiente.setOperacion("COMPRA");
        ordenPendiente.setModo("AHORA");
        ordenPendiente.setFechaOperacion("2023-01-01T11:00:00Z");
        ordenPendiente.setCantidad(0);

        when(ordenService.findPendientes()).thenReturn(List.of(ordenPendiente));
        when(vos.puedeRealizarOperacion(ordenPendiente)).thenReturn(false);
        when(oos.noEsPosibleOperar(ordenPendiente)).thenReturn(ordenPendiente);

        //! Mocker para métodos void
        doNothing().when(ros).reportarOperaciones(anyList());

        List<List<OrdenDTO>> resultado = pos.procesarOrdenes();
        System.out.println(resultado);

        //! Verifica que el método fue llamado en orden
        InOrder inOrder = inOrder(ordenService, vos, ros);
        inOrder.verify(ordenService).findPendientes();
        inOrder.verify(vos).puedeRealizarOperacion(ordenPendiente);
        inOrder.verify(ros).reportarOperaciones(anyList());

        assertEquals(0, resultado.get(0).size());
        assertEquals(1, resultado.get(1).size());
    }

    @Test
    public void procesarOrdenes_ordenBuenaTest() throws Exception {
        //! Orden buena
        OrdenDTO ordenPendiente = new OrdenDTO();
        ordenPendiente.setCliente(26363);
        ordenPendiente.setAccionId(1);
        ordenPendiente.setAccion("APPL");
        ordenPendiente.setOperacion("COMPRA");
        ordenPendiente.setModo("AHORA");
        ordenPendiente.setFechaOperacion("2023-01-01T11:00:00Z");
        ordenPendiente.setCantidad(10);

        when(ordenService.findPendientes()).thenReturn(List.of(ordenPendiente));
        when(vos.puedeRealizarOperacion(ordenPendiente)).thenReturn(true);
        doNothing().when(ros).reportarOperaciones(anyList());
        when(oos.esPosibleOperar(ordenPendiente)).thenReturn(ordenPendiente);

        List<List<OrdenDTO>> resultado = pos.procesarOrdenes();
        System.out.println(resultado);

        //! Verifica que el método fue llamado en orden
        InOrder inOrder = inOrder(ordenService, vos, ros);
        inOrder.verify(ordenService).findPendientes();
        inOrder.verify(vos).puedeRealizarOperacion(ordenPendiente);
        inOrder.verify(ros).reportarOperaciones(anyList());

        assertEquals(1, resultado.get(0).size());
        assertEquals(0, resultado.get(1).size());
    }

    @Test
    public void cargarOrdenes_modo1Test() {
        when(cs.get(anyString())).thenReturn(mock(JsonNode.class));
        when(cs.getConJWT(anyString())).thenReturn(mock(JsonNode.class));
        doNothing().when(cs).postEspejo();
        doNothing().when(ordenService).guardarNuevas(any(JsonNode.class));

        //! Verificar que se hayan llamado los métodos necesarios
        verify(cs, times(1)).get(anyString());
        verify(cs, times(0)).getConJWT(anyString());
        verify(cs, times(0)).postEspejo();
        verify(ordenService, times(1)).guardarNuevas(any(JsonNode.class));
    }

    @Test
    public void cargarOrdenes_modo2Test() {
        when(cs.get(anyString())).thenReturn(mock(JsonNode.class));
        when(cs.getConJWT(anyString())).thenReturn(mock(JsonNode.class));
        doNothing().when(cs).postEspejo();
        doNothing().when(ordenService).guardarNuevas(any(JsonNode.class));

        //! Verificar que se hayan llamado los métodos necesarios
        verify(cs, times(0)).get(anyString());
        verify(cs, times(1)).getConJWT(anyString());
        verify(cs, times(0)).postEspejo();
        verify(ordenService, times(1)).guardarNuevas(any(JsonNode.class));
    }

    @Test
    public void cargarOrdenes_modo3Test() {
        when(cs.get(anyString())).thenReturn(mock(JsonNode.class));
        when(cs.getConJWT(anyString())).thenReturn(mock(JsonNode.class));
        doNothing().when(cs).postEspejo();
        doNothing().when(ordenService).guardarNuevas(any(JsonNode.class));

        //! Verificar que se hayan llamado los métodos necesarios
        verify(cs, times(0)).get(anyString());
        verify(cs, times(1)).getConJWT(anyString());
        verify(cs, times(1)).postEspejo();
        verify(ordenService, times(1)).guardarNuevas(any(JsonNode.class));
    }
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
    // public void testAnalizarOrdenes() throws Exception {
    //     OrdenDTO ordenPendiente1 = new OrdenDTO();
    //     OrdenDTO ordenPendiente = new OrdenDTO();
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
    //     ordenPendiente.setCliente(26363);
    //     ordenPendiente.setAccionId(1);
    //     ordenPendiente.setAccion("APPL");
    //     ordenPendiente.setOperacion("COMPRA");
    //     ordenPendiente.setModo("AHORA");
    //     ordenPendiente.setFechaOperacion("2023-01-01T11:00:00Z");
    //     ordenPendiente.setCantidad(0);
    //     ordenesPendientes.add(ordenPendiente);

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
