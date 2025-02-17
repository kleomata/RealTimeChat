package com.chat.SunScript.repository;

import com.chat.SunScript.entity.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId> {

    Optional<User> findByUsername(String username);

    boolean existsByDiscriminator(String discriminator);

    @Query("{'$or' : [" +
            "{'firstName' : {$regex : ?0, $options: 'i'}}, " +
            "{'lastName' : {$regex : ?0, $options : 'i'}}" +
            "{'discriminator' : {$regex : ?0}}" +
            "]}")
    List<User> findByFirstNameOrLastNameOrDiscriminatorContaining(String keyword);
    @Query("{'$or' : [{'firstName' : {$regex : ?0, $options: 'i'}}, {'lastName' : {$regex : ?1, $options : 'i'}}]}")
    List<User> findByFirstNameOrLastNameContainingSeparate(String firstName, String lastName);

    @Query("{'firstName' : {$regex : ?0, $options; 'i'}}")
    List<User> findByFirstNameRegex(String firstName);

    Optional<User> findByDiscriminator(String discriminator);

    boolean existsByUsername(String username);
}
