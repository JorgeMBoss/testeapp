package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.PessoaFisica;
import com.mboss.core.repository.rowmapper.PessoaComplementoRowMapper;
import com.mboss.core.repository.rowmapper.PessoaFisicaRowMapper;
import com.mboss.core.repository.rowmapper.PessoaRowMapper;
import com.mboss.core.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.LocalDate;
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
 * Spring Data SQL reactive custom repository implementation for the PessoaFisica entity.
 */
@SuppressWarnings("unused")
class PessoaFisicaRepositoryInternalImpl implements PessoaFisicaRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PessoaComplementoRowMapper pessoacomplementoMapper;
    private final PessoaRowMapper pessoaMapper;
    private final PessoaFisicaRowMapper pessoafisicaMapper;

    private static final Table entityTable = Table.aliased("pessoa_fisica", EntityManager.ENTITY_ALIAS);
    private static final Table pessoaComplementoTable = Table.aliased("pessoa_complemento", "pessoaComplemento");
    private static final Table pessoaTable = Table.aliased("pessoa", "pessoa");

    public PessoaFisicaRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PessoaComplementoRowMapper pessoacomplementoMapper,
        PessoaRowMapper pessoaMapper,
        PessoaFisicaRowMapper pessoafisicaMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.pessoacomplementoMapper = pessoacomplementoMapper;
        this.pessoaMapper = pessoaMapper;
        this.pessoafisicaMapper = pessoafisicaMapper;
    }

    @Override
    public Flux<PessoaFisica> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<PessoaFisica> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<PessoaFisica> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PessoaFisicaSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
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

        String select = entityManager.createSelect(selectFrom, PessoaFisica.class, pageable, criteria);
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
    public Flux<PessoaFisica> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<PessoaFisica> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private PessoaFisica process(Row row, RowMetadata metadata) {
        PessoaFisica entity = pessoafisicaMapper.apply(row, "e");
        entity.setPessoaComplemento(pessoacomplementoMapper.apply(row, "pessoaComplemento"));
        entity.setPessoa(pessoaMapper.apply(row, "pessoa"));
        return entity;
    }

    @Override
    public <S extends PessoaFisica> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends PessoaFisica> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update PessoaFisica with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(PessoaFisica entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class PessoaFisicaSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("cpf", table, columnPrefix + "_cpf"));
        columns.add(Column.aliased("rg", table, columnPrefix + "_rg"));
        columns.add(Column.aliased("data_nascimento", table, columnPrefix + "_data_nascimento"));
        columns.add(Column.aliased("idade", table, columnPrefix + "_idade"));
        columns.add(Column.aliased("sexo", table, columnPrefix + "_sexo"));
        columns.add(Column.aliased("cor", table, columnPrefix + "_cor"));
        columns.add(Column.aliased("estado_civil", table, columnPrefix + "_estado_civil"));
        columns.add(Column.aliased("naturalidade", table, columnPrefix + "_naturalidade"));
        columns.add(Column.aliased("nacionalidade", table, columnPrefix + "_nacionalidade"));

        columns.add(Column.aliased("pessoa_complemento_id", table, columnPrefix + "_pessoa_complemento_id"));
        columns.add(Column.aliased("pessoa_id", table, columnPrefix + "_pessoa_id"));
        return columns;
    }
}
