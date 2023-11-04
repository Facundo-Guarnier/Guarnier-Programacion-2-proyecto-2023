package um.edu.prog2.guarnier.service.dto;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class ListaOrdenesDTO implements Serializable {

    private List<OrdenDTO> ordenes;

    public List<OrdenDTO> getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(List<OrdenDTO> ordenes) {
        this.ordenes = ordenes;
    }
}
