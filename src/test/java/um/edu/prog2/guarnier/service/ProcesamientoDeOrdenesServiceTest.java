package um.edu.prog2.guarnier.service;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import um.edu.prog2.guarnier.IntegrationTest;
import um.edu.prog2.guarnier.service.dto.OrdenDTO;

@SpringBootTest
public class ProcesamientoDeOrdenesServiceTest {

    private ProcesamientoDeOrdenesService procesamientoDeOrdenesService;
    private CatedraAPIService cs;
    private OrdenService ordenService;
    private JsonNode jsonClientes;
    private JsonNode jsonAcciones;
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        cs = Mockito.mock(CatedraAPIService.class);
        ordenService = Mockito.mock(OrdenService.class);
        mapper = new ObjectMapper();

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

        //! Simular el resultado de buscar acciones
        ObjectNode accion1 = mapper.createObjectNode();
        accion1.put("id", 1);
        accion1.put("codigo", "AAPL");
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
    // @Test
    // public void PuedeRealizarOperacion_HoraFueraDeRangoTest() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setFechaOperacion("2023-01-01T08:00:00Z");
    //     boolean resultado = procesamientoDeOrdenesService.puedeRealizarOperacion(orden);
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
    //     boolean resultado = procesamientoDeOrdenesService.puedeRealizarOperacion(orden);
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
    //     boolean resultado = procesamientoDeOrdenesService.puedeRealizarOperacion(orden);
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

    //     boolean resultado = procesamientoDeOrdenesService.puedeRealizarOperacion(orden);
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

    //     boolean resultado = procesamientoDeOrdenesService.puedeRealizarOperacion(orden);
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

    //     boolean resultado = procesamientoDeOrdenesService.puedeRealizarOperacion(orden);
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

    //     boolean resultado = procesamientoDeOrdenesService.puedeRealizarOperacion(orden);
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

    //     boolean resultado = procesamientoDeOrdenesService.puedeRealizarOperacion(orden);
    //     assertFalse(resultado);
    //     assertEquals("FALLIDO - MODO NO VALIDO", orden.getEstado());
    // }

    // @Test
    // public void testAnalizarOrdenes_OperacionInvalida() throws Exception {
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

    //     boolean resultado = procesamientoDeOrdenesService.puedeRealizarOperacion(orden);
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

    //     boolean resultado = procesamientoDeOrdenesService.puedeRealizarOperacion(orden);
    //     assertTrue(resultado);
    //     assertEquals("PUEDE OPERAR", orden.getEstado());
    // }

    // @Test
    // public void testNoEsPosibleOperar() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     procesamientoDeOrdenesService.ordenesFallidas.clear();
    //     procesamientoDeOrdenesService.noEsPosibleOperar(orden);

    //     assert (procesamientoDeOrdenesService.ordenesFallidas.contains(orden));

    //     //? No sé si estará bien este para "ordenService.update(orden);"
    //     Mockito.verify(ordenService).update(orden);
    // }

    // @Test
    // public void testEsPosibleOperar_Compra() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setOperacion("COMPRA");
    //     procesamientoDeOrdenesService.esPosibleOperar(orden);
    //     assertEquals("COMPLETADO", orden.getEstado());
    // }

    // @Test
    // public void testEsPosibleOperar_Venta() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     orden.setModo("AHORA");
    //     orden.setOperacion("VENTA");
    //     procesamientoDeOrdenesService.esPosibleOperar(orden);
    //     assertEquals("COMPLETADO", orden.getEstado());
    // }

    // @Test
    // public void testProgramarOrden() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     procesamientoDeOrdenesService.programarOrden(orden);
    //     assertEquals("PROGRAMADO", orden.getEstado());

    //     //? No sé si estará bien este para "ordenService.update(orden);"
    //     Mockito.verify(ordenService).update(orden);
    // }

    // @Test
    // public void testVenderOrden() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     boolean resultado = procesamientoDeOrdenesService.venderOrden(orden);
    //     assertTrue(resultado);
    //     assertEquals("COMPLETADO", orden.getEstado());

    //     //? No sé si estará bien este para "ordenService.update(orden);"
    //     Mockito.verify(ordenService).update(orden);
    // }

    // @Test
    // public void testComprarOrden() throws Exception {
    //     OrdenDTO orden = new OrdenDTO();
    //     boolean resultado = procesamientoDeOrdenesService.comprarOrden(orden);
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

    //     List<List<OrdenDTO>> resultado = procesamientoDeOrdenesService.analizarOrdenes();

    //     List<OrdenDTO> ordenesProcesadas = resultado.get(0);
    //     List<OrdenDTO> ordenesFallidas = resultado.get(1);

    //     assertEquals(1, ordenesProcesadas.size());
    //     assertEquals(1, ordenesFallidas.size());
    // }
}
