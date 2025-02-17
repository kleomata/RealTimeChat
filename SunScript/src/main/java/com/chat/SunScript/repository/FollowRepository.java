package com.chat.SunScript.repository;

import com.chat.SunScript.entity.Follow;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends MongoRepository<Follow, ObjectId> {

    boolean existsByFollowerIdAndFollowingId(ObjectId follower, ObjectId following);
    void deleteByFollowerIdAndFollowingId(ObjectId follower, ObjectId following);

    long countByFollowingId(ObjectId followingId);
    long countByFollowerId(ObjectId followerId);

    List<Follow> findByFollowerId(ObjectId followerId);
}
