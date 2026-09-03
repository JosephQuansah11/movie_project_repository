import { bearerHeaders, csrfHeaders, javaApi } from '../../api/client'
import type { UserProfile } from '../../types'

export async function registerUser(profile: UserProfile & { password: string }): Promise<UserProfile> {
  const { data } = await javaApi.post<UserProfile>('/users', profile, { headers: await csrfHeaders() })
  return data
}

export async function syncCurrentUser(token: string): Promise<UserProfile> {
  const { data } = await javaApi.get<UserProfile>('/users/me', { headers: bearerHeaders(token) })
  return data
}
