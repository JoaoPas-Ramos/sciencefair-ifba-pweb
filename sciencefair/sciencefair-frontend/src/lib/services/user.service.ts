import type {
  LoginPayload,
  LoginResponse,
  User,
  UserPasswordUpdate,
  UserProfileUpdate,
  UserRegistration
} from "$lib/types/user";
import { getAuthenticatedUserId } from "$lib/utils/auth";
import { API_PATH, post, put } from "$lib/utils/http-utils";

export async function createUser(user: UserRegistration): Promise<User> {
  return await post('/auth/register', user);
}

export async function loginUser(credentials: LoginPayload): Promise<any> {
  return await post('/auth/login', {
    email: credentials.email,
    senha: credentials.password
  });
}

export async function updateLoggedUserProfile(payload: UserProfileUpdate): Promise<User> {
  const userId = requireAuthenticatedUserId();
  return await put(`${API_PATH.User}/${userId}/profile`, payload, undefined, true);
}

export async function updateLoggedUserPassword(payload: UserPasswordUpdate): Promise<void> {
  const userId = requireAuthenticatedUserId();
  return await put(`${API_PATH.User}/${userId}/password`, payload, undefined, true);
}

function requireAuthenticatedUserId(): number {
  const userId = getAuthenticatedUserId();

  if (!userId) {
    throw new Error('Authenticated user id not found.');
  }

  return userId;
}
