import { csrfHeaders, javaApi, bearerHeaders } from '../../api/client'
import type { Movie } from '../../types'

export async function getMovies(): Promise<Movie[]> {
  const { data } = await javaApi.get<{ movies?: Movie[] } | Movie[]>('/movies')
  return Array.isArray(data) ? data : data.movies ?? []
}

export async function addMovie(movie: Movie, token: string): Promise<void> {
  await javaApi.post('/movies', movie, { headers: { ...bearerHeaders(token), ...await csrfHeaders() } })
}

export async function updateMovie(id: number, movie: Movie, token: string): Promise<Movie> {
  const { data } = await javaApi.put<Movie>(`/movies/${id}`, movie, { headers: { ...bearerHeaders(token), ...await csrfHeaders() } })
  return data
}
