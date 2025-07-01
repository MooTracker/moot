from fastapi import FastAPI
from pydantic import BaseModel
import uvicorn
import re

app = FastAPI()

class TextRequest(BaseModel):
    text: str

class SentimentResponse(BaseModel):
    stars: int
    sentiment: str

def analyze_sentiment(text):
    """
    Analisis sederhana berdasarkan kata kunci dalam bahasa Indonesia
    Mengembalikan rating 1-5 stars
    """
    text_lower = text.lower()
    
    # Kata-kata positif kuat (5 stars)
    very_positive_words = [
        'sangat senang', 'sangat bahagia', 'sangat gembira', 'luar biasa', 
        'fantastis', 'amazing', 'perfect', 'terbaik', 'hebat sekali',
        'sangat puas', 'sangat beruntung', 'sangat bersyukur'
    ]
    
    # Kata-kata positif (4 stars)
    positive_words = [
        'senang', 'bahagia', 'gembira', 'suka', 'bagus', 'baik',
        'puas', 'beruntung', 'bersyukur', 'nyaman', 'tenang',
        'optimis', 'semangat', 'antusias'
    ]
    
    # Kata-kata negatif kuat (1 star)
    very_negative_words = [
        'sangat sedih', 'sangat marah', 'sangat kecewa', 'depresi',
        'putus asa', 'hancur', 'terpuruk', 'sangat buruk', 'terrible',
        'sangat lelah', 'sangat stress', 'sangat takut'
    ]
    
    # Kata-kata negatif (2 stars)
    negative_words = [
        'sedih', 'marah', 'kecewa', 'kesal', 'jengkel', 'buruk',
        'lelah', 'capek', 'stress', 'cemas', 'khawatir', 'takut',
        'galau', 'bingung', 'frustrasi'
    ]
    
    # Kata-kata netral (3 stars)
    neutral_words = [
        'biasa', 'biasa saja', 'netral', 'standar', 'normal',
        'lumayan', 'cukup', 'ok', 'oke', 'so-so'
    ]
    
    # Hitung score berdasarkan kata kunci
    score = 3  # default netral
    
    # Cek kata-kata sangat positif
    for word in very_positive_words:
        if word in text_lower:
            score = 5
            break
    
    # Cek kata-kata positif
    if score == 3:
        for word in positive_words:
            if word in text_lower:
                score = 4
                break
    
    # Cek kata-kata sangat negatif
    if score == 3:
        for word in very_negative_words:
            if word in text_lower:
                score = 1
                break
    
    # Cek kata-kata negatif
    if score == 3:
        for word in negative_words:
            if word in text_lower:
                score = 2
                break
    
    # Cek kata-kata netral
    if score == 3:
        for word in neutral_words:
            if word in text_lower:
                score = 3
                break
    
    # Jika tidak ada kata kunci yang cocok, analisis berdasarkan pola
    if score == 3:
        # Hitung kata positif vs negatif
        positive_count = sum(1 for word in positive_words + very_positive_words if word in text_lower)
        negative_count = sum(1 for word in negative_words + very_negative_words if word in text_lower)
        
        if positive_count > negative_count:
            score = 4
        elif negative_count > positive_count:
            score = 2
        else:
            score = 3
    
    return score

@app.post("/predict")
async def predict_sentiment(request: TextRequest):
    stars = analyze_sentiment(request.text)
    
    sentiment_labels = {
        1: "Very Negative",
        2: "Negative", 
        3: "Neutral",
        4: "Positive",
        5: "Very Positive"
    }
    
    return {
        "stars": stars,
        "sentiment": sentiment_labels[stars]
    }

@app.get("/")
async def root():
    return {"message": "Sentiment Analysis API is running"}

if __name__ == "__main__":
    uvicorn.run(app, host="127.0.0.1", port=8000)
