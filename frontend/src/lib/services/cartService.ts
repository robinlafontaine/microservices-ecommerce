import { getCookie } from '$lib/utils/cookieUtils';
import { showProduct } from './productService';

export async function addToCart(productId: string, quantity: number): Promise<boolean> {
	if (typeof document !== 'undefined') {
		const cart = getCookie('cart');
		if (!cart) {
			document.cookie = `cart=${productId}:${quantity}; path=/`;
			return true;
		}

		const cartItems = cart.split(',');
		const isProductInCart = cartItems.some((item) => item.split(':')[0] === productId);

		if (!isProductInCart) {
			if (quantity < 1) {
				return true;
			}
			cartItems.push(`${productId}:${quantity}`);
			document.cookie = `cart=${cartItems.join(',')}; path=/`;
			return true;
		}

		const updatedCart = [];
		for (let i = 0; i < cartItems.length; i++) {
			let [id, qty] = cartItems[i].split(':');
			if (id === productId) {
				if (quantity === 0) {
					continue;
				}
				const response = await showProduct(productId);
				const product = response.data[0];

				qty = (parseInt(qty) + quantity).toString();
				qty = Math.min(parseInt(qty), product.stockQuantity).toString();
			}
			updatedCart.push(`${id}:${qty}`);
		}
		document.cookie = `cart=${updatedCart.join(',')}; path=/`;

		return true;
	} else {
		console.error('Document is not defined');
		return false;
	}
}

export async function getCart() {
	if (typeof document !== 'undefined') {
		const cart = getCookie('cart');
		if (!cart) {
			return [];
		}

		const cartItems = cart.split(',').map(async (item) => {
			const [productId, quantity] = item.split(':');
			const response = await showProduct(productId);
			const product = response.data[0];
			return { product, quantity: parseInt(quantity) };
		});
		return cartItems;
	} else {
		console.error('Document is not defined');
		return [];
	}
}
