package com.moodtracker.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "moods")
public class Mood {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    private String mood;
    @Column(name = "emosi_asli")
    private String emosiAsli;
    private String note;
    private LocalDateTime date;

    // Getter & Setter
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public String getEmosiAsli() { return emosiAsli; }
    public void setEmosiAsli(String emosiAsli) { this.emosiAsli = emosiAsli; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}