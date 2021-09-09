package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.CriacaoEspecie;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CriacaoEspecie}, with proper type conversions.
 */
@Service
public class CriacaoEspecieRowMapper implements BiFunction<Row, String, CriacaoEspecie> {

    private final ColumnConverter converter;

    public CriacaoEspecieRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CriacaoEspecie} stored in the database.
     */
    @Override
    public CriacaoEspecie apply(Row row, String prefix) {
        CriacaoEspecie entity = new CriacaoEspecie();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescricao(converter.fromRow(row, prefix + "_descricao", String.class));
        entity.setAtivo(converter.fromRow(row, prefix + "_ativo", Boolean.class));
        return entity;
    }
}
