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
	console.error('Document is not defined');
	return null;
}

export function deleteCookie(name: string): void {
	if (typeof document !== 'undefined') {
		document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
	} else {
		console.error('Document is not defined');
	}
}

export function setCookie(name: string, value: string, seconds: number): void {
	if (typeof document !== 'undefined') {
		const date = new Date();
		date.setTime(date.getTime() + seconds * 1000);
		document.cookie = `${name}=${value}; expires=${date.toUTCString()}; path=/;`;
	} else {
		console.error('Document is not defined');
	}
}

export function deleteAllCookies(): void {
	if (typeof document !== 'undefined') {
		const cookies = document.cookie.split(';');
		for (const cookie of cookies) {
			const [cookieName] = cookie.trim().split('=');
			deleteCookie(cookieName);
		}
	} else {
		console.error('Document is not defined');
	}
}
