package org.app.repository;


import org.app.entity.AppUser;
import org.app.entity.RegisteredURL;
import org.app.entity.Stats;

import java.util.List;

public interface StatsRepository extends AbstractRepository<Stats> {

    Stats findByUserAndUrl(AppUser user, RegisteredURL url);

    List<Stats> findByUser(AppUser user);

}
