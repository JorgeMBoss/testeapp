package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.PessoaColaborador;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PessoaColaborador}, with proper type conversions.
 */
@Service
public class PessoaColaboradorRowMapper implements BiFunction<Row, String, PessoaColaborador> {

    private final ColumnConverter converter;

    public PessoaColaboradorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PessoaColaborador} stored in the database.
     */
    @Override
    public PessoaColaborador apply(Row row, String prefix) {
        PessoaColaborador entity = new PessoaColaborador();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setDataAdimissao(converter.fromRow(row, prefix + "_data_adimissao", LocalDate.class));
        entity.setDataSaida(converter.fromRow(row, prefix + "_data_saida", LocalDate.class));
        entity.setCargaHoraria(converter.fromRow(row, prefix + "_carga_horaria", Long.class));
        entity.setPrimeiroHorario(converter.fromRow(row, prefix + "_primeiro_horario", ZonedDateTime.class));
        entity.setSegundoHorario(converter.fromRow(row, prefix + "_segundo_horario", ZonedDateTime.class));
        entity.setSalario(converter.fromRow(row, prefix + "_salario", Double.class));
        entity.setComissao(converter.fromRow(row, prefix + "_comissao", Double.class));
        entity.setDescontoMaximo(converter.fromRow(row, prefix + "_desconto_maximo", Double.class));
        entity.setPessoaFuncaoId(converter.fromRow(row, prefix + "_pessoa_funcao_id", Long.class));
        entity.setPessoaId(converter.fromRow(row, prefix + "_pessoa_id", Long.class));
        return entity;
    }
}
