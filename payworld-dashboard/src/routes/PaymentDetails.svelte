<script>
  let uuid;
  let payment = {};

  const DETAILS_URL = "http://localhost:8084/api/analytics/payments/";

  async function getPaymentDetails() {
    const response = await fetch(DETAILS_URL + uuid);
    const json = await response.json();

    payment = json;
  }
</script>

<div>
  <h1>Payment Details</h1>
  <p>
    Get the full details of any payment in the payworld ecosystem.
  </p>
  <div class="divider"></div>
  <div class="inputWrapper">
    <input bind:value={uuid} class="input" />
    <button on:click={getPaymentDetails}>Search</button>
  </div>
  <table>
    <tbody>
      {#each Object.entries(payment) as [key, value]}
      <tr>
        <td>{key}</td>
        <td>{value}</td>
      </tr>
      {/each}
    </tbody>
  </table>
</div>

<style>
  .divider {
    width: 100%;
    height: 2px;
    background-color: #90909090;
    margin-bottom: 20px;
  }

  .input {
    width: 500px;
  }

  button:hover {
    cursor: pointer;
    opacity: 0.7;
  }
</style>
