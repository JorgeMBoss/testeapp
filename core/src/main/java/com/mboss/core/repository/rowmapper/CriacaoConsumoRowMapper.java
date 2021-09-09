package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.CriacaoConsumo;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CriacaoConsumo}, with proper type conversions.
 */
@Service
public class CriacaoConsumoRowMapper implements BiFunction<Row, String, CriacaoConsumo> {

    private final ColumnConverter converter;

    public CriacaoConsumoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CriacaoConsumo} stored in the database.
     */
    @Override
    public CriacaoConsumo apply(Row row, String prefix) {
        CriacaoConsumo entity = new CriacaoConsumo();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDataSistema(converter.fromRow(row, prefix + "_data_sistema", LocalDate.class));
        entity.setDataVenda(converter.fromRow(row, prefix + "_data_venda", LocalDate.class));
        entity.setDataAviso(converter.fromRow(row, prefix + "_data_aviso", LocalDate.class));
        entity.setAnotacao(converter.fromRow(row, prefix + "_anotacao", String.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", Boolean.class));
        entity.setEmpresaAppId(converter.fromRow(row, prefix + "_empresa_app_id", Long.class));
        return entity;
    }
}
