// ============================================================
// rentals.js — Rentals page logic
// Show active rentals. Create new rental. Process car return.
// ============================================================

// Store which rental we are returning
var rentalToReturnId = null;

// Selected car's daily rate (for estimate calculation)
var selectedDailyRate = 0;

// Load on page ready
window.onload = function() {
    loadActiveRentals();
    setupRentalListeners();
};

// ---- SETUP LISTENERS ----

function setupRentalListeners() {
    // Rental form submit
    document.getElementById('rentalForm').addEventListener('submit', function(event) {
        event.preventDefault();
        createRental();
    });

    // When car is selected, update daily rate shown
    document.getElementById('rentalCar').addEventListener('change', function() {
        updateEstimate();
    });

    // When dates change, recalculate estimate
    document.getElementById('rentalDate').addEventListener('change', function() {
        updateEstimate();
    });
    document.getElementById('returnDate').addEventListener('change', function() {
        updateEstimate();
    });
}

// ---- LOAD ACTIVE RENTALS TABLE ----

function loadActiveRentals() {
    var tbody = document.getElementById('rentalsTableBody');
    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">Loading...</td></tr>';

    apiCall('/api/rentals?status=ACTIVE', 'GET', null)
        .then(function(rentals) {
            if (rentals.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">No active rentals.</td></tr>';
                return;
            }

            var html = '';
            for (var i = 0; i < rentals.length; i++) {
                var r = rentals[i];
                var carName = r.carBrand + ' ' + r.carModel;
                html += '<tr>';
                html += '<td>' + r.rentalId + '</td>';
                html += '<td>' + r.customerName + '</td>';
                html += '<td>' + carName + '</td>';
                html += '<td>' + r.licensePlate + '</td>';
                html += '<td>' + formatCurrency(r.dailyRate) + '/day</td>';
                html += '<td>' + formatDate(r.rentalDate) + '</td>';
                html += '<td>' + formatDate(r.returnDate) + '</td>';
                html += '<td>';
                html += '<button class="btn btn-sm btn-success" onclick="openReturnModal(' + r.rentalId + ', \'' + carName + '\', \'' + r.customerName + '\')">Return Car</button>';
                html += '</td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        })
        .catch(function(error) {
            showToast('Failed to load rentals: ' + error.message, 'error');
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">Error loading rentals.</td></tr>';
        });
}

// ---- NEW RENTAL MODAL ----

function openNewRentalModal() {
    // Reset the form
    document.getElementById('rentalForm').reset();
    document.getElementById('rentalEstimate').style.display = 'none';
    selectedDailyRate = 0;

    // Set today's date as default rental date
    var today = new Date();
    var yyyy = today.getFullYear();
    var mm = String(today.getMonth() + 1).padStart(2, '0');
    var dd = String(today.getDate()).padStart(2, '0');
    document.getElementById('rentalDate').value = yyyy + '-' + mm + '-' + dd;

    // Set tomorrow as default return date
    var tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    var yyyy2 = tomorrow.getFullYear();
    var mm2 = String(tomorrow.getMonth() + 1).padStart(2, '0');
    var dd2 = String(tomorrow.getDate()).padStart(2, '0');
    document.getElementById('returnDate').value = yyyy2 + '-' + mm2 + '-' + dd2;

    // Load customers dropdown
    loadCustomerDropdown();

    // Load available cars dropdown
    loadAvailableCarDropdown();

    openModal('rentalModal');
}

function loadCustomerDropdown() {
    var select = document.getElementById('rentalCustomer');
    select.innerHTML = '<option value="">Select Customer</option>';

    apiCall('/api/customers', 'GET', null)
        .then(function(customers) {
            for (var i = 0; i < customers.length; i++) {
                var c = customers[i];
                var opt = document.createElement('option');
                opt.value = c.customerId;
                opt.textContent = c.firstName + ' ' + c.lastName + ' (' + c.email + ')';
                select.appendChild(opt);
            }
        })
        .catch(function(error) {
            showToast('Failed to load customers: ' + error.message, 'error');
        });
}

function loadAvailableCarDropdown() {
    var select = document.getElementById('rentalCar');
    select.innerHTML = '<option value="">Select Car</option>';

    // Only available cars can be rented
    apiCall('/api/cars?status=AVAILABLE', 'GET', null)
        .then(function(cars) {
            for (var i = 0; i < cars.length; i++) {
                var car = cars[i];
                var opt = document.createElement('option');
                opt.value = car.carId;
                // Store daily rate in data attribute
                opt.setAttribute('data-rate', car.dailyRate);
                opt.textContent = car.brand + ' ' + car.model + ' (' + car.licensePlate + ') - ' + formatCurrency(car.dailyRate) + '/day';
                select.appendChild(opt);
            }
        })
        .catch(function(error) {
            showToast('Failed to load cars: ' + error.message, 'error');
        });
}

// ---- ESTIMATE CALCULATION ----

function updateEstimate() {
    var carSelect = document.getElementById('rentalCar');
    var selectedOption = carSelect.options[carSelect.selectedIndex];
    var rentalDateVal = document.getElementById('rentalDate').value;
    var returnDateVal = document.getElementById('returnDate').value;
    var estimateDiv = document.getElementById('rentalEstimate');

    if (!selectedOption || !selectedOption.value) {
        estimateDiv.style.display = 'none';
        return;
    }

    selectedDailyRate = parseFloat(selectedOption.getAttribute('data-rate')) || 0;

    if (!rentalDateVal || !returnDateVal || selectedDailyRate <= 0) {
        estimateDiv.style.display = 'none';
        return;
    }

    // Calculate number of days
    var d1 = new Date(rentalDateVal);
    var d2 = new Date(returnDateVal);
    var diffTime = d2 - d1;
    var diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays <= 0) {
        estimateDiv.style.display = 'block';
        estimateDiv.textContent = 'Return date must be after rental date.';
        estimateDiv.style.color = '#EF4444';
        return;
    }

    var estimatedCost = diffDays * selectedDailyRate;
    estimateDiv.style.display = 'block';
    estimateDiv.style.color = '#1E293B';
    estimateDiv.textContent = 'Estimated Cost: ' + formatCurrency(estimatedCost) + ' (' + diffDays + ' day' + (diffDays > 1 ? 's' : '') + ' × ' + formatCurrency(selectedDailyRate) + '/day)';
}

// ---- CREATE RENTAL ----

function createRental() {
    var customerId = document.getElementById('rentalCustomer').value;
    var carId = document.getElementById('rentalCar').value;
    var rentalDate = document.getElementById('rentalDate').value;
    var returnDate = document.getElementById('returnDate').value;

    if (!customerId || !carId || !rentalDate || !returnDate) {
        showToast('Please fill in all fields.', 'error');
        return;
    }

    var rentalData = {
        carId: parseInt(carId),
        customerId: parseInt(customerId),
        rentalDate: rentalDate,
        returnDate: returnDate
    };

    apiCall('/api/rentals', 'POST', rentalData)
        .then(function(result) {
            closeModal('rentalModal');
            showToast('Rental created successfully!', 'success');
            loadActiveRentals();
        })
        .catch(function(error) {
            showToast('Error creating rental: ' + error.message, 'error');
        });
}

// ---- RETURN CAR ----

function openReturnModal(rentalId, carName, customerName) {
    rentalToReturnId = rentalId;

    // Show info in the modal
    document.getElementById('returnInfo').textContent =
        'Return "' + carName + '" rented by ' + customerName + '? The total cost will be calculated automatically based on actual return date.';

    openModal('returnModal');

    // Set up confirm button
    document.getElementById('confirmReturnBtn').onclick = function() {
        confirmReturn(rentalToReturnId);
    };
}

function confirmReturn(rentalId) {
    apiCall('/api/rentals/' + rentalId + '/return', 'PUT', null)
        .then(function(result) {
            closeModal('returnModal');
            showToast('Car returned successfully!', 'success');
            loadActiveRentals();
        })
        .catch(function(error) {
            closeModal('returnModal');
            showToast('Error returning car: ' + error.message, 'error');
        });
}
