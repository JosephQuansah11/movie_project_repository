import { History, Play } from 'lucide-react'
import { useEffect, useState } from 'react'
import { getHistory, getInsights, getMovies } from './dashboardApi'
import { useAuth } from '../../context/AuthContext'
import type { HistoryItem, Insights, Movie } from '../../types'
import { MovieCard, SearchField } from '../../components/UI'
import { DashboardStats } from './DashboardStats'

export function DashboardPage({ onExplore }: { onExplore: () => void }) {
  const auth = useAuth(); const [movies, setMovies] = useState<Movie[]>([]); const [history, setHistory] = useState<HistoryItem[]>([]); const [insights, setInsights] = useState<Insights>(); const [query, setQuery] = useState(''); const [error, setError] = useState('')
  useEffect(() => { getMovies().then(setMovies).catch(() => setError('Java API is unavailable. Showing your workspace shell.')); getInsights().then(setInsights).catch(() => undefined); if (auth.token) getHistory(auth.token).then(setHistory).catch(() => undefined) }, [auth.token])
  const visible = movies.filter((movie) => `${movie.title} ${movie.genre} ${movie.director}`.toLowerCase().includes(query.toLowerCase())).slice(0, 4)
  return <div className="page"><section className="welcome"><div><div className="eyebrow">WEDNESDAY · 02 SEPTEMBER 2026</div><h1>Good evening{auth.profile?.firstName ? `, ${auth.profile.firstName}` : ''}.</h1><p>Make room for a story worth staying up for.</p></div><button className="primary-button" onClick={onExplore}><Play size={16} />Explore library</button></section>{error && <div className="notice">{error}</div>}<DashboardStats movies={movies} history={history} insights={insights} /><section className="section-heading"><div><div className="eyebrow">FROM THE CATALOGUE</div><h2>Find your next favorite</h2></div><SearchField value={query} onChange={setQuery} /></section><div className="movie-grid">{visible.map((movie) => <MovieCard key={movie.id ?? movie.title} movie={movie} />)}{!visible.length && <div className="empty-state">No movies found in the Java catalogue yet.</div>}</div><section className="activity-panel"><div className="section-heading"><div><div className="eyebrow">PRIVATE TIMELINE</div><h2>Recent activity</h2></div><History size={20} /></div>{history.length ? history.slice(0, 3).map((item) => <div className="activity-row" key={item.id ?? item.movieTitle}><span className="activity-marker" /><div><strong>{item.movieTitle}</strong><span>{item.status ?? 'Saved'} · {item.genre ?? 'Movie'}</span></div></div>) : <p className="muted">Sign in to sync your watched and to-watch history.</p>}</section></div>
}
