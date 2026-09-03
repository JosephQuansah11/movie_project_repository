import os
from collections import Counter
from typing import Any

import httpx
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(title='Fable data service', version='1.0.0')
JAVA_API_URL = os.getenv('JAVA_API_URL', 'http://localhost:8080').rstrip('/')
app.add_middleware(
    CORSMiddleware,
    allow_origins=os.getenv('FRONTEND_ORIGIN', 'http://localhost:5173').split(','),
    allow_credentials=True,
    allow_methods=['GET'],
    allow_headers=['*'],
)


async def java_movies() -> list[dict[str, Any]]:
    try:
        async with httpx.AsyncClient(timeout=8) as client:
            response = await client.get(f'{JAVA_API_URL}/movies')
            response.raise_for_status()
            payload = response.json()
            return payload if isinstance(payload, list) else payload.get('movies', [])
    except httpx.HTTPError as error:
        raise HTTPException(status_code=502, detail=f'Java movie API unavailable: {error}') from error


@app.get('/health')
async def health() -> dict[str, str]:
    return {'status': 'ok', 'upstream': JAVA_API_URL}


@app.get('/api/insights')
async def insights() -> dict[str, Any]:
    movies = await java_movies()
    genres = Counter(movie.get('genre') or 'Uncategorized' for movie in movies)
    years = [movie.get('releaseYear') for movie in movies if movie.get('releaseYear')]
    return {'totalMovies': len(movies), 'averageRating': round(sum(movie.get('rating', 0) for movie in movies) / len(movies), 2) if movies else 0, 'genres': [{'name': name, 'count': count} for name, count in genres.most_common()], 'latestReleaseYear': max(years) if years else None}


@app.get('/api/catalogue')
async def catalogue() -> dict[str, Any]:
    return {'source': 'java', 'items': await java_movies()}