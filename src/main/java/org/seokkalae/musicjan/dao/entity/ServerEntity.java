package org.seokkalae.musicjan.dao.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;

@Table(schema = "public", name = "server")
public record ServerEntity(
        @Id
        String id,
        String name,
        Boolean allow
) {
    boolean hasId() {
        return id != null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ServerEntity server)) {
            return false;
        }
        return Objects.equals(name, server.name)
                && Objects.equals(allow, server.allow);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, allow);
    }
}
