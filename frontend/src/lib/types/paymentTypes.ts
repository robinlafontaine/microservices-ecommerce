export interface PaymentRequestDTO {
	orderId: number;
	amount: number;
	paymentMethodId: string;
}

export interface PaymentResponseDTO {
	paymentId: string;
	status: string;
}
