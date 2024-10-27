export async function showProducts() {
	console.log(document.cookie.split('=')[1]);
	try {
		const response = await fetch('http://localhost:8080/inventory/products/search', {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${document.cookie.split('=')[1]}`
			}
		});

		if (response.ok) {
			const result = await response.json();
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
