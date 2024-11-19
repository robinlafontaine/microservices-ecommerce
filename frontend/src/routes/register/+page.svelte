<svelte:head>
	<title>Register</title>
	<meta name="description" content="Register" />
</svelte:head>

<script lang="ts">
  import { useForm, validators, HintGroup, Hint, email, required } from "svelte-use-form";
  import { handleRegister } from '$lib/services/authService';
  import { deleteCookie } from '$lib/utils/cookieUtils';
	import { goto } from "$app/navigation";
	import { onMount } from "svelte";
	import type { ValidationErrors } from "svelte-use-form/models/validator";

  onMount(() => {
    if (document.cookie.includes('authToken')) {
      goto('/profile');
    }
  });

  const form = useForm();
  let firstName = '';
  let lastName = '';
  let _email = '';
  let password = '';
  let confirmPassword = '';

  async function handleSubmit(event: Event) {
    event.preventDefault();

    const result = await handleRegister(firstName, lastName, _email, password);

    if (result.success) {
      alert('Registration successful! Please log in to continue');
      deleteCookie('cart');
      goto('/login');
    } else {
      alert('Registration error');
    }
  }

  function passwordValidator(): null | ValidationErrors {
    return isValidPassword() ? null : { password: "Password must be at least 8 characters long, contain at least one uppercase letter, and at least one number" };
  }

  function confirmPasswordValidator(): null | ValidationErrors {
    return password === confirmPassword ? null : { confirmPassword: "Passwords do not match" };
  }

  function isValidPassword() {
    if (password.length < 8) {
      console.log('Password must be at least 8 characters long');
      return false
    }
    if (!/[A-Z]/.test(password)) {
      console.log('Password must contain at least one uppercase letter');
      return false
    }
    if (!/\d/.test(password)) {
      console.log('Password must contain at least one number');
      return false
    }
    console.log('Password is valid');
    return true
  }
</script>

<div class="container">
  <form use:form on:submit={handleSubmit}>
    <h1>Register</h1>

    <label for="firstName">First Name</label>
    <input type="text" name="firstName" bind:value={firstName} use:validators={[required]} />
    <HintGroup for="firstName">
      <Hint on="required">Please fill this area</Hint>
    </HintGroup>

    <label for="lastName">Last Name</label>
    <input type="text" name="lastName" bind:value={lastName} use:validators={[required]} />
    <HintGroup for="lastName">
      <Hint on="required">Please fill this area</Hint>
    </HintGroup>
  
    <label for="email">Email</label>
    <input type="email" name="email" bind:value={_email} use:validators={[required, email]} />
    <HintGroup for="email">
      <Hint on="required">Please fill this area</Hint>
      <Hint on="email" hideWhenRequired>Email not valid</Hint>
    </HintGroup>
  
    <label for="password">Password</label>
    <input type="password" name="password" bind:value={password} use:validators={[required, passwordValidator]} />
    <HintGroup for="password">
      <Hint for="password" on="required">Please fill this area</Hint>
      <Hint for="password" on="password">Password must be at least 8 characters long, contain at least one uppercase letter, and at least one number</Hint>
    </HintGroup>

    
    <label for="confirmPassword">Confirm Password</label>
    <input type="password" name="confirmPassword" bind:value={confirmPassword} use:validators={[required, confirmPasswordValidator]} />
    <HintGroup for="confirmPassword">
      <Hint for="confirmPassword" on="required">Please fill this area</Hint>
      <Hint for="confirmPassword" on="confirmPassword">Passwords do not match</Hint>
    </HintGroup>
    <button disabled={!$form.valid}>Register</button>
  </form>
  <a href="/login">Already have an account? Log in here</a>
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