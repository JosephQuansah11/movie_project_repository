import { javaApi, bearerHeaders, pythonApi } from '../../api/client'
import type { HistoryItem, Insights, Movie } from '../../types'

export async function getMovies(): Promise<Movie[]> {
  const { data } = await javaApi.get<{ movies?: Movie[] } | Movie[]>('/movies')
  return Array.isArray(data) ? data : data.movies ?? []
}

export async function getHistory(token: string): Promise<HistoryItem[]> {
  const { data } = await javaApi.get<{ data?: HistoryItem[]; history?: HistoryItem[] }>('/users/me/history', { headers: bearerHeaders(token) })
  return data.data ?? data.history ?? []
}

export async function getInsights(): Promise<Insights> {
  const { data } = await pythonApi.get<Insights>('/api/insights')
  return data
}
