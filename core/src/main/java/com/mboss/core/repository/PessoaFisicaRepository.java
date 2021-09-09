package com.mboss.core.repository;

import com.mboss.core.domain.PessoaFisica;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PessoaFisica entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaFisicaRepository extends R2dbcRepository<PessoaFisica, Long>, PessoaFisicaRepositoryInternal {
    @Query("SELECT * FROM pessoa_fisica entity WHERE entity.pessoa_complemento_id = :id")
    Flux<PessoaFisica> findByPessoaComplemento(Long id);

    @Query("SELECT * FROM pessoa_fisica entity WHERE entity.pessoa_complemento_id IS NULL")
    Flux<PessoaFisica> findAllWherePessoaComplementoIsNull();

    @Query("SELECT * FROM pessoa_fisica entity WHERE entity.pessoa_id = :id")
    Flux<PessoaFisica> findByPessoa(Long id);

    @Query("SELECT * FROM pessoa_fisica entity WHERE entity.pessoa_id IS NULL")
    Flux<PessoaFisica> findAllWherePessoaIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<PessoaFisica> findAll();

    @Override
    Mono<PessoaFisica> findById(Long id);

    @Override
    <S extends PessoaFisica> Mono<S> save(S entity);
}

interface PessoaFisicaRepositoryInternal {
    <S extends PessoaFisica> Mono<S> insert(S entity);
    <S extends PessoaFisica> Mono<S> save(S entity);
    Mono<Integer> update(PessoaFisica entity);

    Flux<PessoaFisica> findAll();
    Mono<PessoaFisica> findById(Long id);
    Flux<PessoaFisica> findAllBy(Pageable pageable);
    Flux<PessoaFisica> findAllBy(Pageable pageable, Criteria criteria);
}
