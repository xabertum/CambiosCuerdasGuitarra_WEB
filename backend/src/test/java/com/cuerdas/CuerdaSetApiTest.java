package com.cuerdas;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CuerdaSetApiTest {

    @Autowired
    MockMvc mvc;

    @Test
    void importaElExcelAlArrancar() throws Exception {
        mvc.perform(get("/api/cuerdas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(25))
                .andExpect(jsonPath("$[?(@.descripcion =~ /Orphee.*/)].cantidad").value(5));
    }

    @Test
    void creaSubeImagenYBorra() throws Exception {
        String body = """
                {"tipo":"ELECTRICA","descripcion":"  Ernie Ball Regular Slinky 10-46 ","fechaCompra":"2026-10-13"}
                """;
        String json = mvc.perform(post("/api/cuerdas").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.descripcion").value("Ernie Ball Regular Slinky 10-46"))
                .andExpect(jsonPath("$.cantidad").value(1))
                .andExpect(jsonPath("$.imagenUrl").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        int id = JsonPath.read(json, "$.id");

        byte[] png = {(byte) 0x89, 'P', 'N', 'G'};
        mvc.perform(multipart(HttpMethod.PUT, "/api/cuerdas/" + id + "/imagen")
                        .file(new MockMultipartFile("archivo", "set.png", "image/png", png)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imagenUrl").isString());
        mvc.perform(get("/api/cuerdas/" + id + "/imagen"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(png));

        mvc.perform(delete("/api/cuerdas/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/cuerdas/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void validaLaPeticion() throws Exception {
        mvc.perform(post("/api/cuerdas").contentType(MediaType.APPLICATION_JSON).content("{\"descripcion\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
