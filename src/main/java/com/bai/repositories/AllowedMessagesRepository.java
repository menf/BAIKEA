package com.bai.repositories;

import com.bai.models.AllowedMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllowedMessagesRepository extends JpaRepository<AllowedMessages, Integer> {

    List<AllowedMessages> findByUserId(int userId);

    Optional<AllowedMessages> findByUserIdAndMessageId(int userId, int messageId);

    List<AllowedMessages> findAllByMessageId(int messageId);

    void deleteByUserIdAndMessageId(int userId, int messageId);

}
