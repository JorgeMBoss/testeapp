package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.CriacaoRaca;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CriacaoRaca}, with proper type conversions.
 */
@Service
public class CriacaoRacaRowMapper implements BiFunction<Row, String, CriacaoRaca> {

    private final ColumnConverter converter;

    public CriacaoRacaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CriacaoRaca} stored in the database.
     */
    @Override
    public CriacaoRaca apply(Row row, String prefix) {
        CriacaoRaca entity = new CriacaoRaca();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescricao(converter.fromRow(row, prefix + "_descricao", String.class));
        entity.setAtivo(converter.fromRow(row, prefix + "_ativo", Boolean.class));
        return entity;
    }
}
