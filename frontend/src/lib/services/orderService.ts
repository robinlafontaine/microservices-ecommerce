import { getCookie } from '$lib/utils/cookieUtils';
import { getCart } from './cartService';
import type { OrderItem } from '$lib/types/orderTypes';

export async function createOrder(totalAmount: number) {
	console.log('createOrder');
	try {
		let orderItems = [] as OrderItem[];
		const cartItemsPromise = await getCart();
		const cartItems = await Promise.all(cartItemsPromise);

		cartItems.forEach((item) => {
			orderItems.push({
				productId: item.product.id,
				quantity: item.quantity
			});
		});

		console.log('orderItems', orderItems);

		const orderData = {
			totalAmount: totalAmount,
			items: orderItems
		};

		console.log('orderData', orderData);

		const response = await fetch('http://localhost:8080/orders/create', {
			method: 'post',
			headers: {
				'Content-Type': 'application/json',
				Authorization: `Bearer ${getCookie('authToken')}`
			},
			body: JSON.stringify(orderData)
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
