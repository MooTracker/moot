# ARSITEKTUR SISTEM MOODTRACKER

## 1. KONEKSI DATABASE
```
📁 application.properties
├── spring.datasource.url=jdbc:mysql://localhost:3306/moodtracker
├── spring.datasource.username=root  
├── spring.datasource.password=
└── spring.jpa.hibernate.ddl-auto=update (Auto-create tables)

📁 Database MySQL (XAMPP)
├── Table: users (id, username, password, gender)
└── Table: moods (id, user_id, mood, emosi_asli, note, date)
```

## 2. ALUR KERJA SISTEM

### A. LAYER ARCHITECTURE
```
🌐 PRESENTATION LAYER (Templates + CSS)
    ↕️ 
🎮 CONTROLLER LAYER (Spring MVC)
    ↕️ 
💼 SERVICE LAYER (SentimentService)
    ↕️ 
🗃️ REPOSITORY LAYER (JPA/Hibernate)
    ↕️ 
🗄️ DATABASE LAYER (MySQL)
```

### B. FLOW INPUT MOOD
```
1. User Input (dashboard.html)
   └── POST /input-mood
       └── MoodController.inputMood()
           ├── UserRepository.findByUsername() → DATABASE
           ├── detectEmotionFromNote() → Keyword Detection
           ├── SentimentService.analyze() → Python API (port 8000)
           ├── MoodRepository.save() → DATABASE
           └── redirect → /mood-result

2. Show Result (mood-result.html)
   └── GET /mood-result
       └── MoodController.showMoodResult()
           ├── URLDecoder untuk parameter
           ├── getCharacterImage() → Select image
           ├── getMotivationMessage() → Generate message
           └── return "mood-result" template
```

### C. INTEGRASI KOMPONEN
```
🔤 KEYWORD DETECTION (Java)
├── detectEmotionFromNote()
├── Input: "Aku sedih banget hari ini"
└── Output: "sedih"

🤖 AI SENTIMENT ANALYSIS (Python API)
├── sentiment_api.py (FastAPI)
├── Input: "Today was okay I guess"
└── Output: "Neutral"

📊 DATABASE PERSISTENCE
├── User Entity (@OneToMany Mood)
├── Mood Entity (@ManyToOne User)
└── Auto-save via JPA Repository
```

## 3. NAVIGASI SISTEM

### A. Authentication Flow
```
🏠 localhost:8080 
└── redirect → /login
    ├── AuthController.login() → Validate user
    └── Success → /dashboard?username=xxx
```

### B. Main Features Flow
```
📝 DASHBOARD → Input Mood
    ↓
🎯 MOOD ANALYSIS → Keyword + AI
    ↓
📊 MOOD RESULT → Show character + motivation
    ↓
📋 RIWAYAT → History table + filter
    ↑_______________|
    (Circular navigation)
```

## 4. TEKNOLOGI STACK

### A. Backend (Spring Boot)
```
🌱 Spring Boot 3.x
├── Spring MVC (Controllers)
├── Spring Data JPA (Repositories) 
├── Thymeleaf (Template Engine)
└── MySQL Connector

📦 Dependencies (pom.xml)
├── spring-boot-starter-web
├── spring-boot-starter-data-jpa
├── spring-boot-starter-thymeleaf
└── mysql-connector-j
```

### B. Frontend
```
🎨 Templates (Thymeleaf)
├── login.html, register.html
├── dashboard.html (input form)
├── mood-result.html (display result)
└── riwayat.html (history table)

🎨 Styling (CSS)
├── Bootstrap-like responsive design
├── Gradient backgrounds
└── Character images (assets/*.png)
```

### C. AI Integration
```
🐍 Python API (FastAPI)
├── sentiment_api.py
├── Port: 8000
├── Endpoint: POST /predict
└── Library: transformers (BERT model)

🔗 Java-Python Communication
├── SentimentService.java
├── RestTemplate HTTP client
└── JSON data exchange
```

## 5. DATA FLOW EXAMPLE

Input: "Hari ini aku sangat gembira!"
```
1. POST /input-mood
2. detectEmotionFromNote() → "gembira" (keyword found)
3. mapEmotionToMood() → "Very Positive"
4. Save to database: User + Mood + Note + Date
5. Redirect: /mood-result?mood=Very+Positive&emosiAsli=gembira
6. Display: Character image + "gembira" label + motivation
```

Input: "Today was quite normal"
```
1. POST /input-mood  
2. detectEmotionFromNote() → "" (no keyword)
3. SentimentService.analyze() → API call to Python
4. Python AI response → "Neutral"
5. Save to database
6. Display: Neutral character + motivation
```
