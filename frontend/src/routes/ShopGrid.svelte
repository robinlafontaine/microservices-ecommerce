<script lang="ts">
	import { onMount } from 'svelte';
	import { showProducts, checkAuth } from '$lib/services/productService';
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
	<a href="/product/{product.id}">
		<div class="shop-card">
				<img class="product-image" src={`${product.imageUrl}`} alt={product.productName} />
				<div class="product-info">
					<p class="name">{product.productName}</p>
					<p class="price">{product.price}€</p>
				</div>
		</div>
	</a>
	{/each}
</div>

<style>
	.shop-grid {
		width: 100%;
		display: grid;
		grid-template-columns: 1fr 1fr 1fr;
		grid-gap: 1rem;
	}

	.shop-card {
		display: flex;
		flex-direction: column;
		align-items: left;
		background-color: white;
		border-radius: 5px;
		box-shadow: rgba(99, 99, 99, 0.2) 0px 2px 8px 0px;
		cursor: pointer;
		transition: transform 0.1s;

		&:hover {
			transform: scale(1.05);
			transition: transform 0.2s;
		}
	}

	p {
		margin: 0;
		padding: 0;
	}

	.product-image {
		height: 200px;
		object-fit: contain;
		border-radius: 5px;
		margin-top: 1rem;
		margin-bottom: 1rem;
	}

	.product-info {
		padding: 1rem;
		background-color: #f5f5f5;
	}

	.name {
		font-size: 1.2rem;
		font-weight: 600;
		text-align: center;
	}

	.price {
		font-size: 1.5rem;
		font-weight: 600;
		text-align: center;
	}

	a {
		text-decoration: none;
		color: black;
	}
</style>
