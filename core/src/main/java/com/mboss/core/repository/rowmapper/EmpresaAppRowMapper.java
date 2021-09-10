package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.EmpresaApp;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link EmpresaApp}, with proper type conversions.
 */
@Service
public class EmpresaAppRowMapper implements BiFunction<Row, String, EmpresaApp> {

    private final ColumnConverter converter;

    public EmpresaAppRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link EmpresaApp} stored in the database.
     */
    @Override
    public EmpresaApp apply(Row row, String prefix) {
        EmpresaApp entity = new EmpresaApp();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRazaoSocial(converter.fromRow(row, prefix + "_razao_social", String.class));
        entity.setNomeFantasia(converter.fromRow(row, prefix + "_nome_fantasia", String.class));
        return entity;
    }
}
