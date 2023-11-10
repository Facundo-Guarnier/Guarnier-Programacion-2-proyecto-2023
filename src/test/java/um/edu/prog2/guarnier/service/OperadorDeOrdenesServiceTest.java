package um.edu.prog2.guarnier.service;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@SpringBootTest
public class OperadorDeOrdenesServiceTest {

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
    public void noEsPosibleOperarTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        OrdenDTO orden2 = oos.noEsPosibleOperar(orden);

        verify(ordenService).update(orden);
        assertEquals(orden, orden2);
    }

    @Test
    public void esPosibleOperar_SinPrecioTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();

        orden.setPrecio(null); //* Sin precio

        when(oos.cambiarPrecio(orden)).thenReturn(orden);

        OrdenDTO orden2 = oos.esPosibleOperar(orden);

        verify(oos, times(1)).cambiarPrecio(orden);
        verify(oos, times(0)).comprarOrden(orden);
        verify(oos, times(0)).venderOrden(orden);
        verify(oos, times(0)).programarOrden(orden);
        assertNull(orden2);
    }

    @Test
    public void esPosibleOperar_ComprarTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setPrecio(123f);
        orden.setModo("AHORA");
        orden.setOperacion("COMPRA");

        doNothing().when(oos).comprarOrden(orden);

        OrdenDTO orden2 = oos.esPosibleOperar(orden);

        verify(oos, times(0)).cambiarPrecio(orden);
        verify(oos, times(1)).comprarOrden(orden);
        verify(oos, times(0)).venderOrden(orden);
        verify(oos, times(0)).programarOrden(orden);
        assertNotNull(orden2);
    }

    @Test
    public void esPosibleOperar_VenderTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setPrecio(123f);
        orden.setModo("AHORA");
        orden.setOperacion("VENTA");

        doNothing().when(oos).venderOrden(orden);

        OrdenDTO orden2 = oos.esPosibleOperar(orden);

        verify(oos, times(0)).cambiarPrecio(orden);
        verify(oos, times(0)).comprarOrden(orden);
        verify(oos, times(1)).venderOrden(orden);
        verify(oos, times(0)).programarOrden(orden);
        assertNotNull(orden2);
    }

    @Test
    public void esPosibleOperar_ProgramarTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setPrecio(123f);
        orden.setModo("FINDIA");
        orden.setOperacion("VENTA");

        doNothing().when(oos).programarOrden(orden);

        OrdenDTO orden2 = oos.esPosibleOperar(orden);

        verify(oos, times(0)).cambiarPrecio(orden);
        verify(oos, times(0)).comprarOrden(orden);
        verify(oos, times(0)).venderOrden(orden);
        verify(oos, times(1)).programarOrden(orden);
        assertNotNull(orden2);
    }

    @Test
    public void programarOrdenTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();

        doNothing().when(ordenService).update(orden);

        oos.programarOrden(orden);

        assertEquals(2, orden.getEstado());
        verify(ordenService).update(orden);
    }

    @Test
    public void venderOrdenTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();

        doNothing().when(ordenService).update(orden);

        oos.programarOrden(orden);

        assertEquals(3, orden.getEstado());
        verify(ordenService).update(orden);
    }

    @Test
    public void comprarOrdenTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();

        doNothing().when(ordenService).update(orden);

        oos.programarOrden(orden);

        assertEquals(3, orden.getEstado());
        verify(ordenService).update(orden);
    }
}
