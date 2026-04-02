// api.js - API Communication Layer
const API_BASE_URL = 'http://localhost:8080/api';

class NetPulseAPI {
    constructor() {
        this.token = localStorage.getItem('authToken');
    }

    // Plans
    async getActivePlans() {
        return this.get('/plans/active');
    }

    // Auth
    async register(data) {
        console.log(data)
        return this.post('/auth/register', data);
    }

    async login(email, password) {
        const response = await this.post('/auth/login', { email, password });
        if (response.data && response.data.token) {
            this.token = response.data.token;
            localStorage.setItem('authToken', this.token);
            localStorage.setItem('userEmail', response.data.email);
        }
        return response;
    }

    // Customer
    async getProfile() {
        return this.get('/customer/profile');
    }

    // Billing
    async getInvoices() {
        return this.get('/billing/invoices');
    }

    async getPendingInvoices() {
        return this.get('/billing/pending');
    }

    async payInvoice(invoiceId, paymentMethod) {
        return this.post('/billing/pay', { invoiceId, paymentMethod });
    }

    // Usage
    async getUsageSummary() {
        return this.get('/usage/summary');
    }

    // Tickets
    async getTickets() {
        return this.get('/tickets');
    }

    async createTicket(title, description, category) {
        return this.post('/tickets', { title, description, category });
    }

    // Outages
    async reportOutage(issueType, severity, description) {
        return this.post('/outages/report', { issueType, severity, description });
    }

    async getActiveOutages() {
        return this.get('/outages/active');
    }

    // Helper methods
    async get(endpoint) {
        return this.request(endpoint, 'GET');
    }

    async post(endpoint, data) {
        return this.request(endpoint, 'POST', data);
    }

    async request(endpoint, method, data = null) {
        const headers = {
            'Content-Type': 'application/json',
        };

        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }

        try {
            const response = await fetch(API_BASE_URL + endpoint, {
                method,
                headers,
                body: data ? JSON.stringify(data) : null,
            });

            const json = await response.json();

            if (!response.ok) {
                throw new Error(json.message || 'API Error');
            }

            return json;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    logout() {
        this.token = null;
        localStorage.removeItem('authToken');
        localStorage.removeItem('userEmail');
    }
}

const api = new NetPulseAPI();