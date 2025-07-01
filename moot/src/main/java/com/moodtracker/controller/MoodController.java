package com.moodtracker.controller;

import com.moodtracker.model.Mood;
import com.moodtracker.model.User;
import com.moodtracker.repository.MoodRepository;
import com.moodtracker.repository.UserRepository;
import com.moodtracker.service.SentimentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class MoodController {
    @Autowired
    private MoodRepository moodRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private SentimentService sentimentService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam String username, Model model) {
        model.addAttribute("username", username);
        return "dashboard";
    }

    @PostMapping("/input-mood")
    public String inputMood(@RequestParam String username,
                            @RequestParam String mood,
                            @RequestParam(required = false) String emosiAsli,
                            @RequestParam(required = false) String note,
                            Model model) {
        User user = userRepo.findByUsername(username);
        if (user == null) return "redirect:/login";
        Mood m = new Mood();
        m.setUser(user);
        m.setMood(mood);
        // Jika emosiAsli kosong, analisa otomatis dari note
        if (emosiAsli == null || emosiAsli.isEmpty()) {
            emosiAsli = sentimentService.analyze(note);
        }
        m.setEmosiAsli(emosiAsli);
        m.setNote(note);
        m.setDate(LocalDateTime.now());
        moodRepo.save(m);
        return "redirect:/dashboard?username=" + username;
    }

    @GetMapping("/riwayat")
    public String riwayat(@RequestParam String username,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tanggal,
                          Model model) {
        User user = userRepo.findByUsername(username);
        List<Mood> moods;
        if (tanggal != null) {
            moods = moodRepo.findByUserAndDateBetween(user,
                tanggal.atStartOfDay(),
                tanggal.plusDays(1).atStartOfDay());
            model.addAttribute("tanggal", tanggal.toString());
        } else {
            moods = moodRepo.findByUser(user);
        }
        model.addAttribute("moods", moods);
        model.addAttribute("username", username);
        return "riwayat";
    }
}