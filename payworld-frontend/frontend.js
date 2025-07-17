const paymentStatusCounts = {
    CREATED: 0,
    ENRICHED: 0,
    COMPLETED: 0,
    FAILED_AT_ENRICHMENT: 0,
    FAILED_AT_PROCESSING: 0,
    FAILED_AT_PUBLISHING: 0
};

const paymentMap = new Map();
let stompClient = null;
let paymentChart = null;
let totalPayments = 0;

const SOCKET_URL = 'http://localhost:8084/ws';
const TOPIC = '/topic/payments';

const statusColors = {
    CREATED: '#4facfe',
    ENRICHED: '#00f2fe',
    COMPLETED: '#00c853',
    FAILED_AT_ENRICHMENT: '#ff9100',
    FAILED_AT_PROCESSING: '#ff5252',
    FAILED_AT_PUBLISHING: '#d500f9'
};

function initChart() {
    const ctx = document.getElementById('paymentChart').getContext('2d');

    paymentChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Object.keys(paymentStatusCounts),
            datasets: [{
                label: 'Payment Count',
                data: Object.values(paymentStatusCounts),
                backgroundColor: [
                    'rgba(79, 172, 254, 0.7)',
                    'rgba(0, 242, 254, 0.7)',
                    'rgba(0, 200, 83, 0.7)',
                    'rgba(255, 145, 0, 0.7)',
                    'rgba(255, 82, 82, 0.7)',
                    'rgba(213, 0, 249, 0.7)'
                ],
                borderColor: [
                    'rgb(79, 172, 254)',
                    'rgb(0, 242, 254)',
                    'rgb(0, 200, 83)',
                    'rgb(255, 145, 0)',
                    'rgb(255, 82, 82)',
                    'rgb(213, 0, 249)'
                ],
                borderWidth: 2,
                borderRadius: 6,
                borderSkipped: false,
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                title: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.7)',
                    titleFont: {
                        size: 14
                    },
                    bodyFont: {
                        size: 13
                    },
                    padding: 10,
                    displayColors: true,
                    callbacks: {
                        label: function (context) {
                            return `${context.parsed.y} payments`;
                        }
                    }
                },
                datalabels: {
                    anchor: 'end',
                    align: 'top',
                    color: '#fff',
                    font: {
                        weight: 'bold',
                        size: 14
                    },
                    formatter: function (value) {
                        return value;
                    }
                }
            },
            scales: {
                x: {
                    ticks: {
                        color: 'rgba(255, 255, 255, 0.8)',
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    }
                },
                y: {
                    beginAtZero: true,
                    ticks: {
                        color: 'rgba(255, 255, 255, 0.8)',
                        precision: 0
                    },
                    grid: {
                        color: 'rgba(255, 255, 255, 0.1)'
                    }
                }
            },
            animation: {
                duration: 500,
                easing: 'easeOutQuart'
            }
        },
        plugins: [ChartDataLabels]
    });
    initStatsCards();
}

function initStatsCards() {
    const statsGrid = document.getElementById('statsGrid');
    statsGrid.innerHTML = '';

    Object.entries(paymentStatusCounts).forEach(([status, count]) => {
        const statCard = document.createElement('div');
        statCard.className = 'stat-card';
        statCard.innerHTML = `
                    <div class="stat-value">${count}</div>
                    <div class="stat-label">${status.replace(/_/g, ' ')}</div>
                `;
        statsGrid.appendChild(statCard);
    });
}

function updateStatsCard(status) {
    const statCards = document.querySelectorAll('.stat-card');
    const statusIndex = Object.keys(paymentStatusCounts).indexOf(status);
    if (statusIndex !== -1) {
        const statValue = statCards[statusIndex + 1].querySelector('.stat-value');
        statValue.textContent = paymentStatusCounts[status];
    }

    document.getElementById('totalPayments').textContent = totalPayments;
}

function addLogEntry(message, status) {
    const logsContent = document.getElementById('logsContent');
    const now = new Date();
    const timestamp = now.toLocaleTimeString();

    const logEntry = document.createElement('div');
    logEntry.className = 'log-entry';
    logEntry.innerHTML = `
                <span class="log-timestamp">[${timestamp}]</span>
                <span class="log-message status-${status}">${message}</span>
            `;

    logsContent.prepend(logEntry);

    if (logsContent.children.length > 20) {
        logsContent.removeChild(logsContent.lastChild);
    }
}

function updatePaymentStatus(payment) {
    const prevStatus = paymentMap.get(payment.uuid);

    if (payment.status == "CREATED") {
        totalPayments++;
    }

    if (prevStatus) {
        paymentStatusCounts[prevStatus]--;
    }
    paymentStatusCounts[payment.status]++;
    paymentMap.set(payment.uuid, payment.status);

    paymentChart.data.datasets[0].data = Object.values(paymentStatusCounts);
    paymentChart.update();

    if (prevStatus) updateStatsCard(prevStatus);
    updateStatsCard(payment.status);

    const action = prevStatus ? `updated from ${prevStatus} to` : '';
    addLogEntry(`Payment ${payment.uuid} ${action} <strong>${payment.status}</strong>`, payment.status);
}

function connect() {
    const socket = new SockJS(SOCKET_URL);
    stompClient = Stomp.over(socket);

    stompClient.connect({},
        (frame) => {
            console.log('Connected: ' + frame);
            stompClient.subscribe(TOPIC, (message) => {
                const payment = JSON.parse(message.body);
                updatePaymentStatus(payment);
            });

            document.getElementById('connectBtn').disabled = true;
            document.getElementById('disconnectBtn').disabled = false;
            updateConnectionStatus(true);
        },
        (error) => {
            console.error('Connection error:', error);
            setTimeout(connect, 5000);
            updateConnectionStatus(false);
        }
    );
}

function disconnect() {
    if (stompClient) {
        stompClient.disconnect();

        document.getElementById('connectBtn').disabled = false;
        document.getElementById('disconnectBtn').disabled = true;
        updateConnectionStatus(false);

        addLogEntry('Disconnected from WebSocket server', 'FAILED_AT_PROCESSING');
    }
}

function updateConnectionStatus(connected) {
    const indicator = document.getElementById('statusIndicator');
    const statusText = document.getElementById('statusText');

    if (connected) {
        indicator.className = 'status-indicator status-connected';
        statusText.textContent = 'Connected to WebSocket server';
    } else {
        indicator.className = 'status-indicator status-disconnected';
        statusText.textContent = 'Disconnected from WebSocket server';
    }
}

window.addEventListener('DOMContentLoaded', () => {
    initChart();

    const statsGrid = document.getElementById('statsGrid');
    const totalCard = document.createElement('div');
    totalCard.className = 'stat-card';
    totalCard.innerHTML = `
                <div class="stat-value" id="totalPayments">0</div>
                <div class="stat-label">Total Payments</div>
            `;
    statsGrid.prepend(totalCard);

    document.getElementById('connectBtn').addEventListener('click', connect);
    document.getElementById('disconnectBtn').addEventListener('click', disconnect);
    document.getElementById('clearLogs').addEventListener('click', () => {
        document.getElementById('logsContent').innerHTML = '';
    });

    addLogEntry('System initialized. Click "Connect" to start receiving payment updates.', 'CREATED');
});