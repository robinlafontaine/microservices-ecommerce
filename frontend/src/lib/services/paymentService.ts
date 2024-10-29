import type { PaymentRequestDTO, PaymentResponseDTO } from '$lib/types/paymentTypes';
import { getCookie } from '$lib/utils/getCookie';

const BASE_URL = 'http://localhost:8080/payments';

export async function initiatePayment(
	request: PaymentRequestDTO
): Promise<PaymentResponseDTO | null> {
	try {
		const response = await fetch(`${BASE_URL}/initiate`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${getCookie('authToken')}`
			},
			body: JSON.stringify(request)
		});

		if (response.ok) {
			return await response.json();
		} else {
			console.error('Error while initiating payment', await response.text());
			return null;
		}
	} catch (error) {
		console.error('Network error:', error);
		return null;
	}
}

export async function getPaymentStatus(paymentId: string): Promise<string | null> {
	try {
		const response = await fetch(`${BASE_URL}/${paymentId}/status`, {
			method: 'GET',
			headers: {
				Authorization: `Bearer ${getCookie('authToken')}`
			}
		});
		if (response.ok) {
			return await response.text();
		} else {
			console.error('Error while getting the status of the payment:', await response.text());
			return null;
		}
	} catch (error) {
		console.error('Network error:', error);
		return null;
	}
}

export async function confirmPayment(paymentId: string): Promise<string | null> {
	try {
		const response = await fetch(`${BASE_URL}/confirm?paymentId=${encodeURIComponent(paymentId)}`, {
			method: 'POST',
			headers: {
				Authorization: `Bearer ${getCookie('authToken')}`
			}
		});
		if (response.ok) {
			return await response.text();
		} else {
			console.error('Error while confirmation:', await response.text());
			return null;
		}
	} catch (error) {
		console.error('Network error:', error);
		return null;
	}
}
