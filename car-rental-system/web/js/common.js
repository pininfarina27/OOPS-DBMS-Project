// ============================================================
// common.js — Shared helper functions for all pages
// ============================================================

// Call the backend API
async function apiCall(url, method, body) {
    var options = {
        method: method || 'GET',
        headers: { 'Content-Type': 'application/json' }
    };
    if (body) {
        options.body = JSON.stringify(body);
    }
    var response = await fetch(url, options);
    var data = await response.json();
    if (!response.ok) {
        throw new Error(data.error || 'Something went wrong');
    }
    return data;
}

// Format money in Indian Rupees
function formatCurrency(amount) {
    if (amount === null || amount === undefined || amount === 0) return '₹0';
    return '₹' + Number(amount).toLocaleString('en-IN');
}

// Show a toast notification
function showToast(message, type) {
    var container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    var toast = document.createElement('div');
    toast.className = 'toast toast-' + (type || 'info');
    toast.textContent = message;
    container.appendChild(toast);

    // Auto-remove after 3 seconds
    setTimeout(function() {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        setTimeout(function() {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
            }
        }, 300);
    }, 3000);
}

// Open a modal
function openModal(modalId) {
    document.getElementById(modalId).style.display = 'flex';
}

// Close a modal
function closeModal(modalId) {
    document.getElementById(modalId).style.display = 'none';
}

// Get colored status badge HTML
function getStatusBadge(status) {
    var color = 'blue';
    if (status === 'AVAILABLE' || status === 'COMPLETED') color = 'green';
    if (status === 'MAINTENANCE') color = 'orange';
    if (status === 'CANCELLED') color = 'red';
    return '<span class="badge badge-' + color + '">' + status + '</span>';
}

// Highlight the current page in sidebar
function highlightActiveNav() {
    var currentPage = window.location.pathname.split('/').pop() || 'index.html';
    var links = document.querySelectorAll('.sidebar-nav a');
    for (var i = 0; i < links.length; i++) {
        var href = links[i].getAttribute('href');
        if (href === currentPage || (currentPage === '' && href === 'index.html')) {
            links[i].classList.add('active');
        }
    }
}

// Format a date string nicely
function formatDate(dateStr) {
    if (!dateStr) return '-';
    return dateStr;
}

// Call highlightActiveNav when page loads
document.addEventListener('DOMContentLoaded', function() {
    highlightActiveNav();
});
