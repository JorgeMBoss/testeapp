package com.mboss.core.repository;

import com.mboss.core.domain.CriacaoCor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CriacaoCor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CriacaoCorRepository extends R2dbcRepository<CriacaoCor, Long>, CriacaoCorRepositoryInternal {
    @Query("SELECT * FROM criacao_cor entity WHERE entity.id not in (select criacao_cor_id from criacao)")
    Flux<CriacaoCor> findAllWhereCriacaoIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<CriacaoCor> findAll();

    @Override
    Mono<CriacaoCor> findById(Long id);

    @Override
    <S extends CriacaoCor> Mono<S> save(S entity);
}

interface CriacaoCorRepositoryInternal {
    <S extends CriacaoCor> Mono<S> insert(S entity);
    <S extends CriacaoCor> Mono<S> save(S entity);
    Mono<Integer> update(CriacaoCor entity);

    Flux<CriacaoCor> findAll();
    Mono<CriacaoCor> findById(Long id);
    Flux<CriacaoCor> findAllBy(Pageable pageable);
    Flux<CriacaoCor> findAllBy(Pageable pageable, Criteria criteria);
}
