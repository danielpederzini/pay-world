<script>
  import Router from 'svelte-spa-router';
  import Home   from './routes/Home.svelte';
  import RealTimePayments  from './routes/RealTimePayments.svelte';
  import AccountView  from './routes/AccountView.svelte';
  import PaymentDetails  from './routes/PaymentDetails.svelte';

  const routes = {
    '/':      Home,
    '/real-time-payments': RealTimePayments,
    '/account-view': AccountView,
    '/payment-details': PaymentDetails,
    '*':      Home,  // fallback
  };
</script>

<nav>
  <a href="#/">Home</a>
  <a href="#/real-time-payments">Real Time Payments</a>
  <a href="#/account-view">Account View</a>
  <a href="#/payment-details">Payment Details</a>
</nav>

<main>
  <Router {routes} />
</main>

<style>
  nav {
    display: flex;
    gap: 1rem;
    padding: 1rem;
    background: #f5f5f5;
  }
  a {
    text-decoration: none;
    color: #333;
  }
  a:hover {
    text-decoration: underline;
  }
  main {
    padding: 1rem;
  }
</style>