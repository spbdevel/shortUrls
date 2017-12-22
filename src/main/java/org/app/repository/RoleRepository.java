package org.app.repository;


import org.app.entity.Role;
import org.springframework.cache.annotation.Cacheable;

public interface RoleRepository extends AbstractRepository<Role> {
    @Cacheable("roles")
    Role findByName(String name);

}
