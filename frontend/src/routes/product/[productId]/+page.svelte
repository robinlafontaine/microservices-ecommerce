<!-- src/routes/product/[id].svelte -->
<script lang="ts">
	import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import { showProducts, showProduct, checkAuth, deleteProduct } from '$lib/services/productService';
  import type { ProductResponseDTO } from '$lib/types/productTypes';
  import { onMount } from 'svelte';

  const { productId } = $page.params;
  let product: ProductResponseDTO | null = null;
  let products: ProductResponseDTO[] = [];
  let _canManipulate = false;

  onMount(async () => {
			const response = await showProduct(productId);
			if (response.success) {
				product = response.data[0];
			} else {
				console.error(response.error);
			}
			console.log(product);

      const response2 = await showProducts();
      if (response2.success) {
        console.log(response2.data);
        // select 8 random products
        products = response2.data.sort(() => Math.random() - Math.random()).slice(0, 8);
      } else {
        console.error(response2.error);
      }

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

<a class="back" href="/">
  <span class="material-symbols-outlined">
    arrow_back
  </span>
</a>

{#if product}
  <div class="product-page">
      <div class="product-card">
        <div class="image-div">
          <img class="product-image" src={`${product.imageUrl}`} alt={product.productName} />
        </div>
        <div class="product-info">
          <div class="name-price-cart">
            <div class="product-text">
              <p class="name-price">{product.productName} - {product.price}€</p>
              <p class="description">{product.description}</p>
            </div>
            <div class="cart">
              <button class="add-to-cart">
                <span class="material-symbols-outlined add-to-cart-icon">
                add_shopping_cart
                </span>
              </button>
            </div>
          </div>
          {#if _canManipulate}
            <div class="buttons">
              <button class="edit-product"> 
                <span class="material-symbols-outlined product-icons">
                edit
                </span>
              </button>
              <button class="delete-product" on:click={() => {
                if (product) {
                  deleteProduct(product.id);
                  console.log("deleting");
                } else {
                  console.error("Product is null");
                }
              }}>
                <span class="material-symbols-outlined product-icons">
                delete
                </span>
              </button>
            </div>
          {/if}
        </div>  
          
        
      </div>

      <div class="recommandations">
        <ul>
          {#each products as recommandation}
            <li>
              <a data-sveltekit-reload href="/product/{recommandation.id}">
                <div class="image-div-reco"><img class="reco-image" src={`${recommandation.imageUrl}`} alt={recommandation.productName} /></div>
              </a>
            </li>
          {/each}
        </ul>
      </div>
  </div>
  
{:else}
  <p>Product not found</p>
{/if}

<style>
  .product-page {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
  }

  .product-card {
    display: grid;
    grid-template-columns: 1fr 1fr;
    align-items: center;
  }

  .image-div {
    display: flex;
    justify-content: center;
    padding: 0;
    margin: 0;
    background-color: white;
  }

  .product-image {
    width: 500px;
    height: 600px;
    object-fit: contain;
  }

  .product-info {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    background-color: #f5f5f5;
    height: calc(100% - 2rem);
    padding: 1rem;
  }

  .name-price-cart {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    align-items: left;
    justify-content: top;
  }

  .add-to-cart {
    height: 100%;
  }

  .product-icons,
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

    &:hover {
      background-color: var(--color-theme-2);
      color: var(--color-theme-1);
      transition: background-color 0.2s, color 0.2s;
    }
  }

  .add-to-cart-icon {
    font-size: 2rem;
    background-color: var(--color-theme-1);
    border: 3px solid var(--color-theme-1);
    color: var(--color-theme-2);
    padding: 1rem;
    transition: background-color 0.1s, color 0.1s;
    font-weight: 200;

    &:hover {
      background-color: var(--color-theme-2);
      color: var(--color-theme-1);
      transition: background-color 0.2s, color 0.2s;
    }
  }

  .product-text {
    display: flex;
    flex-direction: column;
    align-items: left;
    justify-content: top;
    background-color: #f5f5f5;
    height: calc(100% - 2rem);
    padding: 1rem;
  }

  .name-price {
    font-size: 2rem;
    font-weight: bold;
  }

  .description {
    font-size: 1rem;
  }

  p {
    margin: 0;
    padding: 0;
  }

  a.back {
    position: absolute;
    top: 5rem;
    left: 5rem;
    cursor: pointer;
  }

  ul {
    list-style-type: none;
    padding: 0;
    display: grid;
    gap: 1rem;
    grid-template-columns: repeat(8, 1fr);
  }

  .image-div-reco {
    width: 100px;
    height: 100px;
    background-color: white;
    transition: transform 0.1s;

		&:hover {
			transform: scale(1.2);
			transition: transform 0.2s;
		}
  }

  .reco-image {
    width: 100px;
    height: 100px;
    object-fit: contain;
  }

  .buttons {
    display: flex;
    gap: 1rem;
    padding: 1rem;
    background-color: white;
    border-radius: 5px;
  }

  button {
    background-color: transparent;
    border: none;
    cursor: pointer;
    color: var(--color-theme-1);
  }
</style>