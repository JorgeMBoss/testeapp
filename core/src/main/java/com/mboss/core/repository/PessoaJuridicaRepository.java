package com.mboss.core.repository;

import com.mboss.core.domain.PessoaJuridica;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PessoaJuridica entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaJuridicaRepository extends R2dbcRepository<PessoaJuridica, Long>, PessoaJuridicaRepositoryInternal {
    @Query("SELECT * FROM pessoa_juridica entity WHERE entity.pessoa_complemento_id = :id")
    Flux<PessoaJuridica> findByPessoaComplemento(Long id);

    @Query("SELECT * FROM pessoa_juridica entity WHERE entity.pessoa_complemento_id IS NULL")
    Flux<PessoaJuridica> findAllWherePessoaComplementoIsNull();

    @Query("SELECT * FROM pessoa_juridica entity WHERE entity.pessoa_id = :id")
    Flux<PessoaJuridica> findByPessoa(Long id);

    @Query("SELECT * FROM pessoa_juridica entity WHERE entity.pessoa_id IS NULL")
    Flux<PessoaJuridica> findAllWherePessoaIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<PessoaJuridica> findAll();

    @Override
    Mono<PessoaJuridica> findById(Long id);

    @Override
    <S extends PessoaJuridica> Mono<S> save(S entity);
}

interface PessoaJuridicaRepositoryInternal {
    <S extends PessoaJuridica> Mono<S> insert(S entity);
    <S extends PessoaJuridica> Mono<S> save(S entity);
    Mono<Integer> update(PessoaJuridica entity);

    Flux<PessoaJuridica> findAll();
    Mono<PessoaJuridica> findById(Long id);
    Flux<PessoaJuridica> findAllBy(Pageable pageable);
    Flux<PessoaJuridica> findAllBy(Pageable pageable, Criteria criteria);
}
