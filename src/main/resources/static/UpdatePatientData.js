function updateVitalsData(vitals) {
    document.getElementById('heart_rate').innerText = vitals.heart_rate;
    document.getElementById('systolic_bp').innerText = vitals.systolicBP;
    document.getElementById('diastolic_bp').innerText = vitals.diastolicBP;
    document.getElementById('temperature').innerText = vitals.temperature;
    document.getElementById('oxygen_saturation').innerText = vitals.oxygen_saturation;
}

function startDataRefresh(patientId) {
    // Fetch the initial vitals data and update the UI
    fetch(`/monitorPatient/${patientId}/vitals`)
        .then(response => response.json())
        .then(vitals => {
            updateVitalsData(vitals);
        })
        .catch(error => {
            console.error('Error fetching vitals data:', error);
        });

    // Set up periodic data refresh every 5 seconds
    setInterval(() => {
        fetch(`/monitorPatient/${patientId}/vitals`)
            .then(response => response.json())
            .then(vitals => {
                updateVitalsData(vitals);
            })
            .catch(error => {
                console.error('Error fetching vitals data:', error);
            });
    }, 5000); // 5000 milliseconds = 5 seconds
}

// Extract patient ID from the URL
const urlParams = new URLSearchParams(window.location.search);
const patientId = urlParams.get('id');

// Start the data refresh process
startDataRefresh(patientId);