package com.mboss.core.repository;

import com.mboss.core.domain.CriacaoRaca;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CriacaoRaca entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CriacaoRacaRepository extends R2dbcRepository<CriacaoRaca, Long>, CriacaoRacaRepositoryInternal {
    @Query("SELECT * FROM criacao_raca entity WHERE entity.id not in (select criacao_raca_id from criacao)")
    Flux<CriacaoRaca> findAllWhereCriacaoIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<CriacaoRaca> findAll();

    @Override
    Mono<CriacaoRaca> findById(Long id);

    @Override
    <S extends CriacaoRaca> Mono<S> save(S entity);
}

interface CriacaoRacaRepositoryInternal {
    <S extends CriacaoRaca> Mono<S> insert(S entity);
    <S extends CriacaoRaca> Mono<S> save(S entity);
    Mono<Integer> update(CriacaoRaca entity);

    Flux<CriacaoRaca> findAll();
    Mono<CriacaoRaca> findById(Long id);
    Flux<CriacaoRaca> findAllBy(Pageable pageable);
    Flux<CriacaoRaca> findAllBy(Pageable pageable, Criteria criteria);
}
