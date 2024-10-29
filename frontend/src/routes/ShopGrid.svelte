<script lang="ts">
	import { onMount } from 'svelte';
	import { showProducts, checkAuth, deleteProduct } from '$lib/services/productService';
	import { getCookie } from '$lib/utils/getCookie';	
	import type { ProductResponseDTO } from '$lib/types/productTypes';

	let products: ProductResponseDTO[] = [];
	let _canManipulate = false;

	if (getCookie('authToken') === null) {
		if (typeof window !== 'undefined') {
			window.location.href = '/login';
		}
	} else {
		console.log('User is logged in');		
	}

	onMount(async () => {
			const response = await showProducts();
			if (response.success) {
				products = response.data;
			} else {
				console.error(response.error);
			}
			console.log(products);

			const canManipulate = await checkAuth();
			console.log(canManipulate);
			if (canManipulate.success) {
				console.log('User can manipulate products');
				_canManipulate = true;
			} else {
				console.error('User cannot manipulate products');
			}
		});
</script>

<div class="shop-grid">
	{#each products as product}
		<div class="shop-card">
			<img class="product-image" src={`${product.imageUrl}`} alt={product.productName} />
			<p class="name">{product.productName}</p>
			<p class="price">{product.price}€</p>
			<button class="add-to-cart">Add to cart 
				<span class="material-symbols-outlined">
				add_shopping_cart
				</span>
			</button>
			
			{#if _canManipulate}
				<button class="edit-product">Edit product 
					<span class="material-symbols-outlined">
					edit
					</span>
				</button>
				<button class="delete-product" on:click={() => {
					deleteProduct(product.id); 
					console.log("deleting")}}>Delete product
					<span class="material-symbols-outlined">
					delete
					</span>
				</button>
			{/if}
		</div>
	{/each}
</div>

<style>
	.shop-grid {
		display: grid;
		grid-template-columns: 1fr 1fr 1fr;
		grid-gap: 1rem;
	}

	.shop-card {
		display: flex;
		flex-direction: column;
		align-items: left;
		padding: 1rem; 	
		background-color: white;
		border-radius: 5px;
		box-shadow: rgba(99, 99, 99, 0.2) 0px 2px 8px 0px;
	}

	p {
		margin: 0;
		padding: 0;
	}

	.product-image {
		height: 200px;
		object-fit: contain;
		border-radius: 5px;
	}

	.name {
		font-size: 1.2rem;
	}

	.price {
		font-size: 1rem;
	}

	button {
		display: flex;
		align-items: center;
		justify-content: center;
		margin-top: 1rem;
		padding: 0.5rem;
		background-color: var(--color-theme-1);
		color: white;
		border: none;
		border-radius: 5px;
		cursor: pointer;
	}
</style>
