package com.mboss.core.repository;

import com.mboss.core.domain.PessoaCriacao;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PessoaCriacao entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaCriacaoRepository extends R2dbcRepository<PessoaCriacao, Long>, PessoaCriacaoRepositoryInternal {
    // just to avoid having unambigous methods
    @Override
    Flux<PessoaCriacao> findAll();

    @Override
    Mono<PessoaCriacao> findById(Long id);

    @Override
    <S extends PessoaCriacao> Mono<S> save(S entity);
}

interface PessoaCriacaoRepositoryInternal {
    <S extends PessoaCriacao> Mono<S> insert(S entity);
    <S extends PessoaCriacao> Mono<S> save(S entity);
    Mono<Integer> update(PessoaCriacao entity);

    Flux<PessoaCriacao> findAll();
    Mono<PessoaCriacao> findById(Long id);
    Flux<PessoaCriacao> findAllBy(Pageable pageable);
    Flux<PessoaCriacao> findAllBy(Pageable pageable, Criteria criteria);
}
