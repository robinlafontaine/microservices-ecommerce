<svelte:head>
	<title>Profile</title>
	<meta name="description" content="Product" />
</svelte:head>

<script lang="ts">
	import { goto } from "$app/navigation";
  import { deleteAllCookies, getCookie } from "$lib/utils/cookieUtils";
  import { onMount } from "svelte";

  onMount(() => {
    if (!getCookie('authToken')) {
      goto('/login');
    }
  });

  async function logout() {
    deleteAllCookies();
    goto('/login');
  }
</script>

<h1>Profile</h1>
<button on:click={() => logout()}>Log out
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