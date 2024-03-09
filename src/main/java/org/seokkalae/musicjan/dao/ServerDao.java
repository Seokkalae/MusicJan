package org.seokkalae.musicjan.dao;

import org.seokkalae.musicjan.dao.entity.ServerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

@Service
public class ServerDao {
    private final static Logger log = LoggerFactory.getLogger(ServerDao.class);
    private final R2dbcEntityTemplate template;

    public ServerDao(R2dbcEntityTemplate template) {
        this.template = template;
    }

    public Mono<Boolean> serverIsAllow(String id) {
        log.info("try to find server with id: {}", id);
        return template.select(ServerEntity.class)
                .matching(query(where("id").is(id)))
                .one()
                .map(ServerEntity::allow)
                .defaultIfEmpty(false);
    }

    public void saveServer(String id, String name) {
        log.info("save server {} with id: {}", name, id);
        template.insert(ServerEntity.class)
                .using(new ServerEntity(
                        id,
                        name,
                        null
                ))
                .then().subscribe();
    }
}
