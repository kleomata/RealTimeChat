package com.chat.SunScript.repository;

import com.chat.SunScript.entity.Admin;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends MongoRepository<Admin, ObjectId> {

    Optional<Admin> findByUsername(String username);
    boolean existsByUsername(String username);

}
