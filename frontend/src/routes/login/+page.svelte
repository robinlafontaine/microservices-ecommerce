<svelte:head>
	<title>Login</title>
	<meta name="description" content="Login" />
</svelte:head>

<script lang="ts">
  import { useForm, validators, HintGroup, Hint, email, required } from "svelte-use-form";
  import { handleLogin } from '$lib/services/authService';
  import { deleteCookie } from '$lib/utils/cookieUtils';
	import { goto } from "$app/navigation";
	import { onMount } from "svelte";

  onMount(() => {
    if (document.cookie.includes('authToken')) {
      goto('/profile');
    }
  });

  const form = useForm();
  let _email = '';
  let password = '';

  async function handleSubmit(event: Event) {
    event.preventDefault();

    const result = await handleLogin(_email, password);

    if (result.success) {
      console.log('Login successful:', result.data);
      const token = result.data.jwtToken;
      document.cookie = `authToken=${token}; path=/; max-age=86400; SameSite=Strict`;
      deleteCookie('cart');
      goto('/');
    } else {
      console.error('Login error:', result.error);
    }
  }
</script>

<div class="container">
  <form use:form on:submit={handleSubmit}>
    <h1>Login</h1>
  
    <label for="email">Email</label>
    <input type="email" name="email" bind:value={_email} use:validators={[required, email]} />
    <HintGroup for="email">
      <Hint on="required">Please fill this area</Hint>
      <Hint on="email" hideWhenRequired>Email not valid</Hint>
    </HintGroup>
  
    <label for="password">Password</label>
    <input type="password" name="password" bind:value={password} use:validators={[required]} />
    <Hint for="password" on="required">Please fill this area</Hint>
  
    <button disabled={!$form.valid}>Login</button>
  </form>
  <a href="/register">Don't have an account? Register here</a>
</div>

<style>
	:global(.touched:invalid) {
		border-color: red;
		outline-color: red;
	}

  .container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 100%;
  }

  form {
    width: 50%;
    display: grid;
    gap: 1em;
  }

  label {
    font-size: 1.5rem;
    font-weight: 600;
  }

  input,
  button {
    padding: 0.5em;
    border-radius: 0.5em;
    border: 1px solid #ccc;
  }

  button {
    background-color: var(--color-theme-1);
    color: var(--color-theme-2);
    border: 3px solid var(--color-theme-1);
    border-radius: 5px;
    cursor: pointer;
    font-weight: 600;
    transition: background-color 0.1s, color 0.1s;

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