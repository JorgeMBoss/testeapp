package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.CriacaoAnamnese;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CriacaoAnamnese}, with proper type conversions.
 */
@Service
public class CriacaoAnamneseRowMapper implements BiFunction<Row, String, CriacaoAnamnese> {

    private final ColumnConverter converter;

    public CriacaoAnamneseRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CriacaoAnamnese} stored in the database.
     */
    @Override
    public CriacaoAnamnese apply(Row row, String prefix) {
        CriacaoAnamnese entity = new CriacaoAnamnese();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDescricao(converter.fromRow(row, prefix + "_descricao", String.class));
        entity.setIdMedico(converter.fromRow(row, prefix + "_id_medico", Long.class));
        return entity;
    }
}
