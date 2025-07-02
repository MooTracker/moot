# PANDUAN MENJALANKAN MOODTRACKER

## PERSIAPAN
1. Pastikan XAMPP sudah terinstall dan berjalan
2. Start Apache dan MySQL di XAMPP Control Panel
3. Buat database 'moodtracker' di phpMyAdmin (jika belum ada)

## CARA MENJALANKAN

### OPSI 1: Menggunakan Batch Files (RECOMMENDED)
1. Jalankan `run_api.bat` terlebih dahulu (untuk sentiment analysis API)
2. Tunggu sampai muncul "Uvicorn running on http://127.0.0.1:8000"
3. Buka terminal/command prompt baru
4. Jalankan `run.bat` (untuk Spring Boot aplikasi)

### OPSI 2: Manual via Terminal
1. Buka terminal/command prompt
2. Jalankan API Python:
   ```
   cd "d:\moot (1)"
   python sentiment_api.py
   ```
3. Buka terminal baru untuk Spring Boot:
   ```
   cd "d:\moot (1)\moot"
   mvn spring-boot:run
   ```

## AKSES APLIKASI
- Buka browser: http://localhost:8080
- Akan redirect otomatis ke halaman login
- Register user baru atau login dengan user yang sudah ada

## FITUR YANG TERSEDIA
1. **Login & Register** - Autentikasi user
2. **Dashboard** - Input catatan mood harian
3. **Analisis Otomatis** - Deteksi sentiment dan emosi dari catatan
4. **Hasil Mood** - Tampilan karakter visual dan motivasi
5. **Riwayat** - List catatan dan grafik mood
6. **Filter** - Filter riwayat berdasarkan tanggal

## TROUBLESHOOTING
- Jika database error: pastikan MySQL running dan database 'moodtracker' sudah dibuat
- Jika sentiment analysis error: pastikan API Python berjalan di port 8000
- Jika tampilan aneh: clear browser cache dan refresh

## TEKNOLOGI
- Frontend: HTML, CSS, JavaScript, Thymeleaf
- Backend: Spring Boot, JPA/Hibernate
- Database: MySQL
- AI: Python FastAPI untuk sentiment analysis
