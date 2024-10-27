<script>
	import {products} from '$lib/datas/products.json';
	import { showProducts } from '$lib/productService';
	const basePath = new URL('$lib/images/products/', import.meta.url);

	if (!document.cookie.includes('authToken')) {
		window.location.href = '/login';
	}
	else {
		console.log('User is logged in');
		showProducts();
	}
</script>

<div class="shop-grid">

	{#each products as product}
		<div class="shop-card">
			<img class="product-image" src={`${basePath}/${product.img}`} alt={product.name} />
			<p class="name">{product.name}</p>
			<p class="price">{product.price}€</p>
			<button class="add-to-cart">Add to cart 
				<span class="material-symbols-outlined">
				add_shopping_cart
				</span>
			</button>
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
