<svelte:head>
	<title>Thanks !</title>
	<meta name="description" content="Thanks !" />
</svelte:head>

<script lang="ts">
	import { goto } from "$app/navigation";
</script>

<h1>Thanks for your purchase!</h1>
<p>Credit card payment was successfully processed.</p>
<button on:click={() => goto('/')}>Go back to shop
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