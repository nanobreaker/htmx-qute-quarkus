package dev.thatwhichis.core.domain.todo;

import java.util.UUID;

public record TodoId(Integer id, UUID userId) {

}