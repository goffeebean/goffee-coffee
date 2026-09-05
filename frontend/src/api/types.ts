// Mirrors the backend DTOs (see coffee/EXERCISE.md / dto/RoastRequest.java, RoastResponse.java).
// Keep this in sync with whatever shape the Java DTOs end up with.

export type RoastLevel = 'LIGHT' | 'MEDIUM' | 'DARK'

export interface RoastResponse {
  id: number
  name: string
  origin: string
  roastLevel: RoastLevel
  price: number
  tastingNotes: string | null
}

export interface RoastRequest {
  name: string
  origin: string
  roastLevel: RoastLevel
  price: number
  tastingNotes: string | null
}

export interface ApiError {
  timestamp: string
  status: number
  error: string
  message: string
  details: string[]
}
