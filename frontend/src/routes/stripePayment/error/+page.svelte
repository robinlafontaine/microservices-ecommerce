<svelte:head>
	<title>Payment error</title>
	<meta name="description" content="Payment error" />
</svelte:head>

<script lang="ts">
	import { goto } from "$app/navigation";
</script>

<h1>There was an error during your payment.</h1>
<p>We are sorry for this inconvenience...</p>
<button on:click={() => goto('/shopping-cart')}>Go back to shopping cart
</button>

<style>
  button {
    height: 3rem;
    padding: .5rem;
    background-color: var(--color-theme-1);
    color: var(--color-theme-2);
    border: 3px solid var(--color-theme-1);
    border-radius: 5px;
    cursor: pointer;
    transition: background-color 0.1s, color 0.1s;

    &:hover {
      background-color: var(--color-theme-2);
      color: var(--color-theme-1);
      transition: background-color 0.2s, color 0.2s;
    }
  }
</style>