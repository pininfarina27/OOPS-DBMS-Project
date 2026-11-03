// ============================================================
// customers.js — Customer management page logic
// Load, search customers. Register, edit, delete customers.
// ============================================================

// Store the customer ID being deleted
var customerToDeleteId = null;

// Load page on ready
window.onload = function() {
    loadCustomers();
    setupCustomerListeners();
};

// ---- SETUP LISTENERS ----

function setupCustomerListeners() {
    // Search: debounce input
    var searchTimer = null;
    document.getElementById('searchInput').addEventListener('input', function() {
        clearTimeout(searchTimer);
        searchTimer = setTimeout(function() {
            loadCustomers();
        }, 400);
    });

    // Customer form submit
    document.getElementById('customerForm').addEventListener('submit', function(event) {
        event.preventDefault();
        saveCustomer();
    });
}

// ---- LOAD CUSTOMERS TABLE ----

function loadCustomers() {
    var search = document.getElementById('searchInput').value;
    var query = '?search=' + encodeURIComponent(search);

    var tbody = document.getElementById('customersTableBody');
    tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">Loading...</td></tr>';

    apiCall('/api/customers' + query, 'GET', null)
        .then(function(customers) {
            if (customers.length === 0) {
                tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">No customers found.</td></tr>';
                return;
            }

            var html = '';
            for (var i = 0; i < customers.length; i++) {
                var c = customers[i];
                html += '<tr>';
                html += '<td>' + c.customerId + '</td>';
                html += '<td>' + c.firstName + '</td>';
                html += '<td>' + c.lastName + '</td>';
                html += '<td>' + c.email + '</td>';
                html += '<td>' + c.phone + '</td>';
                html += '<td>' + (c.address || '-') + '</td>';
                html += '<td>' + formatDate(c.registrationDate) + '</td>';
                html += '<td>';
                html += '<button class="btn btn-sm" onclick="openEditCustomer(' + c.customerId + ')">Edit</button> ';
                html += '<button class="btn btn-sm btn-danger" onclick="openDeleteCustomer(' + c.customerId + ')">Delete</button>';
                html += '</td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        })
        .catch(function(error) {
            showToast('Failed to load customers: ' + error.message, 'error');
            tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;">Error loading customers.</td></tr>';
        });
}

// ---- REGISTER (ADD) CUSTOMER ----

function openRegisterCustomer() {
    document.getElementById('customerModalTitle').textContent = 'Register Customer';
    document.getElementById('customerId').value = '';
    document.getElementById('firstName').value = '';
    document.getElementById('lastName').value = '';
    document.getElementById('email').value = '';
    document.getElementById('phone').value = '';
    document.getElementById('address').value = '';
    openModal('customerModal');
}

// ---- EDIT CUSTOMER ----

function openEditCustomer(customerId) {
    apiCall('/api/customers/' + customerId, 'GET', null)
        .then(function(c) {
            document.getElementById('customerModalTitle').textContent = 'Edit Customer';
            document.getElementById('customerId').value = c.customerId;
            document.getElementById('firstName').value = c.firstName;
            document.getElementById('lastName').value = c.lastName;
            document.getElementById('email').value = c.email;
            document.getElementById('phone').value = c.phone;
            document.getElementById('address').value = c.address || '';
            openModal('customerModal');
        })
        .catch(function(error) {
            showToast('Failed to load customer: ' + error.message, 'error');
        });
}

// ---- SAVE CUSTOMER (add or edit) ----

function saveCustomer() {
    var customerId = document.getElementById('customerId').value;
    var isEdit = customerId !== '';

    var customerData = {
        firstName: document.getElementById('firstName').value,
        lastName: document.getElementById('lastName').value,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        address: document.getElementById('address').value
    };

    var url = isEdit ? '/api/customers/' + customerId : '/api/customers';
    var method = isEdit ? 'PUT' : 'POST';

    apiCall(url, method, customerData)
        .then(function(result) {
            closeModal('customerModal');
            showToast(isEdit ? 'Customer updated!' : 'Customer registered!', 'success');
            loadCustomers();
        })
        .catch(function(error) {
            showToast('Error saving customer: ' + error.message, 'error');
        });
}

// ---- DELETE CUSTOMER ----

function openDeleteCustomer(customerId) {
    customerToDeleteId = customerId;
    openModal('deleteCustomerModal');

    document.getElementById('confirmDeleteCustomerBtn').onclick = function() {
        deleteCustomer(customerToDeleteId);
    };
}

function deleteCustomer(customerId) {
    apiCall('/api/customers/' + customerId, 'DELETE', null)
        .then(function(result) {
            closeModal('deleteCustomerModal');
            showToast('Customer deleted!', 'success');
            loadCustomers();
        })
        .catch(function(error) {
            closeModal('deleteCustomerModal');
            showToast('Cannot delete customer: ' + error.message, 'error');
        });
}
