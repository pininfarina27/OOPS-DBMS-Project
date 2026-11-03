// ============================================================
// history.js — Rental History page logic
// Shows all rentals with status filter
// ============================================================

// Load on page ready
window.onload = function() {
    loadHistory();
    setupHistoryListeners();
};

// ---- SETUP LISTENERS ----

function setupHistoryListeners() {
    // Status filter change → reload table
    document.getElementById('statusFilter').addEventListener('change', function() {
        loadHistory();
    });
}

// ---- LOAD HISTORY TABLE ----

function loadHistory() {
    var status = document.getElementById('statusFilter').value;
    var query = status ? '?status=' + encodeURIComponent(status) : '';

    var tbody = document.getElementById('historyTableBody');
    tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;">Loading...</td></tr>';

    apiCall('/api/rentals' + query, 'GET', null)
        .then(function(rentals) {
            if (rentals.length === 0) {
                tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;">No rental history found.</td></tr>';
                return;
            }

            var html = '';
            for (var i = 0; i < rentals.length; i++) {
                var r = rentals[i];
                var carName = r.carBrand + ' ' + r.carModel;
                var totalCostDisplay = r.totalCost ? formatCurrency(r.totalCost) : '-';
                var actualReturn = r.actualReturnDate ? formatDate(r.actualReturnDate) : '-';

                html += '<tr>';
                html += '<td>' + r.rentalId + '</td>';
                html += '<td>' + r.customerName + '</td>';
                html += '<td>' + carName + '</td>';
                html += '<td>' + r.licensePlate + '</td>';
                html += '<td>' + formatDate(r.rentalDate) + '</td>';
                html += '<td>' + formatDate(r.returnDate) + '</td>';
                html += '<td>' + actualReturn + '</td>';
                html += '<td>' + totalCostDisplay + '</td>';
                html += '<td>' + getStatusBadge(r.status) + '</td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        })
        .catch(function(error) {
            showToast('Failed to load history: ' + error.message, 'error');
            tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;">Error loading history.</td></tr>';
        });
}
