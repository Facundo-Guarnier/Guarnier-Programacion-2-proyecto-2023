package um.edu.prog2.guarnier.service;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@SpringBootTest
public class OperadorDeOrdenesServiceTest {

    //! Servicio a testear
    @InjectMocks
    @Spy
    private OperadorDeOrdenesService oos;

    //! Servicios dependientes
    @Mock
    private OrdenService ordenServicem;

    @Mock
    private CatedraAPIService csm;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void noEsPosibleOperarTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        OrdenDTO orden2 = oos.noEsPosibleOperar(orden);

        verify(ordenServicem).update(orden);
        assertEquals(orden, orden2);
    }

    @Test
    public void esPosibleOperar_SinPrecioTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setModo("AHORA");
        orden.setOperacion("cualquiera");
        orden.setPrecio(null); //* Sin precio

        when(csm.getConJWT(any(String.class))).thenReturn(precio());

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

        OrdenDTO orden2 = oos.esPosibleOperar(orden);
        assertNotNull(orden2);
        verify(oos, times(0)).cambiarPrecio(orden);
        verify(oos, times(1)).comprarOrden(any(OrdenDTO.class));
        verify(oos, times(0)).venderOrden(orden);
        verify(oos, times(0)).programarOrden(orden);
    }

    @Test
    public void esPosibleOperar_VenderTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setPrecio(123f);
        orden.setModo("AHORA");
        orden.setOperacion("VENTA");

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

        when(ordenServicem.update(orden)).thenReturn(orden);

        oos.programarOrden(orden);

        assertEquals(2, orden.getEstado());
        verify(ordenServicem).update(orden);
    }

    @Test
    public void venderOrdenTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setPrecio(123f);
        orden.setModo("AHORA");
        orden.setOperacion("VENTA");

        when(ordenServicem.update(orden)).thenReturn(orden);

        oos.venderOrden(orden);

        assertEquals(3, orden.getEstado());
        verify(ordenServicem).update(orden);
    }

    @Test
    public void comprarOrdenTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setPrecio(123f);
        orden.setModo("AHORA");
        orden.setOperacion("COMPRA");

        when(ordenServicem.update(orden)).thenReturn(orden);

        oos.comprarOrden(orden);

        assertEquals(3, orden.getEstado());
        verify(ordenServicem).update(orden);
    }

    private JsonNode precio() {
        try {
            String jsonString = "{ \"ultimoValor\": { \"valor\": 123.45 } }";

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonString);

            return jsonNode;
        } catch (Exception e) {
            return null;
        }
    }
}
