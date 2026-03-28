export type User = {
  id?: number,
  name: string,
  email: string,
  password?: string | null,
  role?: string,
}

export type UserRegistration = {
  name: string,
  email: string,
  password: string
}

export type LoginPayload = {
  email: string,
  password: string
}

export type LoginResponse = {
  userId: number,
  name: string,
  email: string,
  role: string,
  token: string,
  tokenType: string
}

export type UserProfileUpdate = {
  name: string,
  email: string
}

export type UserPasswordUpdate = {
  currentPassword: string,
  newPassword: string
}
