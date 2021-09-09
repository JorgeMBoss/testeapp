package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.CriacaoAnamnese;
import com.mboss.core.repository.rowmapper.CriacaoAnamneseRowMapper;
import com.mboss.core.repository.rowmapper.CriacaoRowMapper;
import com.mboss.core.repository.rowmapper.EmpresaAppRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the CriacaoAnamnese entity.
 */
@SuppressWarnings("unused")
class CriacaoAnamneseRepositoryInternalImpl implements CriacaoAnamneseRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final EmpresaAppRowMapper empresaappMapper;
    private final CriacaoRowMapper criacaoMapper;
    private final CriacaoAnamneseRowMapper criacaoanamneseMapper;

    private static final Table entityTable = Table.aliased("criacao_anamnese", EntityManager.ENTITY_ALIAS);
    private static final Table empresaAppTable = Table.aliased("empresa_app", "empresaApp");
    private static final Table criacaoTable = Table.aliased("criacao", "criacao");

    public CriacaoAnamneseRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        EmpresaAppRowMapper empresaappMapper,
        CriacaoRowMapper criacaoMapper,
        CriacaoAnamneseRowMapper criacaoanamneseMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.empresaappMapper = empresaappMapper;
        this.criacaoMapper = criacaoMapper;
        this.criacaoanamneseMapper = criacaoanamneseMapper;
    }

    @Override
    public Flux<CriacaoAnamnese> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<CriacaoAnamnese> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<CriacaoAnamnese> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CriacaoAnamneseSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(EmpresaAppSqlHelper.getColumns(empresaAppTable, "empresaApp"));
        columns.addAll(CriacaoSqlHelper.getColumns(criacaoTable, "criacao"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(empresaAppTable)
            .on(Column.create("empresa_app_id", entityTable))
            .equals(Column.create("id", empresaAppTable))
            .leftOuterJoin(criacaoTable)
            .on(Column.create("criacao_id", entityTable))
            .equals(Column.create("id", criacaoTable));

        String select = entityManager.createSelect(selectFrom, CriacaoAnamnese.class, pageable, criteria);
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
    public Flux<CriacaoAnamnese> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<CriacaoAnamnese> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private CriacaoAnamnese process(Row row, RowMetadata metadata) {
        CriacaoAnamnese entity = criacaoanamneseMapper.apply(row, "e");
        entity.setEmpresaApp(empresaappMapper.apply(row, "empresaApp"));
        entity.setCriacao(criacaoMapper.apply(row, "criacao"));
        return entity;
    }

    @Override
    public <S extends CriacaoAnamnese> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends CriacaoAnamnese> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update CriacaoAnamnese with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(CriacaoAnamnese entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CriacaoAnamneseSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("descricao", table, columnPrefix + "_descricao"));
        columns.add(Column.aliased("id_medico", table, columnPrefix + "_id_medico"));

        columns.add(Column.aliased("empresa_app_id", table, columnPrefix + "_empresa_app_id"));
        columns.add(Column.aliased("criacao_id", table, columnPrefix + "_criacao_id"));
        return columns;
    }
}
