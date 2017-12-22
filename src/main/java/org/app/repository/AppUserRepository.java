package org.app.repository;


import org.app.entity.AppUser;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import javax.persistence.QueryHint;

import static org.hibernate.jpa.QueryHints.HINT_READONLY;

@Cacheable("users")
public interface AppUserRepository extends AbstractRepository<AppUser> {

    /**
     * override generated query with custom (to fetch roles collection)
     */
    @QueryHints({
            @QueryHint(name = HINT_READONLY, value = "true"),
            @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    @Query("from AppUser u join fetch u.roles r WHERE u.accountName = :name")
    AppUser findByAccountName(@Param("name") String name);


}
