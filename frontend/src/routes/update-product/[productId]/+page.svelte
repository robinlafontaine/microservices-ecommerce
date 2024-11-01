<svelte:head>
	<title>Update Product</title>
	<meta name="description" content="Update Product" />
</svelte:head>

<script lang="ts">
  import { useForm, validators, HintGroup, Hint, required } from "svelte-use-form";
  import { uploadImage, updateProduct, showProduct  } from '$lib/services/productService';
  import type { ProductResponseDTO } from '$lib/types/productTypes';
	import { page } from '$app/stores';
  import { goto } from '$app/navigation';
	import { onMount } from "svelte";

  const form = useForm();
  let productId = $page.params.productId;
  let productName = '';
  let description = '';
  let image: any 
  let price = 0;
  let categoryId = 0;
  let stockQuantity = 0;
  let data: ProductResponseDTO;

  onMount (async () => {
    const response = await showProduct(productId);
    data = await response.data[0];
    console.log(data);
    productName = data.productName;
    description = data.description;
    price = data.price;
    categoryId = data.categoryId;
    stockQuantity = data.stockQuantity;
    image = data.imageUrl;
  });

  async function handleSubmit(event: { preventDefault: () => void; }) {
    event.preventDefault();
    const imageInput = document.getElementById('image_file') as HTMLInputElement;
    const image_file = imageInput?.files ? imageInput.files[0] : null;
    console.log(image_file);
    let imageUrl = '';
    if (image_file) {
      imageUrl = await uploadImage(image_file);
    }
    await updateProduct(productId, productName, description, imageUrl, price, categoryId, stockQuantity);
    alert('Product updated successfully');
    goto('/');
  }
</script>

<form use:form on:submit={handleSubmit}>
  <h1>Update Product</h1>
  <div class="form-element">
    <label for="productName">Product Name</label>
    <input type="text" placeholder="Product name" name="productName" bind:value={productName} use:validators={[required]} />
    <HintGroup for="productName">
      <Hint on="required">Please fill this area</Hint>
    </HintGroup>
  </div>

  <div class="form-element">
    <label for="description">Description</label>
    <input type="text" placeholder="Description" name="description" bind:value={description} use:validators={[required]} />
    <HintGroup for="description">
      <Hint on="required">Please fill this area</Hint>
    </HintGroup>
  </div>

  <div class="form-element">
    <label for="image">Image</label>
    <input id="image_file" type="file" placeholder="Image" name="image" bind:value={image} use:validators={[required]} />
    <HintGroup for="image">
      <Hint on="required">Please choose an image</Hint>
    </HintGroup>
    <img src="{image}" alt="{productName}" />
  </div>

  <div class="form-element">
    <label for="price">Price</label>
    <input type="number" min="0" placeholder="Price" name="price" bind:value={price} use:validators={[required]} />
    <HintGroup for="price">
      <Hint on="required">Please fill this area</Hint>
    </HintGroup>
  </div>
  
  <div class="form-element">
    <label for="stockQuantity">Stock Quantity</label>
    <input type="number" min="0" placeholder="Stock Quantity" name="stockQuantity" bind:value={stockQuantity} use:validators={[required]} />
    <HintGroup for="stockQuantity">
      <Hint on="required">Please fill this area</Hint>
    </HintGroup>
  </div>
  
  <button disabled={!$form.valid}>Update Product</button>
</form>

<style>
	:global(.touched:invalid) {
		border-color: red;
		outline-color: red;
	}

  img {
    width: 100px;
    height: 100px;
    object-fit: contain;
  }

  form {
    display: grid;
    gap: 1em;
  }

  .form-element {
    display: grid;
  }

  label {
    font-size: large;
    font-weight: 500;
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