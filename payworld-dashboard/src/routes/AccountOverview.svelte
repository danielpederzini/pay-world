<script>
  import { onMount } from "svelte";

  let tableFields = [];
  let accountOverviews = [];

  const OVERVIEWS_URL = "http://localhost:8084/api/analytics/accounts";

  onMount(() => {
    getAccountOverviews();
  });

  async function getAccountOverviews() {
    const response = await fetch(OVERVIEWS_URL);
    const json = await response.json();
    const fields = Object.keys(json[0]);

    if (fields) {
      accountOverviews = json;
      tableFields = fields;
    }
  }
</script>

<div>
  <h1>Account Overview</h1>
  <p>
    An overview of accounts and their completed payments in the payworld system.
  </p>
  <div class="divider"></div>
  <table>
    <thead>
      <tr>
        {#each tableFields as field}
          <td>{field}</td>
        {/each}
      </tr>
    </thead>
    <tbody>
      {#each Object.values(accountOverviews) as overview}
        <tr>
          {#each Object.values(overview) as value}
            <td>{value}</td>
          {/each}
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
</style>
