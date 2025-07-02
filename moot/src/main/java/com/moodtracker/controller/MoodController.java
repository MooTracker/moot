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
import java.time.format.DateTimeFormatter;
import java.util.*;

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
                            @RequestParam(required = false) String mood,
                            @RequestParam(required = false) String emosiAsli,
                            @RequestParam String note,
                            Model model) {
        User user = userRepo.findByUsername(username);
        if (user == null) return "redirect:/login";
        
        // Urutan logika seperti project sebelumnya:
        // 1. Deteksi emosi spesifik dari kata kunci terlebih dahulu
        if (emosiAsli == null || emosiAsli.isEmpty()) {
            emosiAsli = detectEmotionFromNote(note);
        }
        
        // 2. Jika mood kosong, gunakan analisis sentimen
        if (mood == null || mood.isEmpty()) {
            // Jika ada emosi spesifik dari keyword, gunakan itu untuk mood
            if (emosiAsli != null && !emosiAsli.isEmpty()) {
                mood = mapEmotionToMood(emosiAsli);
            } else {
                // Jika tidak ada keyword, baru gunakan sentiment analysis
                try {
                    mood = sentimentService.analyze(note);
                    if (mood == null || mood.equals("Unknown")) {
                        mood = "Neutral"; // fallback
                    }
                } catch (Exception e) {
                    mood = "Neutral"; // fallback jika API error
                }
            }
        }
        
        Mood m = new Mood();
        m.setUser(user);
        m.setMood(mood);
        m.setEmosiAsli(emosiAsli);
        m.setNote(note);
        m.setDate(LocalDateTime.now());
        moodRepo.save(m);
        
        // Redirect ke halaman hasil mood dengan parameter yang aman
        StringBuilder redirectUrl = new StringBuilder("redirect:/mood-result?username=" + username + "&mood=" + mood + "&gender=" + user.getGender());
        if (emosiAsli != null && !emosiAsli.isEmpty()) {
            redirectUrl.append("&emosiAsli=").append(emosiAsli);
        }
        return redirectUrl.toString();
    }
    
    private String detectEmotionFromNote(String note) {
        String lower = note.toLowerCase();
        
        // Deteksi emosi spesifik dari kata kunci
        if (lower.contains("gembira") || lower.contains("bahagia") || lower.contains("senang sekali")) {
            return "gembira";
        } else if (lower.contains("sedih") || lower.contains("kecewa") || lower.contains("galau")) {
            return "sedih";
        } else if (lower.contains("marah") || lower.contains("kesal") || lower.contains("jengkel")) {
            return "marah";
        } else if (lower.contains("cemas") || lower.contains("khawatir") || lower.contains("takut")) {
            return "cemas";
        } else if (lower.contains("tenang") || lower.contains("damai") || lower.contains("rileks")) {
            return "tenang";
        } else if (lower.contains("bosan") || lower.contains("lelah") || lower.contains("capek")) {
            return "bosan";
        } else {
            return "";
        }
    }
    
    private String mapEmotionToMood(String emotion) {
        String emotionLower = emotion.toLowerCase();
        
        switch (emotionLower) {
            case "gembira":
                return "Very Positive";
            case "sedih":
                return "Negative";
            case "marah":
                return "Very Negative";
            case "cemas":
                return "Negative";
            case "tenang":
                return "Positive";
            case "bosan":
                return "Neutral";
            default:
                return "Neutral";
        }
    }

    @GetMapping("/mood-result")
    public String showMoodResult(@RequestParam String username,
                                @RequestParam String mood,
                                @RequestParam(required = false) String emosiAsli,
                                @RequestParam String gender,
                                Model model) {
        try {
            // Handle null/empty values
            if (emosiAsli == null) emosiAsli = "";
            if (mood == null) mood = "Neutral";
            if (gender == null) gender = "Pria";
            
            System.out.println("DEBUG - Username: " + username);
            System.out.println("DEBUG - Mood: " + mood);
            System.out.println("DEBUG - EmosiAsli: " + emosiAsli);
            System.out.println("DEBUG - Gender: " + gender);
            
            model.addAttribute("username", username);
            model.addAttribute("mood", mood);
            model.addAttribute("emosiAsli", emosiAsli);
            model.addAttribute("gender", gender);
            
            // Tentukan karakter image berdasarkan mood dan gender
            String characterImage = getCharacterImage(mood, gender);
            model.addAttribute("characterImage", characterImage);
            System.out.println("DEBUG - Character Image: " + characterImage);
            
            // Dapatkan pesan motivasi berdasarkan mood
            String[] motivationMessage = getMotivationMessage(mood);
            model.addAttribute("motivationMessage1", motivationMessage[0]);
            model.addAttribute("motivationMessage2", motivationMessage[1]);
            System.out.println("DEBUG - Motivation: " + motivationMessage[0]);
            
            return "mood-result";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/dashboard?username=" + username;
        }
    }
    
    private String getCharacterImage(String mood, String gender) {
        String genderSuffix = gender.equalsIgnoreCase("Wanita") ? "_f" : "_m";
        String moodLower = mood.toLowerCase();
        
        if (moodLower.contains("very negative") || moodLower.contains("marah") || moodLower.contains("angry")) {
            return "/assets/angry" + genderSuffix + ".png";
        } else if (moodLower.contains("negative") || moodLower.contains("sedih") || moodLower.contains("sad")) {
            return "/assets/sad" + genderSuffix + ".png";
        } else if (moodLower.contains("neutral") || moodLower.contains("biasa") || moodLower.contains("netral")) {
            return "/assets/neutral" + genderSuffix + ".png";
        } else if (moodLower.contains("positive") || moodLower.contains("senang") || moodLower.contains("happy")) {
            return "/assets/happy" + genderSuffix + ".png";
        } else if (moodLower.contains("very positive") || moodLower.contains("bahagia") || moodLower.contains("gembira")) {
            return "/assets/very_happy" + genderSuffix + ".png";
        } else {
            return "/assets/neutral" + genderSuffix + ".png";
        }
    }
    
    private String[] getMotivationMessage(String mood) {
        String moodLower = mood.toLowerCase();
        
        if (moodLower.contains("very positive") || moodLower.contains("gembira") || moodLower.contains("bahagia")) {
            return new String[]{"Pertahankan semangatmu hari ini!", "Terus tebarkan energi positif ke sekitarmu."};
        } else if (moodLower.contains("positive") || moodLower.contains("senang") || moodLower.contains("happy")) {
            return new String[]{"Nikmati hari yang indah ini.", "Syukuri setiap hal kecil hari ini."};
        } else if (moodLower.contains("neutral") || moodLower.contains("biasa") || moodLower.contains("netral")) {
            return new String[]{"Hari biasa juga penting untuk istirahat.", "Gunakan waktu ini untuk refleksi diri."};
        } else if (moodLower.contains("negative") || moodLower.contains("sedih") || moodLower.contains("sad") || moodLower.contains("cemas")) {
            return new String[]{"Kamu berharga, apapun yang terjadi.", "Jangan ragu untuk meminta bantuan."};
        } else if (moodLower.contains("very negative") || moodLower.contains("marah") || moodLower.contains("angry")) {
            return new String[]{"Tarik napas dalam-dalam, kamu bisa melewati ini.", "Jangan menyerah, hari buruk akan berlalu."};
        } else {
            return new String[]{"Semangat menjalani hari!", "Semoga harimu lebih baik dan kamu lebih bahagia."};
        }
    }

    @GetMapping("/riwayat")
    public String riwayat(@RequestParam String username,
                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tanggal,
                          @RequestParam(required = false) Boolean lastResult,
                          @RequestParam(required = false) String lastMood,
                          @RequestParam(required = false) String lastEmosiAsli,
                          @RequestParam(required = false) String lastGender,
                          Model model) {
        try {
            User user = userRepo.findByUsername(username);
            if (user == null) {
                return "redirect:/login";
            }
            
            List<Mood> moods = new ArrayList<>();
            if (tanggal != null) {
                moods = moodRepo.findByUserAndDateBetween(user,
                    tanggal.atStartOfDay(),
                    tanggal.plusDays(1).atStartOfDay());
                model.addAttribute("tanggal", tanggal.toString());
            } else {
                moods = moodRepo.findByUser(user);
            }
            
            // Handle null moods
            if (moods == null) {
                moods = new ArrayList<>();
            }
            
            model.addAttribute("moods", moods);
            model.addAttribute("username", username);
            
            // Jika ada lastResult, atur link kembali ke mood-result
            if (lastResult != null && lastResult) {
                StringBuilder backUrl = new StringBuilder("/mood-result?username=" + username);
                if (lastMood != null && !lastMood.isEmpty()) {
                    backUrl.append("&mood=").append(lastMood);
                }
                if (lastEmosiAsli != null && !lastEmosiAsli.isEmpty()) {
                    backUrl.append("&emosiAsli=").append(lastEmosiAsli);
                }
                if (lastGender != null && !lastGender.isEmpty()) {
                    backUrl.append("&gender=").append(lastGender);
                }
                model.addAttribute("backUrl", backUrl.toString());
            } else {
                model.addAttribute("backUrl", "/dashboard?username=" + username);
            }
            
            return "riwayat";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/dashboard?username=" + username;
        }
    }
    
    private int mapMoodToValue(String mood) {
        String moodLower = mood.toLowerCase();
        if (moodLower.contains("very positive") || moodLower.contains("bahagia") || moodLower.contains("gembira")) {
            return 5;
        } else if (moodLower.contains("positive") || moodLower.contains("senang") || moodLower.contains("happy")) {
            return 4;
        } else if (moodLower.contains("neutral") || moodLower.contains("biasa") || moodLower.contains("netral")) {
            return 3;
        } else if (moodLower.contains("negative") || moodLower.contains("sedih") || moodLower.contains("sad")) {
            return 2;
        } else if (moodLower.contains("very negative") || moodLower.contains("marah") || moodLower.contains("angry")) {
            return 1;
        } else {
            return 3;
        }
    }
}