package org.app.repository;


import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.QueryHint;
import java.util.List;

import static org.hibernate.jpa.QueryHints.*;


@NoRepositoryBean
public interface AbstractRepository<T> extends JpaRepository<T, Long> {

    @QueryHints({@QueryHint(name = HINT_FETCH_SIZE, value = "50"),
            @QueryHint(name = HINT_READONLY, value = "true"),
            @QueryHint(name = HINT_CACHEABLE, value = "true")})
    List<T> findAll();


    @QueryHints({@QueryHint(name = HINT_FETCH_SIZE, value = "50"),
            @QueryHint(name = HINT_READONLY, value = "true"),
            @QueryHint(name = HINT_CACHEABLE, value = "true")})
    List<T> findAll(Sort sort);



}
