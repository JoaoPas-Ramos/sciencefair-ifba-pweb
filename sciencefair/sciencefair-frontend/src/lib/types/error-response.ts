export type ErrorResponse = {
  title: string,
  description: string,
  fieldErrors: Record<string, string[]>
}
