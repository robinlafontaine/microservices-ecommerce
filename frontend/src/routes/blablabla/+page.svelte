<script lang="ts">
  import { initiatePayment, getPaymentStatus, confirmPayment } from '$lib/services/paymentService';
  import type { PaymentRequestDTO, PaymentResponseDTO } from '$lib/types/paymentTypes';

  let paymentRequest: PaymentRequestDTO = {
    orderId: 123,
    amount: 1000,
    paymentMethodId: 'pm_card_visa'
  };

  let paymentResponse: PaymentResponseDTO | null = null;
  let paymentStatus: string | null = null;
  let paymentId: string = '';

  async function handleInitiatePayment() {
    paymentResponse = await initiatePayment(paymentRequest);
    if (paymentResponse) {
      paymentId = paymentResponse.paymentId;
      console.log('Paiement initié:', paymentResponse);
    }
  }

  async function handleCheckStatus() {
    if (paymentId) {
      paymentStatus = await getPaymentStatus(paymentId);
      console.log('Statut du paiement:', paymentStatus);
    }
  }

  async function handleConfirmPayment() {
    if (paymentId) {
      const confirmationStatus = await confirmPayment(paymentId);
      console.log('Confirmation de paiement:', confirmationStatus);
    }
  }
</script>

<div>
  <h2>Initiate Payment</h2>
  <button on:click={handleInitiatePayment}>Initier le paiement</button>

  {#if paymentResponse}
    <p>Paiement ID: {paymentResponse.paymentId}</p>
    <p>Statut initial: {paymentResponse.status}</p>
    <button on:click={handleCheckStatus}>Vérifier le statut</button>
    <button on:click={handleConfirmPayment}>Confirmer le paiement</button>
  {/if}

  {#if paymentStatus}
    <p>Statut actuel du paiement: {paymentStatus}</p>
  {/if}
</div>
