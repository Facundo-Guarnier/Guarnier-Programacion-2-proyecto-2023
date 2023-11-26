package um.edu.prog2.guarnier.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@SpringBootTest
public class ReportarOperacionesServiceTest {

    @InjectMocks
    @Spy
    private ReportarOperacionesService ros;

    @Mock
    private CatedraAPIService csm;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void reportarOperaciones() throws Exception {
        doNothing().when(csm).postRoprtar(any(JsonNode.class));

        ros.reportarOperaciones(ordenes());

        verify(csm, times(1)).postRoprtar(any(JsonNode.class));
    }

    private List<OrdenDTO> ordenes() {
        List<OrdenDTO> listaDeOrdenes = new ArrayList<>();
        OrdenDTO o1 = new OrdenDTO();
        o1.setId(1L);
        o1.setCliente(26363);
        o1.setAccionId(1);
        o1.setAccion("APPL");
        o1.setOperacion("VENTA");
        o1.setModo("AHORA");
        o1.setFechaOperacion("2023-01-01T11:00:00Z");
        o1.setCantidad(9);
        o1.setPrecio(123.4F);
        o1.setEstado(3);
        o1.setDescripcion("");

        OrdenDTO o2 = new OrdenDTO();
        o1.setId(2L);
        o2.setCliente(26363);
        o2.setAccionId(1);
        o2.setAccion("APPL");
        o2.setOperacion("COMPRA");
        o2.setModo("AHORA");
        o2.setFechaOperacion("2023-01-01T11:00:00Z");
        o2.setCantidad(10);
        o2.setPrecio(123.4F);
        o2.setEstado(3);
        o2.setDescripcion("");

        listaDeOrdenes.add(o1);
        listaDeOrdenes.add(o2);
        return listaDeOrdenes;
    }
}
