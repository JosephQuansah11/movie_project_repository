# Fable Python data service

This service is outside the frontend package. It reads movie data from the Java API and exposes derived resources for the dashboard.

```powershell
cd python-service
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
$env:JAVA_API_URL = "http://localhost:8080"
uvicorn main:app --reload --port 8000
```

Endpoints: `GET /health`, `GET /api/catalogue`, and `GET /api/insights`.

Keycloak authentication and user history remain owned by Java. The frontend sends a bearer token for `/users/me/history`.