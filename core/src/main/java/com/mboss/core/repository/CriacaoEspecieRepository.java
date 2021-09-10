package com.mboss.core.repository;

import com.mboss.core.domain.CriacaoEspecie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CriacaoEspecie entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CriacaoEspecieRepository extends R2dbcRepository<CriacaoEspecie, Long>, CriacaoEspecieRepositoryInternal {
    @Query("SELECT * FROM criacao_especie entity WHERE entity.id not in (select criacao_especie_id from criacao)")
    Flux<CriacaoEspecie> findAllWhereCriacaoIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<CriacaoEspecie> findAll();

    @Override
    Mono<CriacaoEspecie> findById(Long id);

    @Override
    <S extends CriacaoEspecie> Mono<S> save(S entity);
}

interface CriacaoEspecieRepositoryInternal {
    <S extends CriacaoEspecie> Mono<S> insert(S entity);
    <S extends CriacaoEspecie> Mono<S> save(S entity);
    Mono<Integer> update(CriacaoEspecie entity);

    Flux<CriacaoEspecie> findAll();
    Mono<CriacaoEspecie> findById(Long id);
    Flux<CriacaoEspecie> findAllBy(Pageable pageable);
    Flux<CriacaoEspecie> findAllBy(Pageable pageable, Criteria criteria);
}
