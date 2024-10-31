import { deleteCookie, getCookie } from '$lib/utils/cookieUtils';

export async function handleLogin(email: string, password: string) {
	try {
		const response = await fetch('http://localhost:8080/auth/authenticate', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ email, password })
		});

		if (response.ok) {
			const result = await response.json();
			deleteCookie('authToken');
			return { success: true, data: result };
		} else {
			const error = await response.json();
			return { success: false, error };
		}
	} catch (err) {
		return {
			success: false,
			error: err instanceof Error ? err.message : 'An unknown error occurred'
		};
	}
}

export async function getUserId() {
	try {
		const response = await fetch('http://localhost:8080/auth/user/id', {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${getCookie('authToken')}`
			}
		});

		if (response.ok) {
			const result = await response.json();
			return { success: true, data: result.id };
		} else {
			const error = await response.json();
			return { success: false, error };
		}
	} catch (err) {
		return {
			success: false,
			error: err instanceof Error ? err.message : 'An unknown error occurred'
		};
	}
}
