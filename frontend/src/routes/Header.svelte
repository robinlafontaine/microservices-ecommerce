<script>
	import { page } from '$app/stores';
	import logo from '$lib/images/logo.svg';
</script>

<header>
	<div class="corner left-corner">
		<a href="/">
			<img src={logo} alt="SvelteKit" />
		</a>
	</div>

	<div class="title">
		<a href="/">
			<span>Luho</span>
		</a>
	</div>

	<div class="corner right-corner">
		<a href="/about">
			<span class="material-symbols-outlined">
				shopping_cart
				</span>
		</a>
		<a href="/login">
			<span class="material-symbols-outlined">
				account_circle
				</span>
		</a>
	</div>
</header>

<style>
	header {
		display: grid;
		justify-content: space-between;
		grid-template-columns: 1fr 1fr 1fr;
		background-color: var(--color-bg-2);
		position: sticky;
		top: 0;
		z-index: 1000;
	}

	.corner {
		display: flex;
		align-items: center;
		padding: 1em;
		flex-direction: row;
		gap: 1em;
	}

	.right-corner {
		justify-content: flex-end;
	}

	.left-corner {
		justify-content: flex-start;
	}

	.corner img {
		width: 2em;
		height: 2em;
		object-fit: contain;
	}

	header a {
		color: var(--color-theme-3);
	}

	a:hover {
		color: var(--color-theme-1);
	}

	.title {
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 2rem;
		text-align: center;
		font-weight: 700;
		padding: 0;
		margin: 0;
		font-family: var(--font-title);
	}
</style>
