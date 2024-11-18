package org.cocreate.CoCreate.repository;

import org.cocreate.CoCreate.model.entity.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CompanyRepository extends MongoRepository<Company, String> {
}
