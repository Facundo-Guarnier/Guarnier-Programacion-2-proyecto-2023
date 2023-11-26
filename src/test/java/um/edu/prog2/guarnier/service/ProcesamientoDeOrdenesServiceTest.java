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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@SpringBootTest
public class ProcesamientoDeOrdenesServiceTest {

    @InjectMocks
    @Spy
    private ProcesamientoDeOrdenesService pos;

    @Mock
    private VerificadorDeOrdenesService vosm;

    @Mock
    private ReportarOperacionesService rosm;

    @Mock
    private OperadorDeOrdenesService oosm;

    @Mock
    private OrdenService ordenServicem;

    @Mock
    private CatedraAPIService csm;

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

        when(ordenServicem.findPendientes()).thenReturn(List.of(ordenPendiente));
        when(vosm.puedeRealizarOperacion(ordenPendiente)).thenReturn(false);
        when(oosm.noEsPosibleOperar(ordenPendiente)).thenReturn(ordenPendiente);

        //! Mocker para métodos void
        doNothing().when(rosm).reportarOperaciones(anyList());

        List<List<OrdenDTO>> resultado = pos.procesarOrdenes();
        System.out.println(resultado);

        //! Verifica que el método fue llamado en orden
        InOrder inOrder = inOrder(ordenServicem, vosm, rosm);
        inOrder.verify(ordenServicem).findPendientes();
        inOrder.verify(vosm).puedeRealizarOperacion(ordenPendiente);
        inOrder.verify(rosm).reportarOperaciones(anyList());

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

        when(ordenServicem.findPendientes()).thenReturn(List.of(ordenPendiente));
        when(vosm.puedeRealizarOperacion(ordenPendiente)).thenReturn(true);
        doNothing().when(rosm).reportarOperaciones(anyList());
        when(oosm.esPosibleOperar(ordenPendiente)).thenReturn(ordenPendiente);

        List<List<OrdenDTO>> resultado = pos.procesarOrdenes();
        System.out.println(resultado);

        //! Verifica que el método fue llamado en orden
        InOrder inOrder = inOrder(ordenServicem, vosm, rosm);
        inOrder.verify(ordenServicem).findPendientes();
        inOrder.verify(vosm).puedeRealizarOperacion(ordenPendiente);
        inOrder.verify(rosm).reportarOperaciones(anyList());

        assertEquals(1, resultado.get(0).size());
        assertEquals(0, resultado.get(1).size());
    }

    @Test
    public void cargarOrdenes_modo1Test() {
        when(csm.get(anyString())).thenReturn(mock(JsonNode.class));
        when(csm.getConJWT(anyString())).thenReturn(mock(JsonNode.class));
        doNothing().when(csm).postEspejo();
        doNothing().when(ordenServicem).guardarNuevas(any(JsonNode.class));

        pos.cargarOrdenes(1);

        //! Verificar que se hayan llamado los métodos necesarios
        verify(csm, times(1)).get(anyString());
        verify(csm, times(0)).getConJWT(anyString());
        verify(csm, times(0)).postEspejo();
        verify(ordenServicem, times(1)).guardarNuevas(any(JsonNode.class));
    }

    @Test
    public void cargarOrdenes_modo2Test() {
        when(csm.get(anyString())).thenReturn(mock(JsonNode.class));
        when(csm.getConJWT(anyString())).thenReturn(mock(JsonNode.class));
        doNothing().when(csm).postEspejo();
        doNothing().when(ordenServicem).guardarNuevas(any(JsonNode.class));

        pos.cargarOrdenes(2);

        //! Verificar que se hayan llamado los métodos necesarios
        verify(csm, times(0)).get(anyString());
        verify(csm, times(1)).getConJWT(anyString());
        verify(csm, times(0)).postEspejo();
        verify(ordenServicem, times(1)).guardarNuevas(any(JsonNode.class));
    }

    @Test
    public void cargarOrdenes_modo3Test() {
        when(csm.get(anyString())).thenReturn(mock(JsonNode.class));
        when(csm.getConJWT(anyString())).thenReturn(mock(JsonNode.class));
        doNothing().when(csm).postEspejo();
        doNothing().when(ordenServicem).guardarNuevas(any(JsonNode.class));

        pos.cargarOrdenes(3);

        //! Verificar que se hayan llamado los métodos necesarios
        verify(csm, times(0)).get(anyString());
        verify(csm, times(1)).getConJWT(anyString());
        verify(csm, times(1)).postEspejo();
        verify(ordenServicem, times(1)).guardarNuevas(any(JsonNode.class));
    }
}
