<script>
  import { onMount } from "svelte";
  import SockJS from "sockjs-client/dist/sockjs.js";
  import { Stomp } from "@stomp/stompjs";

  const paymentStatusCounts = {
    CREATED: 0,
    ENRICHED: 0,
    COMPLETED: 0,
    FAILED_AT_ENRICHMENT: 0,
    FAILED_AT_PROCESSING: 0,
    FAILED_AT_PUBLISHING: 0,
  };

  let totalPayments = 0;
  const paymentMap = new Map();

  const statusColors = {
    CREATED: "#4facfe",
    ENRICHED: "#00f2fe",
    COMPLETED: "#00c853",
    FAILED_AT_ENRICHMENT: "#ff9100",
    FAILED_AT_PROCESSING: "#ff5252",
    FAILED_AT_PUBLISHING: "#d500f9",
  };

  const POPULATE_URL = "http://localhost:8084/api/analytics/payments/count";
  const SOCKET_URL = "http://localhost:8084/ws";
  const TOPIC = "/topic/payments";

  let circleColor = "red";
  let status = "Connecting…";
  let stompClient = null;

  onMount(() => {
    populate();
    connect();
  });

  async function populate() {
    const response = await fetch(POPULATE_URL);
    const json = await response.json();

    let total = 0

    json.forEach((element) => {
      total += element.count
      paymentStatusCounts[element.status] = element.count
    });

    totalPayments = total
  }

  function connect() {
    const socket = new SockJS(SOCKET_URL);
    stompClient = Stomp.over(socket);

    stompClient.connect(
      {},
      (frame) => {
        console.log("Connected: " + frame);
        circleColor = "green";
        status = "Connected";

        stompClient.subscribe(TOPIC, (message) => {
          const payment = JSON.parse(message.body);
          handlePaymentEvent(payment);
        });
      },
      (error) => {
        console.error("Connection error:", error);
        circleColor = "red";
        status = "Connection error";

        setTimeout(connect, 5000);
      },
    );
  }

  function handlePaymentEvent(payment) {
    if (payment.status == "CREATED") {
      totalPayments++;
    }

    const prevStatus = paymentMap.get(payment.uuid);

    if (prevStatus) {
      paymentStatusCounts[prevStatus]--;
    }

    paymentStatusCounts[payment.status]++;
    paymentMap.set(payment.uuid, payment.status);
  }
</script>

<div>
  <h1>Real Time Payments View</h1>
  <div class="connectionStatusContainer">
    <div class="circle" style="background-color: {circleColor};"></div>
    <span>{status}</span>
  </div>
  <p>
    A visual representation of payworld's payment processing, updated in real
    time.
  </p>
  <div class="divider"></div>
  <div class="statsChart">
    <div class="statsNameList">
      {#each Object.keys(paymentStatusCounts) as key}
        <span class="statsName">{key.replace(/_/g, " ")}</span>
      {/each}
      <span class="statsName">TOTAL</span>
    </div>
    <div class="statsBarList">
      {#each Object.entries(paymentStatusCounts) as [key, value]}
        <div class="statsBarWrapper">
          <div
            class="statsBar"
            style="background-color: {statusColors[key]}; width: {(value /
              totalPayments) *
              100}%"
          ></div>
          <span class="statsCount">{value}</span>
        </div>
      {/each}
      <span class="statsCount">{totalPayments}</span>
    </div>
  </div>
</div>

<style>
  .connectionStatusContainer {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: start;
  }

  .divider {
    width: 100%;
    height: 2px;
    background-color: #00000090;
    margin-bottom: 20px;
  }

  .statsChart {
    display: flex;
    width: 600px;
  }

  .statsNameList {
    display: flex;
    flex-direction: column;
    align-items: end;
    justify-content: start;
    gap: 10px;
    margin-right: 10px;
  }

  .statsBarList {
    display: flex;
    flex-direction: column;
    align-items: start;
    justify-content: start;
    width: 60%;
    gap: 10px;
  }

  .statsName {
    height: 20px;
    font-weight: 500;
  }

  .statsBarWrapper {
    display: flex;
    width: 100%;
  }

  .statsBar {
    height: 20px;
    min-width: 10px;
    background-color: #000;
    text-align: end;
    margin-right: 5px;
  }

  .statsCount {
    color: #000;
    height: 20px;
    font-weight: 500;
  }

  .circle {
    width: 10px;
    height: 10px;
    border-radius: 100%;
    margin-right: 5px;
  }
</style>
