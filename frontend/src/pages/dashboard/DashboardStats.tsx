import { ArrowUpRight, Clock3, Film, Users } from 'lucide-react'
import type { HistoryItem, Insights, Movie } from '../../types'

export function DashboardStats({ movies, history, insights }: { movies: Movie[]; history: HistoryItem[]; insights?: Insights }) {
  return <section className="stat-grid"><div className="stat-card"><span className="stat-icon coral"><Film size={18} /></span><strong>{(insights?.totalMovies ?? movies.length) || '—'}</strong><span>Movies in catalogue</span><small><ArrowUpRight size={13} />Java resource</small></div><div className="stat-card"><span className="stat-icon mint"><Clock3 size={18} /></span><strong>{history.length || '—'}</strong><span>On your timeline</span><small>Across all sessions</small></div><div className="stat-card"><span className="stat-icon gold"><Users size={18} /></span><strong>{insights?.averageRating || '—'}</strong><span>Average rating</span><small>Python insight</small></div></section>
}
