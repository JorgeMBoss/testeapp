package com.mboss.core.repository;

import com.mboss.core.domain.Pessoa;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Pessoa entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PessoaRepository extends R2dbcRepository<Pessoa, Long>, PessoaRepositoryInternal {
    Flux<Pessoa> findAllBy(Pageable pageable);

    @Query("SELECT * FROM pessoa entity WHERE entity.empresa_app_id = :id")
    Flux<Pessoa> findByEmpresaApp(Long id);

    @Query("SELECT * FROM pessoa entity WHERE entity.empresa_app_id IS NULL")
    Flux<Pessoa> findAllWhereEmpresaAppIsNull();

    @Query("SELECT * FROM pessoa entity WHERE entity.pessoa_medico_id = :id")
    Flux<Pessoa> findByPessoaMedico(Long id);

    @Query("SELECT * FROM pessoa entity WHERE entity.pessoa_medico_id IS NULL")
    Flux<Pessoa> findAllWherePessoaMedicoIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Pessoa> findAll();

    @Override
    Mono<Pessoa> findById(Long id);

    @Override
    <S extends Pessoa> Mono<S> save(S entity);
}

interface PessoaRepositoryInternal {
    <S extends Pessoa> Mono<S> insert(S entity);
    <S extends Pessoa> Mono<S> save(S entity);
    Mono<Integer> update(Pessoa entity);

    Flux<Pessoa> findAll();
    Mono<Pessoa> findById(Long id);
    Flux<Pessoa> findAllBy(Pageable pageable);
    Flux<Pessoa> findAllBy(Pageable pageable, Criteria criteria);
}
