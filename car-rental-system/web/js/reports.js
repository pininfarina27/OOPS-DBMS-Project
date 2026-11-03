// ============================================================
// reports.js — Reports & Analytics page logic
// Fetches all 4 reports and renders them as bar charts
// ============================================================

// Load all reports when page is ready
window.onload = function() {
    loadRevenueByCategory();
    loadPopularCars();
    loadTopCustomers();
    loadMonthlyRevenue();
};

// ---- HELPER: Build a visual bar chart row ----

function buildBarRow(label, value, maxValue, unit) {
    // Calculate width as percentage of max value
    var widthPercent = 0;
    if (maxValue > 0) {
        widthPercent = Math.round((value / maxValue) * 100);
    }

    var displayValue = unit === 'currency' ? formatCurrency(value) : value;

    var html = '<div class="report-bar">';
    html += '<div class="report-bar-label">' + label + '</div>';
    html += '<div class="report-bar-track">';
    html += '<div class="report-bar-fill" style="width:' + widthPercent + '%"></div>';
    html += '</div>';
    html += '<div class="report-bar-value">' + displayValue + '</div>';
    html += '</div>';
    return html;
}

// ---- REPORT 1: Revenue by Category ----

function loadRevenueByCategory() {
    var container = document.getElementById('revenueByCategoryReport');
    container.innerHTML = 'Loading...';

    apiCall('/api/reports/revenue-by-category', 'GET', null)
        .then(function(data) {
            if (!data || data.length === 0) {
                container.innerHTML = 'No data available.';
                return;
            }

            // Find max revenue for bar scaling
            var maxRevenue = 0;
            for (var i = 0; i < data.length; i++) {
                if (data[i].totalRevenue > maxRevenue) {
                    maxRevenue = data[i].totalRevenue;
                }
            }

            var html = '';
            for (var j = 0; j < data.length; j++) {
                var item = data[j];
                html += buildBarRow(item.categoryName, item.totalRevenue, maxRevenue, 'currency');
            }
            container.innerHTML = html;
        })
        .catch(function(error) {
            container.innerHTML = 'Error loading report.';
            showToast('Revenue report error: ' + error.message, 'error');
        });
}

// ---- REPORT 2: Popular Cars ----

function loadPopularCars() {
    var container = document.getElementById('popularCarsReport');
    container.innerHTML = 'Loading...';

    apiCall('/api/reports/popular-cars', 'GET', null)
        .then(function(data) {
            if (!data || data.length === 0) {
                container.innerHTML = 'No data available.';
                return;
            }

            // Find max rental count for scaling
            var maxCount = 0;
            for (var i = 0; i < data.length; i++) {
                if (data[i].rentalCount > maxCount) {
                    maxCount = data[i].rentalCount;
                }
            }

            var html = '';
            for (var j = 0; j < data.length; j++) {
                var item = data[j];
                var carLabel = item.carName;
                html += buildBarRow(carLabel, item.rentalCount, maxCount, 'count');
            }
            container.innerHTML = html;
        })
        .catch(function(error) {
            container.innerHTML = 'Error loading report.';
            showToast('Popular cars report error: ' + error.message, 'error');
        });
}

// ---- REPORT 3: Top Customers ----

function loadTopCustomers() {
    var container = document.getElementById('topCustomersReport');
    container.innerHTML = 'Loading...';

    apiCall('/api/reports/top-customers', 'GET', null)
        .then(function(data) {
            if (!data || data.length === 0) {
                container.innerHTML = 'No data available.';
                return;
            }

            // Find max totalSpent for scaling
            var maxSpent = 0;
            for (var i = 0; i < data.length; i++) {
                if (data[i].totalSpent > maxSpent) {
                    maxSpent = data[i].totalSpent;
                }
            }

            var html = '';
            for (var j = 0; j < data.length; j++) {
                var item = data[j];
                var customerLabel = item.customerName + ' (' + item.rentalCount + ' rental' + (item.rentalCount !== 1 ? 's' : '') + ')';
                html += buildBarRow(customerLabel, item.totalSpent, maxSpent > 0 ? maxSpent : 1, 'currency');
            }
            container.innerHTML = html;
        })
        .catch(function(error) {
            container.innerHTML = 'Error loading report.';
            showToast('Top customers report error: ' + error.message, 'error');
        });
}

// ---- REPORT 4: Monthly Revenue ----

function loadMonthlyRevenue() {
    var container = document.getElementById('monthlyRevenueReport');
    container.innerHTML = 'Loading...';

    apiCall('/api/reports/monthly-revenue', 'GET', null)
        .then(function(data) {
            if (!data || data.length === 0) {
                container.innerHTML = 'No data available.';
                return;
            }

            // Find max revenue for scaling
            var maxRevenue = 0;
            for (var i = 0; i < data.length; i++) {
                if (data[i].totalRevenue > maxRevenue) {
                    maxRevenue = data[i].totalRevenue;
                }
            }

            var html = '';
            for (var j = 0; j < data.length; j++) {
                var item = data[j];
                // API returns month and year as numbers — format them
                var monthNames = ['', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
                var monthLabel = monthNames[item.month] + ' ' + item.year;
                html += buildBarRow(monthLabel, item.totalRevenue, maxRevenue, 'currency');
            }
            container.innerHTML = html;
        })
        .catch(function(error) {
            container.innerHTML = 'Error loading report.';
            showToast('Monthly revenue report error: ' + error.message, 'error');
        });
}
