package com.mboss.core.repository;

import com.mboss.core.domain.UserApp;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the UserApp entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserAppRepository extends R2dbcRepository<UserApp, Long>, UserAppRepositoryInternal {
    @Query("SELECT * FROM user_app entity WHERE entity.user_id = :id")
    Flux<UserApp> findByUser(Long id);

    @Query("SELECT * FROM user_app entity WHERE entity.user_id IS NULL")
    Flux<UserApp> findAllWhereUserIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<UserApp> findAll();

    @Override
    Mono<UserApp> findById(Long id);

    @Override
    <S extends UserApp> Mono<S> save(S entity);
}

interface UserAppRepositoryInternal {
    <S extends UserApp> Mono<S> insert(S entity);
    <S extends UserApp> Mono<S> save(S entity);
    Mono<Integer> update(UserApp entity);

    Flux<UserApp> findAll();
    Mono<UserApp> findById(Long id);
    Flux<UserApp> findAllBy(Pageable pageable);
    Flux<UserApp> findAllBy(Pageable pageable, Criteria criteria);
}
