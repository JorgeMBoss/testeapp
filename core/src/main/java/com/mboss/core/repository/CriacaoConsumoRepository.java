package com.mboss.core.repository;

import com.mboss.core.domain.CriacaoConsumo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CriacaoConsumo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CriacaoConsumoRepository extends R2dbcRepository<CriacaoConsumo, Long>, CriacaoConsumoRepositoryInternal {
    Flux<CriacaoConsumo> findAllBy(Pageable pageable);

    @Override
    Mono<CriacaoConsumo> findOneWithEagerRelationships(Long id);

    @Override
    Flux<CriacaoConsumo> findAllWithEagerRelationships();

    @Override
    Flux<CriacaoConsumo> findAllWithEagerRelationships(Pageable page);

    @Override
    Mono<Void> deleteById(Long id);

    @Query(
        "SELECT entity.* FROM criacao_consumo entity JOIN rel_criacao_consumo__criacao joinTable ON entity.id = joinTable.criacao_consumo_id WHERE joinTable.criacao_id = :id"
    )
    Flux<CriacaoConsumo> findByCriacao(Long id);

    // just to avoid having unambigous methods
    @Override
    Flux<CriacaoConsumo> findAll();

    @Override
    Mono<CriacaoConsumo> findById(Long id);

    @Override
    <S extends CriacaoConsumo> Mono<S> save(S entity);
}

interface CriacaoConsumoRepositoryInternal {
    <S extends CriacaoConsumo> Mono<S> insert(S entity);
    <S extends CriacaoConsumo> Mono<S> save(S entity);
    Mono<Integer> update(CriacaoConsumo entity);

    Flux<CriacaoConsumo> findAll();
    Mono<CriacaoConsumo> findById(Long id);
    Flux<CriacaoConsumo> findAllBy(Pageable pageable);
    Flux<CriacaoConsumo> findAllBy(Pageable pageable, Criteria criteria);

    Mono<CriacaoConsumo> findOneWithEagerRelationships(Long id);

    Flux<CriacaoConsumo> findAllWithEagerRelationships();

    Flux<CriacaoConsumo> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
