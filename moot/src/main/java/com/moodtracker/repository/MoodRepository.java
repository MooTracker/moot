package com.moodtracker.repository;

import com.moodtracker.model.Mood;
import com.moodtracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface MoodRepository extends JpaRepository<Mood, Integer> {
    List<Mood> findByUser(User user);
    List<Mood> findByUserAndDateBetween(User user, LocalDateTime start, LocalDateTime end);
}