package com.mboss.core.repository;

import com.mboss.core.domain.PessoaColaborador;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PessoaColaborador entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaColaboradorRepository extends R2dbcRepository<PessoaColaborador, Long>, PessoaColaboradorRepositoryInternal {
    @Query("SELECT * FROM pessoa_colaborador entity WHERE entity.pessoa_funcao_id = :id")
    Flux<PessoaColaborador> findByPessoaFuncao(Long id);

    @Query("SELECT * FROM pessoa_colaborador entity WHERE entity.pessoa_funcao_id IS NULL")
    Flux<PessoaColaborador> findAllWherePessoaFuncaoIsNull();

    @Query("SELECT * FROM pessoa_colaborador entity WHERE entity.pessoa_id = :id")
    Flux<PessoaColaborador> findByPessoa(Long id);

    @Query("SELECT * FROM pessoa_colaborador entity WHERE entity.pessoa_id IS NULL")
    Flux<PessoaColaborador> findAllWherePessoaIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<PessoaColaborador> findAll();

    @Override
    Mono<PessoaColaborador> findById(Long id);

    @Override
    <S extends PessoaColaborador> Mono<S> save(S entity);
}

interface PessoaColaboradorRepositoryInternal {
    <S extends PessoaColaborador> Mono<S> insert(S entity);
    <S extends PessoaColaborador> Mono<S> save(S entity);
    Mono<Integer> update(PessoaColaborador entity);

    Flux<PessoaColaborador> findAll();
    Mono<PessoaColaborador> findById(Long id);
    Flux<PessoaColaborador> findAllBy(Pageable pageable);
    Flux<PessoaColaborador> findAllBy(Pageable pageable, Criteria criteria);
}
