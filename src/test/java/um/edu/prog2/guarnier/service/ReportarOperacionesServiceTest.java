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
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@SpringBootTest
public class ReportarOperacionesServiceTest {

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
    public void procesarOrdenes_OrdenValidaTest() {
        OrdenDTO orden = new OrdenDTO();

        when(ordenService.findPendientes()).thenReturn(Collections.singletonList(orden));
        when(vos.puedeRealizarOperacion(orden)).thenReturn(true);
        when(oos.esPosibleOperar(orden)).thenReturn(orden);
        doNothing().when(ros).reportarOperaciones(anyList(), anyList());

        List<List<OrdenDTO>> resultado = pos.procesarOrdenes();

        verify(ordenService, times(1)).findPendientes();
        verify(vos, times(1)).puedeRealizarOperacion(orden);
        verify(oos, times(1)).esPosibleOperar(orden);
        verify(oos, times(0)).noEsPosibleOperar(orden);
        verify(ros, times(1)).reportarOperaciones(anyList(), anyList());
        assertEquals(orden, resultado.get(0).get(0));
        assertEquals(0, resultado.get(1).size());
    }

    @Test
    public void procesarOrdenes_OrdenInvalidaTest() {
        OrdenDTO orden = new OrdenDTO();

        when(ordenService.findPendientes()).thenReturn(Collections.singletonList(orden));
        when(vos.puedeRealizarOperacion(orden)).thenReturn(false);
        when(oos.noEsPosibleOperar(orden)).thenReturn(orden);
        doNothing().when(ros).reportarOperaciones(anyList(), anyList());

        List<List<OrdenDTO>> resultado = pos.procesarOrdenes();

        verify(ordenService, times(1)).findPendientes();
        verify(vos, times(1)).puedeRealizarOperacion(orden);
        verify(oos, times(0)).esPosibleOperar(orden);
        verify(oos, times(1)).noEsPosibleOperar(orden);
        verify(ros, times(1)).reportarOperaciones(anyList(), anyList());
        assertEquals(orden, resultado.get(1).get(0));
        assertEquals(0, resultado.get(0).size());
    }

    @Test
    public void cargarOrdenes_Modo1Test() {
        when(cs.get(anyString())).thenReturn(mock(JsonNode.class));
        doNothing().when(ordenService).guardarNuevas(any(JsonNode.class));

        pos.cargarOrdenes(1);

        verify(cs, times(1)).get(anyString());
        verify(ordenService, times(1)).guardarNuevas(mock(JsonNode.class));
    }

    @Test
    public void cargarOrdenes_Modo2Test() {
        when(cs.getConJWT(anyString())).thenReturn(mock(JsonNode.class));
        doNothing().when(ordenService).guardarNuevas(any(JsonNode.class));

        pos.cargarOrdenes(2);

        verify(cs, times(1)).getConJWT(anyString());
        verify(ordenService, times(1)).guardarNuevas(mock(JsonNode.class));
    }

    @Test
    public void cargarOrdenes_Modo3Test() {
        when(cs.getConJWT(anyString())).thenReturn(mock(JsonNode.class));
        doNothing().when(ordenService).guardarNuevas(any(JsonNode.class));
        doNothing().when(cs).postEspejo();

        pos.cargarOrdenes(3);

        verify(cs, times(1)).getConJWT(anyString());
        verify(ordenService, times(1)).guardarNuevas(mock(JsonNode.class));
        verify(cs, times(1)).postEspejo();
    }
}
