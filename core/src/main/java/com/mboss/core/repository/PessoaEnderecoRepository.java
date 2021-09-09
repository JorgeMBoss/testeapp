package com.mboss.core.repository;

import com.mboss.core.domain.PessoaEndereco;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PessoaEndereco entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaEnderecoRepository extends R2dbcRepository<PessoaEndereco, Long>, PessoaEnderecoRepositoryInternal {
    @Query("SELECT * FROM pessoa_endereco entity WHERE entity.pessoa_id = :id")
    Flux<PessoaEndereco> findByPessoa(Long id);

    @Query("SELECT * FROM pessoa_endereco entity WHERE entity.pessoa_id IS NULL")
    Flux<PessoaEndereco> findAllWherePessoaIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<PessoaEndereco> findAll();

    @Override
    Mono<PessoaEndereco> findById(Long id);

    @Override
    <S extends PessoaEndereco> Mono<S> save(S entity);
}

interface PessoaEnderecoRepositoryInternal {
    <S extends PessoaEndereco> Mono<S> insert(S entity);
    <S extends PessoaEndereco> Mono<S> save(S entity);
    Mono<Integer> update(PessoaEndereco entity);

    Flux<PessoaEndereco> findAll();
    Mono<PessoaEndereco> findById(Long id);
    Flux<PessoaEndereco> findAllBy(Pageable pageable);
    Flux<PessoaEndereco> findAllBy(Pageable pageable, Criteria criteria);
}
