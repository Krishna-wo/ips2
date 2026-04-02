// app.js - Application Logic with Real API Calls

const REDIRECT_DELAY_MS = 800; // ms to show success message before redirect

// Initialize on page load
document.addEventListener('DOMContentLoaded', init);

async function init() {
    const token = localStorage.getItem('authToken');
    if (!token) {
        showAuthScreen();
        return;
    }

    // Load main portal
    document.getElementById('portalContainer').style.display = 'flex';
    setupNavigation();
    loadDashboard();
    setupEventListeners();
}

// ═══════════════════════════════════════════════════════════
// AUTHENTICATION SCREEN
// ═══════════════════════════════════════════════════════════

function showAuthScreen() {
    document.body.innerHTML = `
        <div style="display:flex;align-items:center;justify-content:center;min-height:100vh;background:linear-gradient(135deg,#0f4c75 0%,#1b6ca8 100%)">
            <div style="background:white;border-radius:16px;padding:40px;width:100%;max-width:420px;box-shadow:0 10px 40px rgba(15,76,117,0.2)">
                <div style="display:flex;align-items:center;gap:12px;margin-bottom:32px">
                    <div style="width:48px;height:48px;background:#00b4d8;border-radius:8px;display:flex;align-items:center;justify-content:center">
                        <svg style="width:24px;height:24px;fill:white" viewBox="0 0 24 24"><path d="M1 6l11 7L23 6V4L12 11 1 4v2zm0 14h22V8l-11 7L1 8v12z"/></svg>
                    </div>
                    <div>
                        <div style="font-size:20px;font-weight:600;color:#1a2b3c">NetPulse</div>
                        <div style="font-size:11px;color:#5a7a95">Customer Portal</div>
                    </div>
                </div>

                <div style="display:flex;gap:8px;margin-bottom:24px;border-bottom:1px solid #e0e8f0">
                    <button class="auth-tab active" onclick="switchAuthTab('login')" style="padding:10px 16px;border:none;background:none;font-size:14px;font-weight:500;color:#0f4c75;border-bottom:3px solid #0f4c75;cursor:pointer">Login</button>
                    <button class="auth-tab" onclick="switchAuthTab('register')" style="padding:10px 16px;border:none;background:none;font-size:14px;font-weight:500;color:#5a7a95;border-bottom:3px solid transparent;cursor:pointer">Register</button>
                </div>

                <!-- Login Form -->
                <form id="loginForm" class="auth-form active" onsubmit="handleLogin(event)">
                    <div style="margin-bottom:14px">
                        <label style="font-size:12px;font-weight:500;color:#5a7a95;margin-bottom:5px;display:block">Email</label>
                        <input type="email" id="loginEmail" required style="width:100%;padding:9px 12px;border:1px solid #e0e8f0;border-radius:8px;font-family:inherit;font-size:13px;color:#1a2b3c;outline:none" />
                    </div>
                    <div style="margin-bottom:14px">
                        <label style="font-size:12px;font-weight:500;color:#5a7a95;margin-bottom:5px;display:block">Password</label>
                        <input type="password" id="loginPassword" required style="width:100%;padding:9px 12px;border:1px solid #e0e8f0;border-radius:8px;font-family:inherit;font-size:13px;color:#1a2b3c;outline:none" />
                    </div>
                    <button type="submit" style="width:100%;padding:9px;border-radius:8px;font-family:inherit;font-size:13px;font-weight:600;cursor:pointer;background:#0f4c75;color:white;border:none;margin-top:14px">Login →</button>
                    <div id="loginError" style="color:#e74c3c;font-size:12px;margin-top:8px;text-align:center"></div>
                </form>

                <!-- Register Form -->
                <form id="registerForm" class="auth-form" onsubmit="handleRegister(event)">
                    <div style="display:grid;grid-template-columns:1fr 1fr;gap:10px;margin-bottom:14px">
                        <div>
                            <label style="font-size:12px;font-weight:500;color:#5a7a95;margin-bottom:5px;display:block">First Name</label>
                            <input type="text" id="regFirstName" required style="width:100%;padding:9px 12px;border:1px solid #e0e8f0;border-radius:8px;font-family:inherit;font-size:13px;color:#1a2b3c;outline:none" />
                        </div>
                        <div>
                            <label style="font-size:12px;font-weight:500;color:#5a7a95;margin-bottom:5px;display:block">Last Name</label>
                            <input type="text" id="regLastName" required style="width:100%;padding:9px 12px;border:1px solid #e0e8f0;border-radius:8px;font-family:inherit;font-size:13px;color:#1a2b3c;outline:none" />
                        </div>
                    </div>
                    <div style="margin-bottom:14px">
                        <label style="font-size:12px;font-weight:500;color:#5a7a95;margin-bottom:5px;display:block">Email</label>
                        <input type="email" id="regEmail" required style="width:100%;padding:9px 12px;border:1px solid #e0e8f0;border-radius:8px;font-family:inherit;font-size:13px;color:#1a2b3c;outline:none" />
                    </div>
                    <div style="margin-bottom:14px">
                        <label style="font-size:12px;font-weight:500;color:#5a7a95;margin-bottom:5px;display:block">Phone</label>
                        <input type="tel" id="regPhone" pattern="[0-9]{10}" required style="width:100%;padding:9px 12px;border:1px solid #e0e8f0;border-radius:8px;font-family:inherit;font-size:13px;color:#1a2b3c;outline:none" />
                    </div>
                    <div style="margin-bottom:14px">
                        <label style="font-size:12px;font-weight:500;color:#5a7a95;margin-bottom:5px;display:block">Password</label>
                        <input type="password" id="regPassword" minlength="8" required style="width:100%;padding:9px 12px;border:1px solid #e0e8f0;border-radius:8px;font-family:inherit;font-size:13px;color:#1a2b3c;outline:none" />
                    </div>
                    <div style="margin-bottom:14px">
                        <label style="font-size:12px;font-weight:500;color:#5a7a95;margin-bottom:5px;display:block">Address</label>
                        <input type="text" id="regAddress" required style="width:100%;padding:9px 12px;border:1px solid #e0e8f0;border-radius:8px;font-family:inherit;font-size:13px;color:#1a2b3c;outline:none" />
                    </div>

                    <!-- Plan Selection -->
                    <div style="margin-bottom:14px">
                        <label style="font-size:12px;font-weight:500;color:#5a7a95;margin-bottom:8px;display:block">Select a Plan <span style="color:#e74c3c">*</span></label>
                        <div id="plansList" style="display:grid;gap:8px;">
                            <div style="font-size:12px;color:#5a7a95;padding:8px;text-align:center">Loading plans...</div>
                        </div>
                        <input type="hidden" id="regPlanId" required />
                    </div>

                    <button type="submit" id="regSubmitBtn" style="width:100%;padding:9px;border-radius:8px;font-family:inherit;font-size:13px;font-weight:600;cursor:pointer;background:#0f4c75;color:white;border:none;margin-top:14px">Create Account →</button>
                    <div id="registerError" style="color:#e74c3c;font-size:12px;margin-top:8px;text-align:center"></div>
                    <div id="registerSuccess" style="color:#27ae60;font-size:12px;margin-top:8px;text-align:center;display:none"></div>
                </form>
            </div>
        </div>
    `;
}

function switchAuthTab(tab) {
    document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.auth-form').forEach(f => f.classList.remove('active'));

    event.target.classList.add('active');
    document.getElementById(tab + 'Form').classList.add('active');

    if (tab === 'register') {
        loadPlansForRegistration();
    }
}

async function loadPlansForRegistration() {
    const container = document.getElementById('plansList');
    if (!container) return;
    try {
        const response = await api.getActivePlans();
        const plans = response.data || [];
        if (plans.length === 0) {
            container.innerHTML = '<div style="font-size:12px;color:#5a7a95;padding:8px;text-align:center">No plans available</div>';
            return;
        }
        container.innerHTML = plans.map(p => `
            <div class="plan-card" data-plan-id="${p.id}" onclick="selectPlan(${p.id}, this)"
                style="border:2px solid #e0e8f0;border-radius:10px;padding:12px 14px;cursor:pointer;transition:border-color 0.2s,background 0.2s">
                <div style="display:flex;justify-content:space-between;align-items:center">
                    <div>
                        <div style="font-size:13px;font-weight:600;color:#1a2b3c">${p.name}</div>
                        <div style="font-size:11px;color:#5a7a95;margin-top:2px">${p.downloadSpeedMbps} Mbps · ${p.dataCapGb > 0 ? p.dataCapGb + ' GB' : 'Unlimited'}</div>
                        ${p.description ? `<div style="font-size:11px;color:#7a9ab5;margin-top:2px">${p.description}</div>` : ''}
                    </div>
                    <div style="text-align:right;flex-shrink:0;margin-left:12px">
                        <div style="font-size:15px;font-weight:700;color:#0f4c75">₹${p.monthlyPrice}</div>
                        <div style="font-size:10px;color:#5a7a95">/month</div>
                    </div>
                </div>
            </div>
        `).join('');
    } catch (err) {
        container.innerHTML = '<div style="font-size:12px;color:#e74c3c;padding:8px;text-align:center">Could not load plans. Please refresh.</div>';
    }
}

function selectPlan(planId, el) {
    document.querySelectorAll('.plan-card').forEach(c => {
        c.style.borderColor = '#e0e8f0';
        c.style.background = '';
    });
    el.style.borderColor = '#0f4c75';
    el.style.background = '#f0f7ff';
    document.getElementById('regPlanId').value = planId;
}

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    const errorDiv = document.getElementById('loginError');

    try {
        const response = await api.login(email, password);
        if (response.data && response.data.token) {
            localStorage.setItem('authToken', response.data.token);
            localStorage.setItem('userEmail', email);
            window.location.reload();
        }
    } catch (error) {
        errorDiv.textContent = error.message || 'Login failed';
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const errorDiv = document.getElementById('registerError');
    const successDiv = document.getElementById('registerSuccess');
    const submitBtn = document.getElementById('regSubmitBtn');
    errorDiv.textContent = '';
    successDiv.style.display = 'none';

    const planId = document.getElementById('regPlanId').value;
    if (!planId) {
        errorDiv.textContent = 'Please select a plan to continue.';
        return;
    }

    submitBtn.disabled = true;
    submitBtn.textContent = 'Creating account...';

    try {
        const response = await api.register({
            firstName: document.getElementById('regFirstName').value,
            lastName: document.getElementById('regLastName').value,
            email: document.getElementById('regEmail').value,
            phone: document.getElementById('regPhone').value,
            password: document.getElementById('regPassword').value,
            address: document.getElementById('regAddress').value,
            planId: parseInt(planId),
        });

        if (response.data && response.data.token) {
            successDiv.textContent = 'Account created! Logging you in...';
            successDiv.style.display = 'block';
            localStorage.setItem('authToken', response.data.token);
            localStorage.setItem('userEmail', response.data.email);
            setTimeout(() => window.location.reload(), REDIRECT_DELAY_MS);
        }
    } catch (error) {
        errorDiv.textContent = error.message || 'Registration failed';
        submitBtn.disabled = false;
        submitBtn.textContent = 'Create Account →';
    }
}

// ═══════════════════════════════════════════════════════════
// DASHBOARD & MAIN LOGIC
// ═══════════════════════════════════════════════════════════

async function loadDashboard() {
    try {
        const profileRes = await api.getProfile();
        const usageRes = await api.getUsageSummary();
        const ticketsRes = await api.getTickets();
        const outagesRes = await api.getActiveOutages();

        updateDashboard(profileRes.data, usageRes.data, ticketsRes.data, outagesRes.data);
    } catch (error) {
        console.error('Error loading dashboard:', error);
    }
}

function updateDashboard(profile, usage, tickets, outages) {
    // User info
    document.getElementById('userName').textContent = profile.firstName + ' ' + profile.lastName;
    document.getElementById('userPlan').textContent = profile.planName || 'No Plan';
    document.getElementById('userAvatar').textContent = (profile.firstName[0] + profile.lastName[0]).toUpperCase();

    // Stats
    document.getElementById('dashDataUsed').textContent = usage.downloadGb;
    document.getElementById('dashDataCap').textContent = `of ${usage.dataCapGb} GB plan`;
    document.getElementById('dashDataPercent').textContent = Math.round(usage.usagePercent) + '% used';

    // Usage bar
    document.getElementById('dashUsageBar').style.width = Math.min(usage.usagePercent, 100) + '%';
    document.getElementById('usedGb').textContent = usage.downloadGb + ' GB';
    document.getElementById('capGb').textContent = usage.dataCapGb + ' GB';

    // Tickets
    document.getElementById('dashOpenTickets').textContent = tickets.filter(t => t.status !== 'CLOSED').length;

    // Recent tickets
    const ticketsHtml = tickets.slice(0, 3).map(t => `
        <div class="ticket-item">
            <div class="ticket-num">${t.ticketNumber}</div>
            <div class="ticket-info">
                <div class="ticket-title">${t.title}</div>
                <div class="ticket-date">${new Date(t.createdAt).toLocaleDateString()}</div>
            </div>
            <span class="ticket-status ${t.status.toLowerCase()}">${t.status}</span>
        </div>
    `).join('');

    document.getElementById('recentTicketsContainer').innerHTML = ticketsHtml || '<div style="color: #5a7a95; font-size: 12px; padding: 10px 0;">No tickets</div>';

    // Outages
    const outagesHtml = outages.slice(0, 2).map(o => `
        <div class="outage-item">
            <div class="outage-dot ${o.severity.toLowerCase() === 'complete' ? 'red' : 'orange'}"></div>
            <div class="outage-info">
                <div class="outage-title">${o.issueType}</div>
                <div class="outage-meta">${o.description || 'Investigating...'}</div>
            </div>
            <span class="outage-badge ongoing">${o.status}</span>
        </div>
    `).join('');

    if (outagesHtml) {
        document.getElementById('serviceStatusContainer').innerHTML = outagesHtml;
    }
}

function setupNavigation() {
    document.querySelectorAll('.nav-item[data-screen]').forEach(item => {
        item.addEventListener('click', (e) => {
            const screen = item.dataset.screen;
            if (screen) {
                switchScreen(screen);
                document.querySelectorAll('.nav-item').forEach(i => i.classList.remove('active'));
                item.classList.add('active');
            }
        });
    });
}

function switchScreen(screenName) {
    document.querySelectorAll('.screen').forEach(s => s.classList.remove('active'));
    const screen = document.getElementById(`screen-${screenName}`);
    if (screen) {
        screen.classList.add('active');
        document.getElementById('page-title').textContent = screenName.charAt(0).toUpperCase() + screenName.slice(1).replace(/([A-Z])/g, ' $1');

        // Load data for specific screens
        if (screenName === 'billing') loadInvoices();
        if (screenName === 'tickets') loadTickets();
        if (screenName === 'outage') loadOutages();
    }
}

// ═══════════════════════════════════════════════════════════
// BILLING
// ═══════════════════════════════════════════════════════════

async function loadInvoices() {
    try {
        const response = await api.getInvoices();
        const html = response.data.map(inv => `
            <tr>
                <td class="mono">${inv.invoiceNumber}</td>
                <td class="mono">₹${inv.totalAmount}</td>
                <td><span class="inv-status ${inv.status.toLowerCase()}">${inv.status}</span></td>
                <td><button class="dl-btn" onclick="alert('PDF download')">PDF</button></td>
            </tr>
        `).join('');

        document.getElementById('invoiceTableBody').innerHTML = html || '<tr><td colspan="4" style="text-align:center;color:#5a7a95">No invoices</td></tr>';

        // Update pending bill
        const pending = response.data.find(i => i.status === 'PENDING');
        if (pending) {
            document.getElementById('billingAmount').textContent = pending.totalAmount;
            document.getElementById('billingDue').textContent = `Due ${pending.dueDate}`;
        }
    } catch (error) {
        console.error('Error loading invoices:', error);
    }
}

async function handlePayment(method) {
    try {
        const response = await api.getInvoices();
        const pending = response.data.find(i => i.status === 'PENDING');
        if (!pending) {
            alert('No pending invoices');
            return;
        }

        await api.payInvoice(pending.id, method);
        alert('Payment processed successfully!');
        loadInvoices();
    } catch (error) {
        alert('Payment failed: ' + error.message);
    }
}

// ═══════════════════════════════════════════════════════════
// TICKETS
// ═══════════════════════════════════════════════════════════

async function loadTickets() {
    try {
        const response = await api.getTickets();
        const html = response.data.map(t => `
            <div class="ticket-item">
                <div class="ticket-num">${t.ticketNumber}</div>
                <div class="ticket-info">
                    <div class="ticket-title">${t.title}</div>
                    <div class="ticket-date">${new Date(t.createdAt).toLocaleDateString()} · ${t.category}</div>
                </div>
                <span class="ticket-status ${t.status.toLowerCase()}">${t.status}</span>
            </div>
        `).join('');

        document.getElementById('ticketsList').innerHTML = html || '<div style="color:#5a7a95;font-size:12px;padding:10px 0">No tickets yet</div>';
    } catch (error) {
        console.error('Error loading tickets:', error);
    }
}

function showTicketForm() {
    document.getElementById('ticketModal').style.display = 'flex';
}

function closeTicketForm() {
    document.getElementById('ticketModal').style.display = 'none';
}

async function handleCreateTicket() {
    try {
        await api.createTicket(
            document.getElementById('ticketTitle').value,
            document.getElementById('ticketDesc').value,
            document.getElementById('ticketCategory').value
        );
        alert('Ticket created successfully!');
        closeTicketForm();
        loadTickets();
    } catch (error) {
        alert('Failed to create ticket: ' + error.message);
    }
}

// ═══════════════════════════════════════════════════════════
// OUTAGES
// ═══════════════════════════════════════════════════════════

async function loadOutages() {
    try {
        const response = await api.getActiveOutages();
        const html = response.data.map(o => `
            <div class="outage-item">
                <div class="outage-dot ${o.severity === 'COMPLETE' ? 'red' : o.severity === 'PARTIAL' ? 'orange' : 'yellow'}"></div>
                <div class="outage-info">
                    <div class="outage-title">${o.issueType}</div>
                    <div class="outage-meta">${o.description || 'Investigating...'}</div>
                </div>
                <span class="outage-badge ongoing">${o.status}</span>
            </div>
        `).join('');

        document.getElementById('outagesList').innerHTML = html || '<div style="color:#5a7a95;font-size:12px;padding:10px 0">No active outages</div>';
    } catch (error) {
        console.error('Error loading outages:', error);
    }
}

async function handleReportOutage() {
    try {
        await api.reportOutage(
            document.getElementById('outageType').value,
            document.getElementById('outageSeverity').value,
            document.getElementById('outageDesc').value
        );
        alert('Outage reported successfully!');
        document.getElementById('outageDesc').value = '';
        loadOutages();
    } catch (error) {
        alert('Failed to report outage: ' + error.message);
    }
}

// ═══════════════════════════════════════════════════════════
// SCHEDULE
// ═══════════════════════════════════════════════════════════

async function handleSchedule() {
    alert('Scheduling feature coming soon!');
}

// ═══════════════════════════════════════════════════════════
// LOGOUT
// ═══════════════════════════════════════════════════════════

function handleLogout() {
    api.logout();
    window.location.reload();
}

function setupEventListeners() {
    // Additional event listeners can be added here
}