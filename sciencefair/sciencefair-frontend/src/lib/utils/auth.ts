import { browser } from '$app/environment';
import type { LoginResponse } from '$lib/types/user';

const COOKIE_MAX_AGE = 60 * 60 * 24 * 7;

export const AUTH_COOKIE = {
	token: 'ifba_blog_token',
	tokenType: 'ifba_blog_token_type',
	userId: 'ifba_blog_user_id',
	name: 'ifba_blog_user_name',
	email: 'ifba_blog_user_email',
	role: 'ifba_blog_user_role'
} as const;

export function saveAuthSession(session: LoginResponse): void {
	if (!browser) {
		return;
	}

	setCookie(AUTH_COOKIE.token, session.token);
	setCookie(AUTH_COOKIE.tokenType, session.tokenType);
	setCookie(AUTH_COOKIE.userId, String(session.userId));
	setCookie(AUTH_COOKIE.name, session.name);
	setCookie(AUTH_COOKIE.email, session.email);
	setCookie(AUTH_COOKIE.role, session.role);
	notifyAuthChange();
}

export function clearAuthSession(): void {
	if (!browser) {
		return;
	}

	Object.values(AUTH_COOKIE).forEach((cookieName) => {
		document.cookie = `${cookieName}=; Path=/; Max-Age=0; SameSite=Lax`;
	});

	notifyAuthChange();
}

export function getAuthToken(): string | null {
	if (!browser) {
		return null;
	}

	const token = getCookie(AUTH_COOKIE.token);
	const tokenType = getCookie(AUTH_COOKIE.tokenType);

	if (!token || !tokenType) {
		return null;
	}

	return `${tokenType} ${token}`;
}

export function getAuthenticatedUserId(): number | null {
	if (!browser) {
		return null;
	}

	const rawUserId = getCookie(AUTH_COOKIE.userId);

	if (!rawUserId) {
		return null;
	}

	const userId = Number(rawUserId);
	return Number.isFinite(userId) && userId > 0 ? userId : null;
}

export function getAuthenticatedUserName(): string | null {
	if (!browser) {
		return null;
	}

	const name = getCookie(AUTH_COOKIE.name)?.trim();
	return name ? name : null;
}

export function getAuthenticatedUserEmail(): string | null {
	if (!browser) {
		return null;
	}

	const email = getCookie(AUTH_COOKIE.email)?.trim();
	return email ? email : null;
}

export function getAuthenticatedUserRole(): string | null {
	if (!browser) {
		return null;
	}

	const role = getCookie(AUTH_COOKIE.role)?.trim();
	return role ? role : null;
}

export function isAuthenticated(): boolean {
	return getAuthenticatedUserId() !== null && getAuthToken() !== null;
}

export function isAdmin(): boolean {
	return getAuthenticatedUserRole() === 'ADMIN';
}

export function updateAuthenticatedUserProfile(name: string, email: string): void {
	if (!browser) {
		return;
	}

	setCookie(AUTH_COOKIE.name, name);
	setCookie(AUTH_COOKIE.email, email);
	notifyAuthChange();
}

function setCookie(name: string, value: string): void {
	document.cookie =
		`${name}=${encodeURIComponent(value)}; Path=/; Max-Age=${COOKIE_MAX_AGE}; SameSite=Lax`;
}

function getCookie(name: string): string | null {
	const prefix = `${name}=`;
	const cookie = document.cookie
		.split('; ')
		.find((entry) => entry.startsWith(prefix));

	return cookie ? decodeURIComponent(cookie.slice(prefix.length)) : null;
}

function notifyAuthChange(): void {
	window.dispatchEvent(new CustomEvent('authchange'));
}
