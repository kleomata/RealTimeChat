package com.chat.SunScript.repository;

import com.chat.SunScript.entity.UserStatus;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStatusRepository extends MongoRepository<UserStatus, ObjectId> {
    Optional<UserStatus> findByUsername(String username);
}
