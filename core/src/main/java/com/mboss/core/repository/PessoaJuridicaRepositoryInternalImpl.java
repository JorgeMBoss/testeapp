package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.PessoaJuridica;
import com.mboss.core.repository.rowmapper.PessoaComplementoRowMapper;
import com.mboss.core.repository.rowmapper.PessoaJuridicaRowMapper;
import com.mboss.core.repository.rowmapper.PessoaRowMapper;
import com.mboss.core.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the PessoaJuridica entity.
 */
@SuppressWarnings("unused")
class PessoaJuridicaRepositoryInternalImpl implements PessoaJuridicaRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PessoaComplementoRowMapper pessoacomplementoMapper;
    private final PessoaRowMapper pessoaMapper;
    private final PessoaJuridicaRowMapper pessoajuridicaMapper;

    private static final Table entityTable = Table.aliased("pessoa_juridica", EntityManager.ENTITY_ALIAS);
    private static final Table pessoaComplementoTable = Table.aliased("pessoa_complemento", "pessoaComplemento");
    private static final Table pessoaTable = Table.aliased("pessoa", "pessoa");

    public PessoaJuridicaRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PessoaComplementoRowMapper pessoacomplementoMapper,
        PessoaRowMapper pessoaMapper,
        PessoaJuridicaRowMapper pessoajuridicaMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.pessoacomplementoMapper = pessoacomplementoMapper;
        this.pessoaMapper = pessoaMapper;
        this.pessoajuridicaMapper = pessoajuridicaMapper;
    }

    @Override
    public Flux<PessoaJuridica> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<PessoaJuridica> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<PessoaJuridica> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PessoaJuridicaSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PessoaComplementoSqlHelper.getColumns(pessoaComplementoTable, "pessoaComplemento"));
        columns.addAll(PessoaSqlHelper.getColumns(pessoaTable, "pessoa"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(pessoaComplementoTable)
            .on(Column.create("pessoa_complemento_id", entityTable))
            .equals(Column.create("id", pessoaComplementoTable))
            .leftOuterJoin(pessoaTable)
            .on(Column.create("pessoa_id", entityTable))
            .equals(Column.create("id", pessoaTable));

        String select = entityManager.createSelect(selectFrom, PessoaJuridica.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<PessoaJuridica> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<PessoaJuridica> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private PessoaJuridica process(Row row, RowMetadata metadata) {
        PessoaJuridica entity = pessoajuridicaMapper.apply(row, "e");
        entity.setPessoaComplemento(pessoacomplementoMapper.apply(row, "pessoaComplemento"));
        entity.setPessoa(pessoaMapper.apply(row, "pessoa"));
        return entity;
    }

    @Override
    public <S extends PessoaJuridica> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends PessoaJuridica> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update PessoaJuridica with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(PessoaJuridica entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class PessoaJuridicaSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("cnpj", table, columnPrefix + "_cnpj"));
        columns.add(Column.aliased("nome_razao", table, columnPrefix + "_nome_razao"));
        columns.add(Column.aliased("nome_fantasia", table, columnPrefix + "_nome_fantasia"));

        columns.add(Column.aliased("pessoa_complemento_id", table, columnPrefix + "_pessoa_complemento_id"));
        columns.add(Column.aliased("pessoa_id", table, columnPrefix + "_pessoa_id"));
        return columns;
    }
}
