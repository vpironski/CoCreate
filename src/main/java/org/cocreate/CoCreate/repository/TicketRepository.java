package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketRepository extends MongoRepository<Ticket, String> {
}