package com.chat.SunScript.repository;

import com.chat.SunScript.entity.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<Message, ObjectId> {
    List<Message> findBySenderAndRecipient(String sender, String recipient);
    List<Message> findByRecipientAndSender(String recipient, String sender);
}
