package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.CriacaoConsumo;
import com.mboss.core.repository.rowmapper.CriacaoConsumoRowMapper;
import com.mboss.core.repository.rowmapper.EmpresaAppRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the CriacaoConsumo entity.
 */
@SuppressWarnings("unused")
class CriacaoConsumoRepositoryInternalImpl implements CriacaoConsumoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final EmpresaAppRowMapper empresaappMapper;
    private final CriacaoConsumoRowMapper criacaoconsumoMapper;

    private static final Table entityTable = Table.aliased("criacao_consumo", EntityManager.ENTITY_ALIAS);
    private static final Table empresaAppTable = Table.aliased("empresa_app", "empresaApp");

    public CriacaoConsumoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        EmpresaAppRowMapper empresaappMapper,
        CriacaoConsumoRowMapper criacaoconsumoMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.empresaappMapper = empresaappMapper;
        this.criacaoconsumoMapper = criacaoconsumoMapper;
    }

    @Override
    public Flux<CriacaoConsumo> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<CriacaoConsumo> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<CriacaoConsumo> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CriacaoConsumoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(EmpresaAppSqlHelper.getColumns(empresaAppTable, "empresaApp"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(empresaAppTable)
            .on(Column.create("empresa_app_id", entityTable))
            .equals(Column.create("id", empresaAppTable));

        String select = entityManager.createSelect(selectFrom, CriacaoConsumo.class, pageable, criteria);
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
    public Flux<CriacaoConsumo> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<CriacaoConsumo> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private CriacaoConsumo process(Row row, RowMetadata metadata) {
        CriacaoConsumo entity = criacaoconsumoMapper.apply(row, "e");
        entity.setEmpresaApp(empresaappMapper.apply(row, "empresaApp"));
        return entity;
    }

    @Override
    public <S extends CriacaoConsumo> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends CriacaoConsumo> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update CriacaoConsumo with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(CriacaoConsumo entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CriacaoConsumoSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("data_sistema", table, columnPrefix + "_data_sistema"));
        columns.add(Column.aliased("data_venda", table, columnPrefix + "_data_venda"));
        columns.add(Column.aliased("data_aviso", table, columnPrefix + "_data_aviso"));
        columns.add(Column.aliased("anotacao", table, columnPrefix + "_anotacao"));
        columns.add(Column.aliased("status", table, columnPrefix + "_status"));

        columns.add(Column.aliased("empresa_app_id", table, columnPrefix + "_empresa_app_id"));
        return columns;
    }
}
