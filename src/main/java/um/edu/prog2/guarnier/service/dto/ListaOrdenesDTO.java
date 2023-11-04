package um.edu.prog2.guarnier.service.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import um.edu.prog2.guarnier.domain.Orden;

@SuppressWarnings("common-java:DuplicatedBlocks")
public class ListaOrdenesDTO implements Serializable {

    private List<Orden> ordenes;

    public List<Orden> getOrdenes() {
        return ordenes;
    }

    public void setOrdenes(List<Orden> ordenes) {
        this.ordenes = ordenes;
    }
}
