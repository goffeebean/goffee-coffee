import { useEffect, useRef, useState } from 'react'
import type { FormEvent } from 'react'
import { ApiClientError, createRoast, updateRoast } from '../api/client'
import type { RoastLevel, RoastRequest, RoastResponse } from '../api/types'

interface RoastFormModalProps {
  mode: 'create' | 'edit'
  initialRoast?: RoastResponse
  onClose: () => void
  onSaved: (roast: RoastResponse) => void
}

interface FormFields {
  name: string
  origin: string
  roastLevel: RoastLevel
  price: string
  tastingNotes: string
}

function initialFields(initialRoast?: RoastResponse): FormFields {
  if (initialRoast) {
    return {
      name: initialRoast.name,
      origin: initialRoast.origin,
      roastLevel: initialRoast.roastLevel,
      price: String(initialRoast.price),
      tastingNotes: initialRoast.tastingNotes ?? '',
    }
  }
  return { name: '', origin: '', roastLevel: 'MEDIUM', price: '', tastingNotes: '' }
}

function parseFieldErrors(details: string[]): Record<string, string> {
  const fieldErrors: Record<string, string> = {}
  for (const detail of details) {
    const separatorIndex = detail.indexOf(': ')
    if (separatorIndex === -1) continue
    const field = detail.slice(0, separatorIndex)
    const message = detail.slice(separatorIndex + 2)
    fieldErrors[field] = message
  }
  return fieldErrors
}

export function RoastFormModal({ mode, initialRoast, onClose, onSaved }: RoastFormModalProps) {
  const dialogRef = useRef<HTMLDialogElement>(null)
  const [fields, setFields] = useState<FormFields>(() => initialFields(initialRoast))
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({})
  const [formError, setFormError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    dialogRef.current?.showModal()
  }, [])

  function updateField<K extends keyof FormFields>(key: K, value: FormFields[K]) {
    setFields((prev) => ({ ...prev, [key]: value }))
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitting(true)
    setFormError(null)
    setFieldErrors({})

    const payload: RoastRequest = {
      name: fields.name,
      origin: fields.origin,
      roastLevel: fields.roastLevel,
      price: Number(fields.price),
      tastingNotes: fields.tastingNotes.trim() === '' ? null : fields.tastingNotes,
    }

    try {
      const saved =
        mode === 'edit' && initialRoast
          ? await updateRoast(initialRoast.id, payload)
          : await createRoast(payload)
      onSaved(saved)
      onClose()
    } catch (err) {
      if (err instanceof ApiClientError && err.status === 400) {
        setFieldErrors(parseFieldErrors(err.details))
        setFormError(err.message)
      } else if (err instanceof ApiClientError) {
        setFormError(err.message)
      } else {
        setFormError('Something went wrong. Please try again.')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <dialog ref={dialogRef} className="modal" onClose={onClose}>
      <div className="modal-box">
        <h3 className="font-bold text-lg">{mode === 'edit' ? 'Edit Roast' : 'Add Roast'}</h3>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4 mt-4">
          {formError && (
            <div className="alert alert-error text-sm">
              <span>{formError}</span>
            </div>
          )}

          <fieldset className="fieldset">
            <legend className="fieldset-legend">Name</legend>
            <input
              className="input w-full"
              value={fields.name}
              onChange={(e) => updateField('name', e.target.value)}
              required
            />
            {fieldErrors.name && <p className="text-error text-sm mt-1">{fieldErrors.name}</p>}
          </fieldset>

          <fieldset className="fieldset">
            <legend className="fieldset-legend">Origin</legend>
            <input
              className="input w-full"
              value={fields.origin}
              onChange={(e) => updateField('origin', e.target.value)}
              required
            />
            {fieldErrors.origin && <p className="text-error text-sm mt-1">{fieldErrors.origin}</p>}
          </fieldset>

          <fieldset className="fieldset">
            <legend className="fieldset-legend">Roast level</legend>
            <select
              className="select w-full"
              value={fields.roastLevel}
              onChange={(e) => updateField('roastLevel', e.target.value as RoastLevel)}
            >
              <option value="LIGHT">Light</option>
              <option value="MEDIUM">Medium</option>
              <option value="DARK">Dark</option>
            </select>
            {fieldErrors.roastLevel && (
              <p className="text-error text-sm mt-1">{fieldErrors.roastLevel}</p>
            )}
          </fieldset>

          <fieldset className="fieldset">
            <legend className="fieldset-legend">Price</legend>
            <input
              type="number"
              step="0.01"
              min="0"
              className="input w-full"
              value={fields.price}
              onChange={(e) => updateField('price', e.target.value)}
              required
            />
            {fieldErrors.price && <p className="text-error text-sm mt-1">{fieldErrors.price}</p>}
          </fieldset>

          <fieldset className="fieldset">
            <legend className="fieldset-legend">Tasting notes</legend>
            <textarea
              className="textarea w-full"
              rows={3}
              value={fields.tastingNotes}
              onChange={(e) => updateField('tastingNotes', e.target.value)}
            />
          </fieldset>

          <div className="modal-action">
            <button type="button" className="btn" onClick={() => dialogRef.current?.close()}>
              Cancel
            </button>
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting && <span className="loading loading-spinner loading-xs" />}
              Save
            </button>
          </div>
        </form>
      </div>
      <form method="dialog" className="modal-backdrop">
        <button>close</button>
      </form>
    </dialog>
  )
}
