import { Plus, SlidersHorizontal } from 'lucide-react'
import { useEffect, useState } from 'react'
import { addMovie, getMovies } from './libraryApi'
import type { FormField, Movie } from '../../types'
import { DynamicForm, MovieCard, SearchField } from '../../components/UI'
import { useAuth } from '../../context/AuthContext'

type MovieDraft = Pick<Movie, 'title' | 'genre' | 'director' | 'description'>
const movieFields: FormField<MovieDraft>[] = [{ name: 'title', label: 'Title', required: true, placeholder: 'e.g. The Last Horizon' }, { name: 'genre', label: 'Genre', type: 'select', options: ['Drama', 'Comedy', 'Sci-Fi', 'Documentary'] }, { name: 'director', label: 'Director', placeholder: 'Who made it?' }, { name: 'description', label: 'Description', type: 'textarea', placeholder: 'A short note about the film' }]
export function LibraryPage() {
  const auth = useAuth(); const [movies, setMovies] = useState<Movie[]>([]); const [query, setQuery] = useState(''); const [showForm, setShowForm] = useState(false); const [error, setError] = useState('')
  useEffect(() => { getMovies().then(setMovies).catch(() => undefined) }, [])
  const saveMovie = async (movie: Pick<Movie, 'title' | 'genre' | 'director' | 'description'>) => { if (!auth.token) return; try { await addMovie(movie, auth.token); setShowForm(false); setError(''); setMovies(await getMovies()) } catch { setError('The Java API rejected this movie. Confirm your ADMIN role is present in Keycloak.') } }
  const filtered = movies.filter((movie) => `${movie.title} ${movie.genre} ${movie.director}`.toLowerCase().includes(query.toLowerCase()))
  return <div className="page"><section className="page-heading"><div><div className="eyebrow">JAVA RESOURCE · MOVIES</div><h1>Movie library</h1><p>Browse the catalogue managed by your Java service.</p></div>{auth.isAdmin && <button className="primary-button" onClick={() => setShowForm(!showForm)}><Plus size={16} />Add a title</button>}</section>{error && <div className="notice">{error}</div>}{showForm && auth.isAdmin && <section className="form-panel"><div className="eyebrow">ADMIN · NEW CATALOGUE ENTRY</div><h2>Describe the story</h2><DynamicForm fields={movieFields} initialValue={{ title: '', genre: '', director: '', description: '' }} submitLabel="Save movie" onSubmit={saveMovie} /></section>}<div className="library-toolbar"><SearchField value={query} onChange={setQuery} /><button className="quiet-button"><SlidersHorizontal size={16} />Filters</button><span className="result-count">{filtered.length} titles</span></div><div className="movie-grid library-grid">{filtered.map((movie) => <MovieCard key={movie.id ?? movie.title} movie={movie} />)}{!filtered.length && <div className="empty-state">The catalogue is empty or your search returned no matches.</div>}</div></div>
}
