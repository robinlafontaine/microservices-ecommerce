import type { ProductResponseDTO } from './productTypes';

export interface CartItemDTO {
	product: ProductResponseDTO;
	quantity: number;
}
