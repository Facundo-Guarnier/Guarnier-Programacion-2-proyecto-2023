package um.edu.prog2.guarnier.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrdenMapperTest {

    private OrdenMapper ordenMapper;

    @BeforeEach
    public void setUp() {
        ordenMapper = new OrdenMapperImpl();
    }
}
