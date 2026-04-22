/* ============================================================
   JOBPORTAL – MAIN JAVASCRIPT
   ============================================================ */

// ── Auto-dismiss flash messages ──
document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('.flash').forEach(el => {
    setTimeout(() => {
      el.style.transition = 'opacity 0.5s';
      el.style.opacity = '0';
      setTimeout(() => el.remove(), 500);
    }, 4000);
  });

  // Active nav highlight
  const path = window.location.pathname;
  document.querySelectorAll('.nav-link, .dash-nav-item').forEach(link => {
    if (link.getAttribute('href') && path.startsWith(link.getAttribute('href')) && link.getAttribute('href') !== '/') {
      link.classList.add('nav-active', 'active');
    }
  });

  // Table search
  initTableSearch();

  // Confirm deletes
  document.querySelectorAll('[data-confirm]').forEach(btn => {
    btn.addEventListener('click', e => {
      if (!confirm(btn.dataset.confirm)) e.preventDefault();
    });
  });

  // Animate stats numbers
  animateCounters();
});

// ── Mobile nav toggle ──
function toggleNav() {
  const links = document.querySelector('.nav-links');
  if (links) links.classList.toggle('nav-open');
}

// ── Table live search ──
function initTableSearch() {
  const searchInput = document.getElementById('tableSearch');
  if (!searchInput) return;
  searchInput.addEventListener('input', () => {
    const query = searchInput.value.toLowerCase();
    document.querySelectorAll('tbody tr').forEach(row => {
      row.style.display = row.textContent.toLowerCase().includes(query) ? '' : 'none';
    });
  });
}

// ── Animate counter numbers ──
function animateCounters() {
  document.querySelectorAll('.stat-val, .stat-num').forEach(el => {
    const text = el.textContent.trim();
    const num = parseInt(text.replace(/[^0-9]/g, ''));
    if (isNaN(num) || num === 0) return;
    let start = 0;
    const duration = 1000;
    const step = Math.ceil(num / (duration / 16));
    const timer = setInterval(() => {
      start = Math.min(start + step, num);
      el.textContent = text.replace(/[0-9]+/, start.toLocaleString());
      if (start >= num) clearInterval(timer);
    }, 16);
  });
}

// ── Job filter auto-submit ──
document.querySelectorAll('#filterForm select').forEach(sel => {
  sel.addEventListener('change', () => document.getElementById('filterForm')?.submit());
});

// ── Character counter for textareas ──
document.querySelectorAll('textarea[maxlength]').forEach(ta => {
  const counter = document.createElement('small');
  counter.style.cssText = 'color:#9ca3af;float:right;margin-top:4px;';
  ta.parentNode.appendChild(counter);
  const update = () => counter.textContent = `${ta.value.length}/${ta.getAttribute('maxlength')}`;
  ta.addEventListener('input', update);
  update();
});

// ── Copy to clipboard ──
function copyToClipboard(text) {
  navigator.clipboard.writeText(text).then(() => showToast('Copied!'));
}

// ── Toast notification ──
function showToast(message, type = 'success') {
  const toast = document.createElement('div');
  toast.className = `flash flash-${type}`;
  toast.textContent = message;
  toast.style.cssText = 'position:fixed;top:80px;right:24px;z-index:9999;min-width:240px;box-shadow:0 8px 32px rgba(0,0,0,.15)';
  document.body.appendChild(toast);
  setTimeout(() => { toast.style.opacity = '0'; setTimeout(() => toast.remove(), 500); }, 3000);
}

// ── Skill chip input ──
function initSkillChips() {
  const input = document.getElementById('skillsInput');
  const container = document.getElementById('skillChips');
  if (!input || !container) return;

  input.addEventListener('keydown', e => {
    if (e.key === ',' || e.key === 'Enter') {
      e.preventDefault();
      const val = input.value.trim().replace(',', '');
      if (val) {
        addSkillChip(val, container);
        input.value = '';
      }
    }
  });
}

function addSkillChip(skill, container) {
  const chip = document.createElement('span');
  chip.className = 'skill-chip';
  chip.style.cursor = 'pointer';
  chip.innerHTML = `${skill} <span style="margin-left:4px;font-size:.7rem">✕</span>`;
  chip.addEventListener('click', () => chip.remove());
  container.appendChild(chip);
}

// ── Form validation feedback ──
document.querySelectorAll('form').forEach(form => {
  form.addEventListener('submit', () => {
    const btn = form.querySelector('[type=submit]');
    if (btn && !btn.disabled) {
      btn.disabled = true;
      btn.textContent = 'Please wait...';
      btn.style.opacity = '.7';
    }
  });
});

// ── Salary range display ──
function updateSalaryDisplay() {
  const min = document.getElementById('salaryMin');
  const max = document.getElementById('salaryMax');
  const display = document.getElementById('salaryDisplay');
  if (!min || !max || !display) return;
  const fmt = n => n ? '₹' + parseInt(n).toLocaleString('en-IN') : '';
  display.textContent = (min.value || max.value) ? `${fmt(min.value)} – ${fmt(max.value)}` : '';
}

document.getElementById('salaryMin')?.addEventListener('input', updateSalaryDisplay);
document.getElementById('salaryMax')?.addEventListener('input', updateSalaryDisplay);
