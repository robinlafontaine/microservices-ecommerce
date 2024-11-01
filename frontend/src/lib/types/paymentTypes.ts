export interface PaymentRequestDTO {
	orderId: number;
	amount: number;
	paymentMethodId: string;
}

export interface PaymentResponseDTO {
	paymentId: string;
	clientSecret: string;
	status: string;
}
