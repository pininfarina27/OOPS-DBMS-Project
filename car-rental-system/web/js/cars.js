// ============================================================
// cars.js — Car management page logic
// Load, search, filter, sort cars. Add, edit, delete cars.
// ============================================================

// Store the car ID being deleted so the confirm button knows which one
var carToDeleteId = null;

// Load everything when page is ready
window.onload = function() {
    loadCategories();
    loadCars();
    setupEventListeners();
};

// ---- SETUP LISTENERS ----

function setupEventListeners() {
    // Search: debounce so we don't fire on every keystroke
    var searchTimer = null;
    document.getElementById('searchInput').addEventListener('input', function() {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(function() {
            loadCars();
        }, 400);
    });

    // Status filter change
    document.getElementById('statusFilter').addEventListener('change', function() {
        loadCars();
    });

    // Category filter change
    document.getElementById('categoryFilter').addEventListener('change', function() {
        loadCars();
    });

    // Sort filter change
    document.getElementById('sortFilter').addEventListener('change', function() {
        loadCars();
    });

    // Car form submit (add or edit)
    document.getElementById('carForm').addEventListener('submit', function(event) {
        event.preventDefault();
        saveCar();
    });
}

// ---- LOAD CATEGORIES INTO DROPDOWNS ----

function loadCategories() {
    apiCall('/api/categories', 'GET', null)
        .then(function(categories) {
            // Populate toolbar category filter
            var filterSelect = document.getElementById('categoryFilter');
            // Keep the first "All Categories" option
            while (filterSelect.options.length > 1) {
                filterSelect.remove(1);
            }
            for (var i = 0; i < categories.length; i++) {
                var opt = document.createElement('option');
                opt.value = categories[i].categoryId;
                opt.textContent = categories[i].categoryName;
                filterSelect.appendChild(opt);
            }

            // Also populate the modal category dropdown
            var modalSelect = document.getElementById('carCategory');
            while (modalSelect.options.length > 1) {
                modalSelect.remove(1);
            }
            for (var j = 0; j < categories.length; j++) {
                var opt2 = document.createElement('option');
                opt2.value = categories[j].categoryId;
                opt2.textContent = categories[j].categoryName;
                modalSelect.appendChild(opt2);
            }
        })
        .catch(function(error) {
            showToast('Failed to load categories: ' + error.message, 'error');
        });
}

// ---- LOAD CARS TABLE ----

function loadCars() {
    // Read current filter values
    var search = document.getElementById('searchInput').value;
    var status = document.getElementById('statusFilter').value;
    var category = document.getElementById('categoryFilter').value;
    var sortValue = document.getElementById('sortFilter').value;

    // Map sort dropdown values to API params
    var sortBy = '';
    var order = '';
    if (sortValue === 'price-low') {
        sortBy = 'daily_rate';
        order = 'asc';
    } else if (sortValue === 'price-high') {
        sortBy = 'daily_rate';
        order = 'desc';
    } else if (sortValue === 'newest') {
        sortBy = 'car_id';
        order = 'desc';
    } else if (sortValue === 'brand-az') {
        sortBy = 'brand';
        order = 'asc';
    }

    // Build query string
    var query = '?search=' + encodeURIComponent(search)
              + '&status=' + encodeURIComponent(status)
              + '&category=' + encodeURIComponent(category)
              + '&sortBy=' + encodeURIComponent(sortBy)
              + '&order=' + encodeURIComponent(order);

    var tbody = document.getElementById('carsTableBody');
    tbody.innerHTML = '<tr><td colspan="10" style="text-align:center;">Loading...</td></tr>';

    apiCall('/api/cars' + query, 'GET', null)
        .then(function(cars) {
            if (cars.length === 0) {
                tbody.innerHTML = '<tr><td colspan="10" style="text-align:center;">No cars found.</td></tr>';
                return;
            }

            var html = '';
            for (var i = 0; i < cars.length; i++) {
                var car = cars[i];
                html += '<tr>';
                html += '<td>' + car.carId + '</td>';
                html += '<td>' + car.brand + '</td>';
                html += '<td>' + car.model + '</td>';
                html += '<td>' + car.year + '</td>';
                html += '<td>' + car.licensePlate + '</td>';
                html += '<td>' + (car.categoryName || '-') + '</td>';
                html += '<td>' + (car.color || '-') + '</td>';
                html += '<td>' + formatCurrency(car.dailyRate) + '</td>';
                html += '<td>' + getStatusBadge(car.status) + '</td>';
                html += '<td>';
                html += '<button class="btn btn-sm" onclick="openEditCar(' + car.carId + ')">Edit</button> ';
                html += '<button class="btn btn-sm btn-danger" onclick="openDeleteCar(' + car.carId + ')">Delete</button>';
                html += '</td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        })
        .catch(function(error) {
            showToast('Failed to load cars: ' + error.message, 'error');
            tbody.innerHTML = '<tr><td colspan="10" style="text-align:center;">Error loading cars.</td></tr>';
        });
}

// ---- ADD CAR ----

function openAddCar() {
    // Reset form fields
    document.getElementById('carModalTitle').textContent = 'Add New Car';
    document.getElementById('carId').value = '';
    document.getElementById('carCategory').value = '';
    document.getElementById('carBrand').value = '';
    document.getElementById('carModel').value = '';
    document.getElementById('carYear').value = '';
    document.getElementById('carPlate').value = '';
    document.getElementById('carColor').value = '';
    document.getElementById('carRate').value = '';
    document.getElementById('carStatus').value = 'AVAILABLE';
    openModal('carModal');
}

// ---- EDIT CAR ----

function openEditCar(carId) {
    // Fetch this car's data first
    apiCall('/api/cars/' + carId, 'GET', null)
        .then(function(car) {
            document.getElementById('carModalTitle').textContent = 'Edit Car';
            document.getElementById('carId').value = car.carId;
            document.getElementById('carCategory').value = car.categoryId || '';
            document.getElementById('carBrand').value = car.brand;
            document.getElementById('carModel').value = car.model;
            document.getElementById('carYear').value = car.year;
            document.getElementById('carPlate').value = car.licensePlate;
            document.getElementById('carColor').value = car.color || '';
            document.getElementById('carRate').value = car.dailyRate;
            document.getElementById('carStatus').value = car.status;
            openModal('carModal');
        })
        .catch(function(error) {
            showToast('Failed to load car details: ' + error.message, 'error');
        });
}

// ---- SAVE CAR (both add and edit) ----

function saveCar() {
    var carId = document.getElementById('carId').value;
    var isEdit = carId !== '';

    // Collect form data
    var carData = {
        categoryId: parseInt(document.getElementById('carCategory').value),
        brand: document.getElementById('carBrand').value,
        model: document.getElementById('carModel').value,
        year: parseInt(document.getElementById('carYear').value),
        licensePlate: document.getElementById('carPlate').value,
        color: document.getElementById('carColor').value,
        dailyRate: parseFloat(document.getElementById('carRate').value),
        status: document.getElementById('carStatus').value
    };

    var url = isEdit ? '/api/cars/' + carId : '/api/cars';
    var method = isEdit ? 'PUT' : 'POST';

    apiCall(url, method, carData)
        .then(function(result) {
            closeModal('carModal');
            showToast(isEdit ? 'Car updated successfully!' : 'Car added successfully!', 'success');
            loadCars();
        })
        .catch(function(error) {
            showToast('Error saving car: ' + error.message, 'error');
        });
}

// ---- DELETE CAR ----

function openDeleteCar(carId) {
    carToDeleteId = carId;
    openModal('deleteModal');

    // Set up confirm button
    document.getElementById('confirmDeleteBtn').onclick = function() {
        deleteCar(carToDeleteId);
    };
}

function deleteCar(carId) {
    apiCall('/api/cars/' + carId, 'DELETE', null)
        .then(function(result) {
            closeModal('deleteModal');
            showToast('Car deleted successfully!', 'success');
            loadCars();
        })
        .catch(function(error) {
            closeModal('deleteModal');
            showToast('Cannot delete car: ' + error.message, 'error');
        });
}
