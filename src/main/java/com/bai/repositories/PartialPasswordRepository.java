package com.bai.repositories;

import com.bai.models.PartialPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface PartialPasswordRepository extends JpaRepository<PartialPassword, Integer> {
    List<PartialPassword> findAllByUserId(int userId);

    Optional<PartialPassword> findByUserIdAndCurrentTrue(int userId);

    @Transactional(readOnly = true)
    Stream<PartialPassword> findByUserIdAndCurrentFalseOrderByLastUsedAsc(int userId);

    @Transactional
    void deletePartialPasswordByUserId(int userId);
}
