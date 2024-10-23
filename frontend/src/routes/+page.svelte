<script>
	import ShopGrid from './ShopGrid.svelte';
</script>

<svelte:head>
	<title>Shop</title>
	<meta name="description" content="Svelte demo app" />
</svelte:head>

<section>
	<h1>
		Boutique
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
