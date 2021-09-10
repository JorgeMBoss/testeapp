package com.mboss.core.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.mboss.core.domain.Criacao;
import com.mboss.core.domain.CriacaoAnamnese;
import com.mboss.core.repository.rowmapper.CriacaoAnamneseRowMapper;
import com.mboss.core.service.EntityManager;
import com.mboss.core.service.EntityManager.LinkTable;
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
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
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

    private final CriacaoAnamneseRowMapper criacaoanamneseMapper;

    private static final Table entityTable = Table.aliased("criacao_anamnese", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable criacaoLink = new LinkTable(
        "rel_criacao_anamnese__criacao",
        "criacao_anamnese_id",
        "criacao_id"
    );

    public CriacaoAnamneseRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CriacaoAnamneseRowMapper criacaoanamneseMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
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
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);

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

    @Override
    public Mono<CriacaoAnamnese> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<CriacaoAnamnese> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<CriacaoAnamnese> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private CriacaoAnamnese process(Row row, RowMetadata metadata) {
        CriacaoAnamnese entity = criacaoanamneseMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends CriacaoAnamnese> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends CriacaoAnamnese> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity).flatMap(savedEntity -> updateRelations(savedEntity));
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update CriacaoAnamnese with id = " + entity.getId());
                        }
                        return entity;
                    }
                )
                .then(updateRelations(entity));
        }
    }

    @Override
    public Mono<Integer> update(CriacaoAnamnese entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId)
            .then(r2dbcEntityTemplate.delete(CriacaoAnamnese.class).matching(query(where("id").is(entityId))).all().then());
    }

    protected <S extends CriacaoAnamnese> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(criacaoLink, entity.getId(), entity.getCriacaos().stream().map(Criacao::getId))
            .then();
        return result.thenReturn(entity);
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(criacaoLink, entityId);
    }
}

class CriacaoAnamneseSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("descricao", table, columnPrefix + "_descricao"));
        columns.add(Column.aliased("id_medico", table, columnPrefix + "_id_medico"));

        return columns;
    }
}
