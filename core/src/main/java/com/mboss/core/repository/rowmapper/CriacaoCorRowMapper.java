package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.CriacaoCor;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CriacaoCor}, with proper type conversions.
 */
@Service
public class CriacaoCorRowMapper implements BiFunction<Row, String, CriacaoCor> {

    private final ColumnConverter converter;

    public CriacaoCorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CriacaoCor} stored in the database.
     */
    @Override
    public CriacaoCor apply(Row row, String prefix) {
        CriacaoCor entity = new CriacaoCor();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescricao(converter.fromRow(row, prefix + "_descricao", String.class));
        entity.setAtivo(converter.fromRow(row, prefix + "_ativo", Boolean.class));
        return entity;
    }
}
