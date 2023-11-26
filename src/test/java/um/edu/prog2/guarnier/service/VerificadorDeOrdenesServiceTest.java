package um.edu.prog2.guarnier.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@SpringBootTest
public class VerificadorDeOrdenesServiceTest {

    @InjectMocks
    @Spy
    private VerificadorDeOrdenesService vos;

    @Mock
    private CatedraAPIService csm;

    private JsonNode jsonClientes;
    private JsonNode jsonAcciones;
    private JsonNode jsonClienteAccion;
    private ObjectMapper mapper;
    private String urlCliente;
    private String urlAccion;
    private String urlClienteAccion;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mapper = new ObjectMapper();
        setJsonClientes();
        setJsonAcciones();
        setJsonClienteAccion();
        urlCliente = "http://192.168.194.254:8000/api/clientes/buscar";
        urlAccion = "http://192.168.194.254:8000/api/acciones/buscar";
        urlClienteAccion = "http://192.168.194.254:8000/api/reporte-operaciones/consulta_cliente_accion?clienteId=26363&accionId=1";
    }

    @Test
    public void puedeRealizarOperacion_HoraFueraDeRangoTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setModo("AHORA");

        orden.setFechaOperacion("2023-01-01T08:00:00Z"); //* Hora inválida

        boolean resultado = vos.puedeRealizarOperacion(orden);
        assertFalse(resultado);
        assertEquals(1, orden.getEstado());
        assertEquals("HORA FUERA DE RANGO", orden.getDescripcion());
    }

    @Test
    public void puedeRealizarOperacion_SinClienteAsociadoTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setModo("AHORA");
        orden.setFechaOperacion("2023-01-01T10:00:00Z");
        orden.setAccionId(4534);

        orden.setCliente(null); //* Cliente inválido

        boolean resultado = vos.puedeRealizarOperacion(orden);
        assertFalse(resultado);
        assertEquals(1, orden.getEstado());
        assertEquals("SIN CLIENTE O ACCION ASOCIADA", orden.getDescripcion());
    }

    @Test
    public void puedeRealizarOperacion_SinAccionAsociadaTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setModo("AHORA");
        orden.setFechaOperacion("2023-01-01T10:00:00Z");
        orden.setCliente(168);

        orden.setAccionId(null); //* Acción inválida

        boolean resultado = vos.puedeRealizarOperacion(orden);
        assertFalse(resultado);
        assertEquals(1, orden.getEstado());
        assertEquals("SIN CLIENTE O ACCION ASOCIADA", orden.getDescripcion());
    }

    @Test
    public void uedeRealizarOperacion_ClienteInvalidoTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setModo("AHORA");
        orden.setFechaOperacion("2023-01-01T11:00:00Z");
        orden.setAccionId(1);

        orden.setCliente(1234); //* Cliente inválido

        when(csm.getConJWT(urlCliente)).thenReturn(jsonClientes);

        boolean resultado = vos.puedeRealizarOperacion(orden);
        assertFalse(resultado);
        assertEquals(1, orden.getEstado());
        assertEquals("CLIENTE NO VALIDO", orden.getDescripcion());
    }

    @Test
    public void puedeRealizarOperacion_AccionIdInvalidaTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setModo("AHORA");
        orden.setFechaOperacion("2023-01-01T11:00:00Z");
        orden.setCliente(26363);
        orden.setAccion("APPL");

        when(csm.getConJWT(urlCliente)).thenReturn(jsonClientes);
        when(csm.getConJWT(urlAccion)).thenReturn(jsonAcciones);

        orden.setAccionId(1234); //* Acción id inválida

        boolean resultado = vos.clienteAccionAsociados(orden);
        assertFalse(resultado);
        assertEquals(1, orden.getEstado());
        assertEquals("ACCION ID Y ACCION NO VALIDOS", orden.getDescripcion());
    }

    @Test
    public void puedeRealizarOperacion_AccionCodigoInvalidoTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setModo("AHORA");
        orden.setFechaOperacion("2023-01-01T11:00:00Z");
        orden.setCliente(26363);
        orden.setAccionId(1);

        when(csm.getConJWT(urlCliente)).thenReturn(jsonClientes);
        when(csm.getConJWT(urlAccion)).thenReturn(jsonAcciones);

        orden.setAccion("abcd"); //* Acción código inválido

        boolean resultado = vos.puedeRealizarOperacion(orden);
        assertFalse(resultado);
        assertEquals(1, orden.getEstado());
        assertEquals("ACCION ID Y ACCION NO VALIDOS", orden.getDescripcion());
    }

    @Test
    public void puedeRealizarOperacion_CantidadNulaTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setModo("AHORA");
        orden.setFechaOperacion("2023-01-01T11:00:00Z");
        orden.setCliente(26363);
        orden.setAccionId(1);
        orden.setAccion("APPL");

        when(csm.getConJWT(urlCliente)).thenReturn(jsonClientes);
        when(csm.getConJWT(urlAccion)).thenReturn(jsonAcciones);

        orden.setCantidad(0); //* Cantidad inválida

        boolean resultado = vos.puedeRealizarOperacion(orden);
        assertFalse(resultado);
        assertEquals(1, orden.getEstado());
        assertEquals("CANTIDAD DE ACCIONES MENOR O IGUAL A 0", orden.getDescripcion());
    }

    @Test
    public void puedeRealizarOperacion_CantidadInvalidaTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setModo("AHORA");
        orden.setFechaOperacion("2023-01-01T11:00:00Z");
        orden.setCliente(26363);
        orden.setAccionId(1);
        orden.setAccion("APPL");
        orden.setOperacion("VENTA");

        when(csm.getConJWT(urlCliente)).thenReturn(jsonClientes);
        when(csm.getConJWT(urlAccion)).thenReturn(jsonAcciones);
        when(csm.getConJWT(urlClienteAccion)).thenReturn(jsonClienteAccion);

        orden.setCantidad(10); //* Cantidad inválida

        boolean resultado = vos.puedeRealizarOperacion(orden);
        assertFalse(resultado);
        assertEquals(1, orden.getEstado());
        assertEquals("CANTIDAD DE ACCIONES INSUFICIENTE", orden.getDescripcion());
    }

    @Test
    public void puedeRealizarOperacion_ModoInvalidoTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setFechaOperacion("2023-01-01T11:00:00Z");
        orden.setCliente(26363);
        orden.setAccionId(1);
        orden.setAccion("APPL");
        orden.setCantidad(2);

        when(csm.getConJWT(urlCliente)).thenReturn(jsonClientes);
        when(csm.getConJWT(urlAccion)).thenReturn(jsonAcciones);

        orden.setModo("cualquiercosa"); //* Modo inválido

        boolean resultado = vos.puedeRealizarOperacion(orden);
        assertFalse(resultado);
        assertEquals(1, orden.getEstado());
        assertEquals("MODO NO VALIDO", orden.getDescripcion());
    }

    @Test
    public void puedeRealizarOperacion_OperacioninvalidaTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setModo("AHORA");
        orden.setFechaOperacion("2023-01-01T11:00:00Z");
        orden.setCliente(26363);
        orden.setAccionId(1);
        orden.setAccion("APPL");
        orden.setCantidad(2);
        orden.setModo("AHORA");

        when(csm.getConJWT(urlCliente)).thenReturn(jsonClientes);
        when(csm.getConJWT(urlAccion)).thenReturn(jsonAcciones);

        orden.setOperacion("cualquiercosa"); //* Operación válida

        boolean resultado = vos.puedeRealizarOperacion(orden);
        assertFalse(resultado);
        assertEquals(1, orden.getEstado());
        assertEquals("OPERACION NO VALIDA", orden.getDescripcion());
    }

    @Test
    public void puedeRealizarOperacion_ValidoTest() throws Exception {
        OrdenDTO orden = new OrdenDTO();
        orden.setModo("AHORA");
        orden.setFechaOperacion("2023-01-01T11:00:00Z");
        orden.setCliente(26363);
        orden.setAccionId(1);
        orden.setAccion("APPL");
        orden.setCantidad(2);
        orden.setModo("AHORA");
        orden.setOperacion("COMPRA");

        when(csm.getConJWT(urlCliente)).thenReturn(jsonClientes);
        when(csm.getConJWT(urlAccion)).thenReturn(jsonAcciones);
        when(csm.getConJWT(urlClienteAccion)).thenReturn(jsonClienteAccion);

        boolean resultado = vos.puedeRealizarOperacion(orden);
        assertTrue(resultado);
        assertEquals(null, orden.getEstado());
        assertEquals(null, orden.getDescripcion());
    }

    private void setJsonClientes() {
        //! Simular el resultado de buscar clientes
        ObjectNode cliente1 = mapper.createObjectNode();
        cliente1.put("id", 26363);
        cliente1.put("nombreApellido", "María Corvalán");
        cliente1.put("empresa", "Happy Soul");

        ObjectNode cliente2 = mapper.createObjectNode();
        cliente2.put("id", 26364);
        cliente2.put("nombreApellido", "Ricardo Tapia");
        cliente2.put("empresa", "Salud Zen");

        ArrayNode clientesArray = mapper.createArrayNode();
        clientesArray.add(cliente1);
        clientesArray.add(cliente2);

        ObjectNode objectNode1 = mapper.createObjectNode();
        objectNode1.set("clientes", clientesArray);
        jsonClientes = objectNode1;
    }

    private void setJsonAcciones() {
        //! Simular el resultado de buscar acciones
        ObjectNode accion1 = mapper.createObjectNode();
        accion1.put("id", 1);
        accion1.put("codigo", "APPL");
        accion1.put("empresa", "Apple Inc.");

        ObjectNode accion2 = mapper.createObjectNode();
        accion2.put("id", 2);
        accion2.put("codigo", "GOOGL");
        accion2.put("empresa", "Alphabet Inc. (google)");

        ArrayNode accionesArray = mapper.createArrayNode();
        accionesArray.add(accion1);
        accionesArray.add(accion2);

        ObjectNode objectNode2 = mapper.createObjectNode();
        objectNode2.set("acciones", accionesArray);

        jsonAcciones = objectNode2;
    }

    private void setJsonClienteAccion() {
        ObjectNode jsonNode = JsonNodeFactory.instance.objectNode();

        jsonNode.put("cliente", 26363);
        jsonNode.put("accionId", 1);
        jsonNode.put("accion", "APPL");
        jsonNode.put("cantidadActual", 1);
        jsonNode.put("observaciones", "Acciones presentes");

        jsonClienteAccion = jsonNode;
    }
}
