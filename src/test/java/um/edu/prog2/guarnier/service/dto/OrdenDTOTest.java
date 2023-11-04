package um.edu.prog2.guarnier.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import um.edu.prog2.guarnier.web.rest.TestUtil;

class OrdenDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrdenDTO.class);
        OrdenDTO ordenDTO1 = new OrdenDTO();
        ordenDTO1.setId(1L);
        OrdenDTO ordenDTO2 = new OrdenDTO();
        assertThat(ordenDTO1).isNotEqualTo(ordenDTO2);
        ordenDTO2.setId(ordenDTO1.getId());
        assertThat(ordenDTO1).isEqualTo(ordenDTO2);
        ordenDTO2.setId(2L);
        assertThat(ordenDTO1).isNotEqualTo(ordenDTO2);
        ordenDTO1.setId(null);
        assertThat(ordenDTO1).isNotEqualTo(ordenDTO2);
    }
}
