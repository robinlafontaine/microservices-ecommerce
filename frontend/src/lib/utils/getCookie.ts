export function getCookie(name: string): string | null {
	if (typeof document !== 'undefined') {
		const cookies = document.cookie.split(';');
		for (const cookie of cookies) {
			const [cookieName, cookieValue] = cookie.trim().split('=');
			if (cookieName === name) {
				return decodeURIComponent(cookieValue);
			}
		}
		return null;
	}
	return null;
}
