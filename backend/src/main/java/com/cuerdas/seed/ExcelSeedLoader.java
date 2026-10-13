package com.cuerdas.seed;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cuerdas.domain.CuerdaSet;
import com.cuerdas.domain.CuerdaSetRepository;
import com.cuerdas.domain.CuerdaSetService;
import com.cuerdas.domain.TipoGuitarra;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

/**
 * Loads the data extracted from "Cuerdas Guitarra (2026).xlsx" (seed/cuerdas.json + seed/images)
 * the first time the application starts against an empty database.
 */
@Component
@ConditionalOnBooleanProperty(name = "app.seed.enabled")
public class ExcelSeedLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ExcelSeedLoader.class);

    record SeedItem(TipoGuitarra tipo, String descripcion, int cantidad, LocalDate fechaCompra,
                    LocalDate fechaCambio, String notas, String imagen) {
    }

    private final CuerdaSetRepository repository;
    private final CuerdaSetService service;
    private final ObjectMapper mapper;

    public ExcelSeedLoader(CuerdaSetRepository repository, CuerdaSetService service, ObjectMapper mapper) {
        this.repository = repository;
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws IOException {
        if (repository.count() > 0) {
            return;
        }
        List<SeedItem> items;
        try (InputStream in = new ClassPathResource("seed/cuerdas.json").getInputStream()) {
            items = mapper.readValue(in, new TypeReference<>() { });
        }
        for (SeedItem item : items) {
            CuerdaSet c = service.crear(item.tipo(), item.descripcion(), item.cantidad(),
                    item.fechaCompra(), item.fechaCambio(), item.notas());
            if (item.imagen() != null) {
                byte[] datos = new ClassPathResource("seed/images/" + item.imagen()).getContentAsByteArray();
                String contentType = item.imagen().endsWith(".png") ? "image/png" : "image/jpeg";
                service.guardarImagen(c.getId(), contentType, datos);
            }
        }
        log.info("Importados {} sets de cuerdas desde el Excel", items.size());
    }
}
