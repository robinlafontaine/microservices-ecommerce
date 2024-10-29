export interface ProductRequestDTO {
	productName: string;
	description: string;
	imageUrl: string;
	price: number;
	categoryId: number;
	stockQuantity: number;
}

export interface ProductResponseDTO {
	id: number;
	productName: string;
	description: string;
	imageUrl: string;
	price: number;
	categoryId: number;
	stockQuantity: number;
}
