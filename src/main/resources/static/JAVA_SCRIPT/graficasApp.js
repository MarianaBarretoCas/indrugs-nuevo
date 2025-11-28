Chart.defaults.color = "#1a0346ff";
Chart.defaults.font.family = "'Poppins', sans-serif";

const printCharts = () => {
    renderTopChart();
    renderEnviosChart();
};

const getDataColors = opacity => {
    const colors = [
        '#00087a', // azul oscuro
        '#18ad57', // verde
        '#7bcfff'  // celeste
    ];
    return colors.map(color => opacity ? `${color + opacity}` : color);
};

const renderTopChart = async () => {
    const response = await fetch("/dashboard/medicamentosTop3");
    const dataApi = await response.json();

    const titles = dataApi.map(item => item.medicamento);
    const values = dataApi.map(item => item.totalEnviados);

    const ctx = document.getElementById("topMedicamentos");
    const data = {
        labels: titles,
        datasets: [{
            label: 'Cantidad de veces solicitado',
            data: values,
            borderColor: getDataColors('95'),
            backgroundColor: getDataColors('80'),
            hoverBackgroundColor: getDataColors('90'),
            borderWidth: 2,
            hoverBorderWidth: 3,
            hoverOffset: 25
        }]
    };

    const options = {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '45%',
        plugins: {
            legend: {
                position: 'top',
                labels: {
                    color: '#1a0346',
                    boxWidth: 14,
                    padding: 12,
                    font: { size: 13, weight: 'bold' }
                }
            },
            tooltip: {
                backgroundColor: '#1a0346',
                titleColor: '#fff',
                bodyColor: '#fff',
                callbacks: {
                    label: context => `${context.label}: ${context.parsed} envíos`
                }
            }
        },
        layout: {
            padding: 10
        }
    };

    new Chart(ctx, { type: 'doughnut', data, options });
};

const renderEnviosChart = async () => {
    const response = await fetch("/dashboard/enviosDelMes");
    const dataApi = await response.json();

    const titles = dataApi.map(item => item.estado);
    const values = dataApi.map(item => item.totalActivos);

    const ctx = document.getElementById("estadoEnvios");

    const gradient = ctx.getContext('2d').createLinearGradient(0, 0, 0, 200);
    gradient.addColorStop(0, 'rgba(24, 173, 87, 0.4)');
    gradient.addColorStop(1, 'rgba(24, 173, 87, 0.05)');

    const data = {
        labels: titles,
        datasets: [{
            label: 'Cantidad de domicilios',
            data: values,
            borderColor: '#18ad57',
            backgroundColor: gradient,
            fill: true,
            tension: 0.4, // curva suave
            pointBackgroundColor: '#18ad57',
            pointBorderWidth: 2,
            pointRadius: 5,
            pointHoverRadius: 7,
        }]
    };

    const options = {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
            legend: {
                position: 'top',
                labels: {
                    color: '#1a0346',
                    font: { size: 13, weight: 'bold' }
                }
            },
            tooltip: {
                backgroundColor: '#18ad57',
                titleColor: '#fff',
                bodyColor: '#fff',
                callbacks: {
                    label: context => `${context.parsed.y} domicilios`
                }
            }
        },
        scales: {
            x: {
                grid: { display: false },
                ticks: { color: '#1a0346' }
            },
            y: {
                grid: { color: 'rgba(0,0,0,0.05)' },
                ticks: { color: '#1a0346', stepSize: 1 }
            }
        }
    };

    new Chart(ctx, { type: 'line', data, options });
};

printCharts();

function exportarReporte() {


    const chart1 = document.getElementById('topMedicamentos').toDataURL("image/png");
    const chart2 = document.getElementById('estadoEnvios').toDataURL("image/png");

    const formData = new FormData();
    formData.append('chart1', chart1);
    formData.append('chart2', chart2);

    fetch('/dashboard/export/reporte_dashboard', {
        method: 'POST',
        body: formData
    })

    .then(response => {
        if (response.ok) {
            return response.blob();
        } else {
            throw new Error('Error al generar el PDF');
        }
    })
    .then(blob => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'reporte_dashboard_Indrugs.pdf';
        document.body.appendChild(a);
        a.click();
    })
    .catch(error => {
        console.error('Error:', error);
    });
}
