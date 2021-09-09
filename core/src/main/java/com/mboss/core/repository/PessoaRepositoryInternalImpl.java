package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.Pessoa;
import com.mboss.core.repository.rowmapper.EmpresaAppRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Pessoa entity.
 */
@SuppressWarnings("unused")
class PessoaRepositoryInternalImpl implements PessoaRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final EmpresaAppRowMapper empresaappMapper;
    private final PessoaRowMapper pessoaMapper;

    private static final Table entityTable = Table.aliased("pessoa", EntityManager.ENTITY_ALIAS);
    private static final Table empresaAppTable = Table.aliased("empresa_app", "empresaApp");

    public PessoaRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        EmpresaAppRowMapper empresaappMapper,
        PessoaRowMapper pessoaMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.empresaappMapper = empresaappMapper;
        this.pessoaMapper = pessoaMapper;
    }

    @Override
    public Flux<Pessoa> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Pessoa> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Pessoa> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PessoaSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(EmpresaAppSqlHelper.getColumns(empresaAppTable, "empresaApp"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(empresaAppTable)
            .on(Column.create("empresa_app_id", entityTable))
            .equals(Column.create("id", empresaAppTable));

        String select = entityManager.createSelect(selectFrom, Pessoa.class, pageable, criteria);
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
    public Flux<Pessoa> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Pessoa> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Pessoa process(Row row, RowMetadata metadata) {
        Pessoa entity = pessoaMapper.apply(row, "e");
        entity.setEmpresaApp(empresaappMapper.apply(row, "empresaApp"));
        return entity;
    }

    @Override
    public <S extends Pessoa> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Pessoa> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Pessoa with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Pessoa entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class PessoaSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("id_empresa", table, columnPrefix + "_id_empresa"));
        columns.add(Column.aliased("data_cadastro", table, columnPrefix + "_data_cadastro"));
        columns.add(Column.aliased("ativo", table, columnPrefix + "_ativo"));

        columns.add(Column.aliased("empresa_app_id", table, columnPrefix + "_empresa_app_id"));
        return columns;
    }
}
