<script lang="ts">
	import { onMount } from 'svelte';
	import { getCart, addToCart } from '$lib/services/cartService';
  import { createOrder } from '$lib/services/orderService';
  import type { CartItemDTO } from '$lib/types/cartTypes';
	import { goto } from '$app/navigation';
	import { setCookie } from '$lib/utils/cookieUtils';

  let cart = [] as CartItemDTO[];
  let total = 0;

	onMount(async () => {
    const cartPromises = await getCart();
    cart = await Promise.all(cartPromises);
    total = cart.reduce((acc, item) => acc + item.product.price * item.quantity, 0);
			});

  async function handleAdd(productId: string) {
    await addToCart(productId, 1);
    window.location.reload();
  }

  async function handleRemove(productId: string) {
    await addToCart(productId, -1);
    window.location.reload();
  }

  async function handleDelete(productId: string) {
    await addToCart(productId, 0);
    window.location.reload();
  }

  async function checkout() {
    const response = await createOrder(total);
    if (!response.success) {
      console.error(response.error);
      return;
    } 
    setCookie('clientSecret', response.data.clientSecret, 600);
    goto('/stripePayment');
  }
</script>

<body>
  <a class="back" href="/">
    <span class="material-symbols-outlined">
      arrow_back
    </span>
  </a>

  <h1>Shopping Cart</h1>
  {#if cart.length === 0}
    <p>Your cart is empty</p>
  {:else}
  <div class="total-container">
    <h2>Total : {total}€</h2>
    <button class="checkout" on:click={() => checkout()}>Checkout</button>
  </div>
  <div class="cart-list">
    {#each cart as item}
    <div class="cart-item">
      <div class="img-div">
        <img class="img-item" src={item.product.imageUrl} alt={item.product.productName} />
      </div>
      <div class="product-text">
        <div class="item-info">
          <p class="name">{item.product.productName}</p>
          <p class="price">{item.product.price}€</p>
          <p class="description">{item.product.description}</p>
        </div>
        <div class="qty-div">
          <p class="qty">Quantity : {item.quantity}</p>
          <div class="buttons">
            <button on:click={() => handleRemove(item.product.id.toString())}>
              <span class="icons material-symbols-outlined">
                remove
              </span>
            </button>
            <button on:click={() => handleAdd(item.product.id.toString())}>
              <span class="icons material-symbols-outlined">
                add
              </span>
            </button>
            <button on:click={() => handleDelete(item.product.id.toString())}>
              <span class="icons material-symbols-outlined">
                delete
              </span>
            </button>
          </div>
          
        </div>
      </div>
      
      
    </div>
    {/each}
  </div>
  <div class="total-container">
    <h2>Total : {total}€</h2>
    <button class="checkout" on:click={() => goto('/stripePayment')}>Checkout</button>
  </div>
    
  {/if}
</body>

<style>
  .icons {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: calc(1rem - 6px);
    background-color: var(--color-theme-1);
    color: var(--color-theme-2);
    border: 3px solid var(--color-theme-1);
    border-radius: 100px;
    transition: background-color 0.1s, color 0.1s;

    &:hover {
      background-color: var(--color-theme-2);
      color: var(--color-theme-1);
      transition: background-color 0.2s, color 0.2s;
    }
  }

  a.back {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: calc(1rem - 6px);
    background-color: var(--color-theme-1);
    color: var(--color-theme-2);
    border: 3px solid var(--color-theme-1);
    border-radius: 100px;
    transition: background-color 0.1s, color 0.1s;
    position: absolute;
    top: 5rem;
    left: 5rem;
    cursor: pointer;

    &:hover {
      background-color: var(--color-theme-2);
      color: var(--color-theme-1);
      transition: background-color 0.2s, color 0.2s;
    }
  }

  .buttons {
    display: flex;
    flex-direction: row;
    gap: 1rem;
  }

  button {
    outline: none;
    border: none;
    background-color: transparent;
    padding: 0;
    margin: 0;
    cursor: pointer;
    border-radius: 100px;
  }

  .cart-item {
    width: 100%;
    display: flex;
    direction: row;
    align-items: top;
    background-color: #f5f5f5;
    box-shadow: rgba(99, 99, 99, 0.2) 0px 2px 8px 0px;
  }

  .img-div {
    height: 200px;
    width: 200px;
    background-color: var(--color-theme-2);
  }

  .img-div img {
    width: 100%;
    height: 100%;
    object-fit: contain;
  }

  p {
    margin: 0;
    padding: 0;
  }

  .product-text {
    display: flex;
    width: 100%;
    flex-direction: row;
    justify-content: space-between;
    padding: 1rem;
  }

  .name,
  .qty {
    font-size: 1.5rem;
    font-weight: 600;
  }

  .price {
    font-size: 1.2rem;
    font-weight: 600;
  }

  .cart-list {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }

  .checkout {
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
  
  .total-container {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    width: 100%;
  }

</style>