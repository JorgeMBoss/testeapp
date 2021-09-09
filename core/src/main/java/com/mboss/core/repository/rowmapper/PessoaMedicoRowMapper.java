package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.PessoaMedico;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PessoaMedico}, with proper type conversions.
 */
@Service
public class PessoaMedicoRowMapper implements BiFunction<Row, String, PessoaMedico> {

    private final ColumnConverter converter;

    public PessoaMedicoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PessoaMedico} stored in the database.
     */
    @Override
    public PessoaMedico apply(Row row, String prefix) {
        PessoaMedico entity = new PessoaMedico();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setCrm(converter.fromRow(row, prefix + "_crm", String.class));
        return entity;
    }
}
