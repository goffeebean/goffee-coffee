import { useCallback, useEffect, useState } from 'react'
import { ApiClientError, deleteRoast, generateTastingNotes, getRoasts } from './api/client'
import type { RoastResponse, TastingNoteResponse } from './api/types'
import { RoastFormModal } from './components/RoastFormModal'
import { RoastTable } from './components/RoastTable'

type ModalState = { type: 'closed' } | { type: 'create' } | { type: 'edit'; roast: RoastResponse }

function App() {
  const [roasts, setRoasts] = useState<RoastResponse[]>([])
  const [listLoading, setListLoading] = useState(true)
  const [listError, setListError] = useState<string | null>(null)
  const [modalState, setModalState] = useState<ModalState>({ type: 'closed' })

  const loadRoasts = useCallback(async () => {
    setListLoading(true)
    setListError(null)
    try {
      setRoasts(await getRoasts())
    } catch (err) {
      setListError(err instanceof ApiClientError ? err.message : 'Failed to load roasts.')
    } finally {
      setListLoading(false)
    }
  }, [])

  useEffect(() => {
    loadRoasts()
  }, [loadRoasts])

  async function handleDelete(roast: RoastResponse) {
    if (!confirm(`Delete "${roast.name}"?`)) return
    try {
      await deleteRoast(roast.id)
      await loadRoasts()
    } catch (err) {
      setListError(err instanceof ApiClientError ? err.message : 'Failed to delete roast.')
    }
  }

  async function handleGenerate(roastId: number): Promise<TastingNoteResponse> {
    const result = await generateTastingNotes(roastId)
    setRoasts((prev) =>
      prev.map((r) => (r.id === roastId ? { ...r, tastingNotes: result.tastingNotes } : r)),
    )
    return result
  }

  function handleSaved() {
    setModalState({ type: 'closed' })
    loadRoasts()
  }

  return (
    <div className="min-h-screen bg-base-200">
      <div className="navbar bg-base-100 shadow-sm px-4">
        <span className="text-xl font-bold flex-1">Goffee's Coffee</span>
        <button className="btn btn-primary btn-sm" onClick={() => setModalState({ type: 'create' })}>
          Add Roast
        </button>
      </div>

      <div className="p-4">
        {listError && (
          <div className="alert alert-error mb-4">
            <span>{listError}</span>
            <button className="btn btn-sm" onClick={loadRoasts}>
              Retry
            </button>
          </div>
        )}

        {listLoading ? (
          <div className="flex justify-center py-12">
            <span className="loading loading-spinner loading-lg" />
          </div>
        ) : (
          <RoastTable
            roasts={roasts}
            onEdit={(roast) => setModalState({ type: 'edit', roast })}
            onDelete={handleDelete}
            onGenerate={handleGenerate}
          />
        )}
      </div>

      {modalState.type !== 'closed' && (
        <RoastFormModal
          key={modalState.type === 'edit' ? modalState.roast.id : 'create'}
          mode={modalState.type}
          initialRoast={modalState.type === 'edit' ? modalState.roast : undefined}
          onClose={() => setModalState({ type: 'closed' })}
          onSaved={handleSaved}
        />
      )}
    </div>
  )
}

export default App
