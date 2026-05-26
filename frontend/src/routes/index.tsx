import { createFileRoute } from '@tanstack/react-router'
import { OpsBoardApp } from '../components/OpsBoardApp'

export const Route = createFileRoute('/')({
  component: OpsBoardApp,
})

