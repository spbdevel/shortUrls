package org.app.repository;


import org.app.entity.RegisteredURL;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.QueryHints;

import javax.persistence.QueryHint;
import java.util.List;

import static org.hibernate.jpa.QueryHints.HINT_READONLY;

@Cacheable("urls")
public interface RegisteredURLRepository extends AbstractRepository<RegisteredURL> {

    @QueryHints({
            @QueryHint(name = HINT_READONLY, value = "true"),
            @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    List<RegisteredURL> findByLongUrlAndAccountAccountName(String longUrl, String accountName);

    @QueryHints({
            @QueryHint(name = HINT_READONLY, value = "true"),
            @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    RegisteredURL findByLongUrlAndAccountId(String longUrl, Long id);

    @QueryHints({
            @QueryHint(name = HINT_READONLY, value = "true"),
            @QueryHint(name = "org.hibernate.cacheable", value = "true")})
    RegisteredURL findByShortUrlAndAccountId(String shortUrl, Long id);


}
