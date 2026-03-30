export function getLoggedUser() {
	if (typeof localStorage === 'undefined') return null;

	const raw = localStorage.getItem('user');
	return raw ? JSON.parse(raw) : null;
}

export function isLoggedIn(): boolean {
	return !!getLoggedUser();
}
