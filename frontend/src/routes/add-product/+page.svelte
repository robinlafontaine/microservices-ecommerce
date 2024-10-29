<script>
  import { useForm, validators, HintGroup, Hint, email, required } from "svelte-use-form";
  import { uploadImage, createProduct  } from '$lib/services/productService';

  const form = useForm();
  let productName = '';
  let description = '';
  /**
	 * @type {File}
	 */
  let image 
  let price = 0;
  let categoryId = 0;
  let stockQuantity = 0;

  /**
	 * @param {{ preventDefault: () => void; }} event
	 */
  async function handleSubmit(event) {
    event.preventDefault();

    // @ts-ignore
    const image_file = document.getElementById('image_file').files[0];
    console.log(image_file);
    let imageUrl = await uploadImage(image_file);
    let result = await createProduct(productName, description, imageUrl, price, categoryId, stockQuantity);
  }
</script>

<form use:form on:submit={handleSubmit}>
  <h1>Add Product</h1>
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
      <Hint on="required">Please fill this area</Hint>
    </HintGroup>
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
  
  <button disabled={!$form.valid}>Add Product</button>
</form>

<style>
	:global(.touched:invalid) {
		border-color: red;
		outline-color: red;
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

  input {
    margin-bottom: 0.5em;
  }
</style>