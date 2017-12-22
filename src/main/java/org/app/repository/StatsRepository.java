package org.app.repository;


import org.app.entity.AppUser;
import org.app.entity.RegisteredURL;
import org.app.entity.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    Stats findByUserAndUrl(AppUser user, RegisteredURL url);

    List<Stats> findByUser(AppUser user);

}
