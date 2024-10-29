import { getCookie } from '$lib/utils/getCookie';

export async function showProducts() {
	console.log(document.cookie.split('=')[1]);
	try {
		const response = await fetch('http://localhost:8080/inventory/products/search', {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${getCookie('authToken')}`
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

export async function checkAuth() {
	try {
		const response = await fetch(`http://localhost:8080/inventory/products`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${getCookie('authToken')}`
			}
		});
		if (response.status === 400) {
			let new_response = { success: true, data: 'admin' };
			console.log(new_response);
			return new_response;
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

export async function uploadImage(file: File): Promise<string> {
	const formData = new FormData();
	formData.append('file', file); // `file` is a File object
	console.log('File to upload:', file);

	try {
		const response = await fetch('http://localhost:8080/inventory/products/upload', {
			method: 'POST',
			headers: {
				Authorization: `Bearer ${getCookie('authToken')}`
			},
			body: formData
		});

		if (response.ok) {
			const result = await response.json(); // Parse JSON response
			console.log('Upload successful:', result.url);
			return result.url; // Return the image URL from the response body
		} else {
			const errorResult = await response.json();
			console.error('Upload failed:', errorResult.error);
			return '';
		}
	} catch (error) {
		console.error('An error occurred:', (error as Error).message);
		return '';
	}
}

export async function createProduct(
	productName: string,
	description: string,
	imageUrl: string,
	price: number,
	categoryId: number,
	stockQuantity: number
): Promise<void> {
	try {
		const response = await fetch('http://localhost:8080/inventory/products', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${getCookie('authToken')}`
			},
			body: JSON.stringify({
				productName,
				description,
				imageUrl,
				price,
				categoryId,
				stockQuantity
			})
		});

		if (response.ok) {
			console.log('Product created successfully');
		} else {
			console.error('Failed to create product:', response.statusText);
		}
	} catch (error) {
		console.error('An error occurred:', (error as Error).message);
	}
}

export async function deleteProduct(productId: number): Promise<void> {
	try {
		const response = await fetch(`http://localhost:8080/inventory/products/${productId}`, {
			method: 'DELETE',
			headers: {
				Authorization: `Bearer ${document.cookie.split('=')[1]}`
			}
		});
	} catch (error) {
		console.error('An error occurred:', (error as Error).message);
	}
}
