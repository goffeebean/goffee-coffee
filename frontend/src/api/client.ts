import type { ApiError, RoastRequest, RoastResponse, TastingNoteResponse } from './types'

export class ApiClientError extends Error {
  readonly status: number
  readonly error: string
  readonly details: string[]

  constructor(apiError: ApiError) {
    super(apiError.message)
    this.name = 'ApiClientError'
    this.status = apiError.status
    this.error = apiError.error
    this.details = apiError.details
  }
}

const BASE_URL = '/api/v1/roasts'

async function toApiClientError(response: Response): Promise<ApiClientError> {
  try {
    const body = (await response.json()) as ApiError
    return new ApiClientError(body)
  } catch {
    return new ApiClientError({
      timestamp: new Date().toISOString(),
      status: response.status,
      error: response.statusText || 'Error',
      message: `Request failed with status ${response.status}`,
      details: [],
    })
  }
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, {
    ...init,
    headers: { 'Content-Type': 'application/json', ...init?.headers },
  })

  if (!response.ok) {
    throw await toApiClientError(response)
  }
  if (response.status === 204) {
    return undefined as T
  }
  return (await response.json()) as T
}

export function getRoasts(): Promise<RoastResponse[]> {
  return request<RoastResponse[]>(BASE_URL)
}

export function getRoast(id: number): Promise<RoastResponse> {
  return request<RoastResponse>(`${BASE_URL}/${id}`)
}

export function createRoast(payload: RoastRequest): Promise<RoastResponse> {
  return request<RoastResponse>(BASE_URL, {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function updateRoast(id: number, payload: RoastRequest): Promise<RoastResponse> {
  return request<RoastResponse>(`${BASE_URL}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function deleteRoast(id: number): Promise<void> {
  return request<void>(`${BASE_URL}/${id}`, { method: 'DELETE' })
}

export function generateTastingNotes(id: number): Promise<TastingNoteResponse> {
  return request<TastingNoteResponse>(`${BASE_URL}/${id}/tasting-notes/generate`, {
    method: 'POST',
  })
}
