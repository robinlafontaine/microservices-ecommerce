<svelte:head>
	<title>Payment</title>
	<meta name="description" content="Payment" />
</svelte:head>

<script lang="ts">
  import { goto } from '$app/navigation'
  import { onMount } from 'svelte'
  import { loadStripe, type Stripe, type StripeError } from '@stripe/stripe-js'
  import { Elements, LinkAuthenticationElement, PaymentElement, Address } from 'svelte-stripe'
  import { getCookie } from '$lib/utils/cookieUtils'

  // API key is in .env file
  import { PUBLIC_STRIPE_KEY } from '$env/static/public'


  let stripe: Stripe | null = null
  let error: StripeError | null = null
  let cardElement: any
  let name: any
  let processing = false
  let elements: any
  let clientSecret: string | null = null

  onMount(async () => {
    if (getCookie('clientSecret') == null) {
      goto('/')
    }
    clientSecret = getCookie('clientSecret');
    stripe = await loadStripe(PUBLIC_STRIPE_KEY)
  })

  async function submit() {
    // avoid processing duplicates
    if (processing) return

    processing = true

    // confirm payment with stripe
    if (!stripe) {
      error = { message: 'Stripe is not loaded', type: 'validation_error' };
      processing = false;
      return;
    }

    const result = await stripe.confirmPayment({
      elements,
      redirect: 'if_required'
    })

    // log results, for debugging
    console.log({ result })

    if (result.error) {
      // payment failed, notify user
      error = result.error
      processing = false
      goto('/stripePayment/error')
    } else {
      // payment succeeded, redirect to "thank you" page
      goto('/stripePayment/thanks');
    }
  }
</script>

<h1>Payment</h1>

{#if error}
  <p class="error">{error.message} Please try again.</p>
{/if}

{#if clientSecret}
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
  Loading...
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
    padding: 1rem;
    border-radius: 5px;
    border: solid 1px #ccc;
    color: white;
    background: var(--link-color);
    font-size: 1.2rem;
    margin: 1rem 0;
  }
</style>