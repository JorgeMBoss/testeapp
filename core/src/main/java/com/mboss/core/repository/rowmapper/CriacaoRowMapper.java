package com.mboss.core.repository.rowmapper;

import com.mboss.core.domain.Criacao;
import com.mboss.core.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Criacao}, with proper type conversions.
 */
@Service
public class CriacaoRowMapper implements BiFunction<Row, String, Criacao> {

    private final ColumnConverter converter;

    public CriacaoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Criacao} stored in the database.
     */
    @Override
    public Criacao apply(Row row, String prefix) {
        Criacao entity = new Criacao();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setIdEmpresa(converter.fromRow(row, prefix + "_id_empresa", Long.class));
        entity.setNome(converter.fromRow(row, prefix + "_nome", String.class));
        entity.setSexo(converter.fromRow(row, prefix + "_sexo", String.class));
        entity.setPorte(converter.fromRow(row, prefix + "_porte", String.class));
        entity.setIdade(converter.fromRow(row, prefix + "_idade", Integer.class));
        entity.setDataNascimento(converter.fromRow(row, prefix + "_data_nascimento", LocalDate.class));
        entity.setCastrado(converter.fromRow(row, prefix + "_castrado", Boolean.class));
        entity.setAnotacao(converter.fromRow(row, prefix + "_anotacao", String.class));
        entity.setPedigree(converter.fromRow(row, prefix + "_pedigree", Boolean.class));
        entity.setAtivo(converter.fromRow(row, prefix + "_ativo", Boolean.class));
        entity.setCriacaoCorId(converter.fromRow(row, prefix + "_criacao_cor_id", Long.class));
        entity.setCriacaoRacaId(converter.fromRow(row, prefix + "_criacao_raca_id", Long.class));
        entity.setCriacaoEspecieId(converter.fromRow(row, prefix + "_criacao_especie_id", Long.class));
        entity.setPessoaCriacaoId(converter.fromRow(row, prefix + "_pessoa_criacao_id", Long.class));
        return entity;
    }
}
