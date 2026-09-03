export interface Movie {
  id?: number
  title: string
  genre?: string
  language?: string
  director?: string
  description?: string
  isSeasonal?: boolean
  rating?: number
  releaseYear?: number
  price?: number
  duration?: number
  playUrl?: string
}

export interface HistoryItem {
  id?: number
  movieTitle: string
  genre?: string
  status?: string
  watchUrl?: string
  createdAt?: string
}

export interface UserProfile {
  id?: number
  username: string
  email?: string
  firstName?: string
  lastName?: string
}

export interface FormField<T> {
  name: keyof T & string
  label: string
  type?: 'text' | 'email' | 'password' | 'number' | 'textarea' | 'select'
  placeholder?: string
  options?: string[]
  required?: boolean
}

export interface Insights {
  totalMovies: number
  averageRating: number
  latestReleaseYear?: number
  genres: { name: string; count: number }[]
}