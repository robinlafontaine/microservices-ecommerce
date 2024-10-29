<svelte:head>
	<title>Login</title>
	<meta name="description" content="Login" />
</svelte:head>

<script lang="ts">
  import { useForm, validators, HintGroup, Hint, email, required } from "svelte-use-form";
  import { handleLogin } from '$lib/services/authService';

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
    } else {
      console.error('Login error:', result.error);
    }
  }
</script>

<form use:form on:submit={handleSubmit}>
  <h1>Login</h1>

  <input type="email" name="email" bind:value={_email} use:validators={[required, email]} />
  <HintGroup for="email">
    <Hint on="required">Please fill this area</Hint>
    <Hint on="email" hideWhenRequired>Email not valid</Hint>
  </HintGroup>

  <input type="password" name="password" bind:value={password} use:validators={[required]} />
  <Hint for="password" on="required">Please fill this area</Hint>

  <button disabled={!$form.valid}>Login</button>
</form>

<style>
	:global(.touched:invalid) {
		border-color: red;
		outline-color: red;
	}

  form {
    display: grid;
    gap: 1em;
  }
</style>