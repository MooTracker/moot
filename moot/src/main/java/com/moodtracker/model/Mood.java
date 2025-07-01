package com.moodtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "moods")
public class Mood {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String mood;
    @Column(name = "emosi_asli")
    private String emosiAsli;
    private String note;
    private LocalDateTime date;
    // getter & setter
}