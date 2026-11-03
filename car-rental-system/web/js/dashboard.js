// ============================================================
// dashboard.js — Dashboard page logic
// Fetches stats and recent rentals from the backend API
// ============================================================

// Load dashboard data when page loads
window.onload = function() {
    loadDashboard();
};

// Main function: fetch dashboard stats and populate the page
function loadDashboard() {
    // Fetch stats from GET /api/dashboard
    apiCall('/api/dashboard', 'GET', null)
        .then(function(data) {
            // Populate the 4 stat cards
            document.getElementById('totalCarsNum').textContent = data.totalCars || 0;
            document.getElementById('availableCarsNum').textContent = data.availableCars || 0;
            document.getElementById('activeRentalsNum').textContent = data.activeRentals || 0;
            document.getElementById('totalRevenueNum').textContent = formatCurrency(data.totalRevenue || 0);

            // Populate recent rentals table
            var tbody = document.getElementById('recentRentalsBody');
            var recentList = data.recentRentals;

            if (!recentList || recentList.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">No recent rentals found.</td></tr>';
                return;
            }

            // Build table rows
            var html = '';
            for (var i = 0; i < recentList.length; i++) {
                var r = recentList[i];
                // dashboard API returns carName directly (not carBrand/carModel separately)
                var carName = r.carName || (r.carBrand + ' ' + r.carModel);
                var amount = r.totalCost ? formatCurrency(r.totalCost) : '-';
                html += '<tr>';
                html += '<td>' + r.rentalId + '</td>';
                html += '<td>' + r.customerName + '</td>';
                html += '<td>' + carName + '</td>';
                html += '<td>' + formatDate(r.rentalDate) + '</td>';
                html += '<td>' + getStatusBadge(r.status) + '</td>';
                html += '<td>' + amount + '</td>';
                html += '</tr>';
            }
            tbody.innerHTML = html;
        })
        .catch(function(error) {
            showToast('Failed to load dashboard: ' + error.message, 'error');
            var tbody = document.getElementById('recentRentalsBody');
            tbody.innerHTML = '<tr><td colspan="6" style="text-align:center;">Error loading data.</td></tr>';
        });
}
