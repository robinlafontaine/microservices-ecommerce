<svelte:head>
  <title>Payment</title>
  <meta name="description" content="Payment" />
</svelte:head>

<script lang="ts">
  import { goto } from '$app/navigation';
  import { onMount } from 'svelte';
  import { loadStripe, type Stripe, type StripeError } from '@stripe/stripe-js';
  import { Elements, LinkAuthenticationElement, PaymentElement, Address } from 'svelte-stripe';
  import { getCookie } from '$lib/utils/cookieUtils';

  import { PUBLIC_STRIPE_KEY } from '$env/static/public';

  let stripe: Stripe | null = null;
  let error: StripeError | null = null;
  let processing = false;
  let elements: any;
  let clientSecret: string | null = null;

  onMount(async () => {
    clientSecret = getCookie('clientSecret');

    if (!clientSecret) {
      goto('/'); // Redirect to home if no clientSecret is available
      return;
    }

    try {
      stripe = await loadStripe(PUBLIC_STRIPE_KEY);
    } catch (e) {
      error = { message: 'Failed to load Stripe. Please refresh the page or try again later.', type: 'api_error' };
    }
  });

  async function submit() {
    if (processing || !stripe || !elements) return; // Ensure no duplicate processing

    processing = true;

    const result = await stripe.confirmPayment({
      elements,
      redirect: 'if_required'
    });

    if (result.error) {
      error = result.error; // Display error message on failure
      processing = false;
      goto('/stripePayment/error');
    } else {
      goto('/stripePayment/thanks'); // Redirect to thank-you page on success
    }
  }
</script>

<h1>Payment</h1>

{#if error}
  <p class="error">{error.message} Please try again.</p>
{/if}

{#if stripe && clientSecret}
  <p>This is a test payment page. Please try to pay with the following test card details:</p>
  <ul>
    <li>Card number: 4242 4242 4242 4242</li>
    <li>Expiry date: Any future date</li>
    <li>CVC: Any 3 digits</li>
    <li>Random informations for the rest of the fields</li>
  </ul>
  <Elements
    {stripe}
    {clientSecret}
    theme="flat"
    labels="floating"
    variables={{ colorPrimary: '#7c4dff' }}
    rules={{ '.Input': { border: 'solid 1px #0002' } }}
    bind:elements
  >
    <form on:submit|preventDefault={submit}>
      <LinkAuthenticationElement />
      <PaymentElement />
      <Address mode="billing" />

      <button disabled={processing}>
        {#if processing}
          Processing...
        {:else}
          Pay
        {/if}
      </button>
    </form>
  </Elements>
{:else}
  <p>Loading payment information...</p>
{/if}

<style>
  .error {
    color: tomato;
    margin: 2rem 0 0;
  }

  form {
    display: flex;
    flex-direction: column;
    gap: 10px;
    margin: 2rem 0;
  }

  button {
    background-color: var(--color-theme-1);
    color: var(--color-theme-2);
    border: 3px solid var(--color-theme-1);
    border-radius: 5px;
    cursor: pointer;
    font-weight: 600;
    transition: background-color 0.1s, color 0.1s;
    padding: .5rem;

    &:hover {
      background-color: var(--color-theme-2);
      color: var(--color-theme-1);
      transition: background-color 0.2s, color 0.2s;
    }

    &:disabled {
      background-color: #ccc;
      color: #666;
      border: 3px solid #ccc;
      cursor: not-allowed;
    }
  }
</style>
