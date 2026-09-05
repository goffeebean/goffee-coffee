import { useState } from 'react'
import { ApiClientError } from '../api/client'
import type { RoastLevel, RoastResponse, TastingNoteResponse } from '../api/types'

interface RoastTableProps {
  roasts: RoastResponse[]
  onEdit: (roast: RoastResponse) => void
  onDelete: (roast: RoastResponse) => void
  onGenerate: (roastId: number) => Promise<TastingNoteResponse>
}

const currencyFormatter = new Intl.NumberFormat('en-US', {
  style: 'currency',
  currency: 'USD',
})

const LEVEL_BADGE_CLASS: Record<RoastLevel, string> = {
  LIGHT: 'badge-info',
  MEDIUM: 'badge-warning',
  DARK: 'badge-neutral',
}

export function RoastTable({ roasts, onEdit, onDelete, onGenerate }: RoastTableProps) {
  if (roasts.length === 0) {
    return (
      <div className="overflow-x-auto">
        <table className="table table-zebra">
          <TableHead />
          <tbody>
            <tr>
              <td colSpan={6} className="text-center text-base-content/60 py-8">
                No roasts yet — add one to get started.
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    )
  }

  return (
    <div className="overflow-x-auto">
      <table className="table table-zebra">
        <TableHead />
        <tbody>
          {roasts.map((roast) => (
            <RoastTableRow
              key={roast.id}
              roast={roast}
              onEdit={onEdit}
              onDelete={onDelete}
              onGenerate={onGenerate}
            />
          ))}
        </tbody>
      </table>
    </div>
  )
}

function TableHead() {
  return (
    <thead>
      <tr>
        <th>Name</th>
        <th>Origin</th>
        <th>Roast level</th>
        <th>Price</th>
        <th>Tasting notes</th>
        <th>Actions</th>
      </tr>
    </thead>
  )
}

interface RoastTableRowProps {
  roast: RoastResponse
  onEdit: (roast: RoastResponse) => void
  onDelete: (roast: RoastResponse) => void
  onGenerate: (roastId: number) => Promise<TastingNoteResponse>
}

function RoastTableRow({ roast, onEdit, onDelete, onGenerate }: RoastTableRowProps) {
  const [isGenerating, setIsGenerating] = useState(false)
  const [generateError, setGenerateError] = useState<string | null>(null)

  async function handleGenerate() {
    setIsGenerating(true)
    setGenerateError(null)
    try {
      await onGenerate(roast.id)
    } catch (err) {
      if (err instanceof ApiClientError && err.status === 503) {
        setGenerateError('Ollama is unavailable — is it running?')
      } else {
        setGenerateError('Could not generate tasting notes.')
      }
    } finally {
      setIsGenerating(false)
    }
  }

  return (
    <tr>
      <td>{roast.name}</td>
      <td>{roast.origin}</td>
      <td>
        <span className={`badge ${LEVEL_BADGE_CLASS[roast.roastLevel]}`}>{roast.roastLevel}</span>
      </td>
      <td>{currencyFormatter.format(roast.price)}</td>
      <td>
        <span className={roast.tastingNotes ? '' : 'text-base-content/60'}>
          {roast.tastingNotes ?? '—'}
        </span>
        {generateError && <p className="text-error text-xs mt-1">{generateError}</p>}
      </td>
      <td className="flex gap-2">
        <button className="btn btn-xs" onClick={() => onEdit(roast)}>
          Edit
        </button>
        <button className="btn btn-xs btn-error btn-outline" onClick={() => onDelete(roast)}>
          Delete
        </button>
        <button
          className="btn btn-xs btn-secondary btn-outline"
          onClick={handleGenerate}
          disabled={isGenerating}
        >
          {isGenerating && <span className="loading loading-spinner loading-xs" />}
          Generate
        </button>
      </td>
    </tr>
  )
}
