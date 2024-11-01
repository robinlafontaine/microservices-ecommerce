<script>
	import ShopGrid from './ShopGrid.svelte';
	
	// if (!document.cookie.includes('authToken')) {
	// 	window.location.href = '/login';
	// }
</script>

<svelte:head>
	<title>Shop</title>
	<meta name="description" content="Svelte demo app" />
</svelte:head>

<section>
	<h1>
		Shop
	</h1>

	<ShopGrid />
</section>

<style>
	section {
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		flex: 0.6;
	}
</style>
