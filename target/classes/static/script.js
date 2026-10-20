const API = '/api/cars';

const form = document.getElementById('carForm');
const carId = document.getElementById('carId');
const brand = document.getElementById('brand');
const releaseYear = document.getElementById('releaseYear');
const registrationDate = document.getElementById('registrationDate');
const ownerName = document.getElementById('ownerName');
const saveBtn = document.getElementById('saveBtn');
const cancelBtn = document.getElementById('cancelBtn');
const errorBox = document.getElementById('error');
const searchInput = document.getElementById('search');
const orderSelect = document.getElementById('order');
const tableBody = document.getElementById('tableBody');
const counter = document.getElementById('counter');

let cars = [];

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

function formatDate(str) {
    const parts = str.split('-');
    return parts[2] + '.' + parts[1] + '.' + parts[0];
}

async function loadCars() {
    const params = new URLSearchParams({
        search: searchInput.value,
        order: orderSelect.value
    });
    const response = await fetch(API + '?' + params);
    cars = await response.json();
    renderTable();
}

function renderTable() {
    tableBody.innerHTML = '';
    cars.forEach(car => {
        const row = document.createElement('tr');
        row.innerHTML =
            '<td>' + car.id + '</td>' +
            '<td>' + escapeHtml(car.brand) + '</td>' +
            '<td>' + car.releaseYear + '</td>' +
            '<td>' + formatDate(car.registrationDate) + '</td>' +
            '<td>' + escapeHtml(car.ownerName) + '</td>' +
            '<td>' +
            '<button onclick="editCar(' + car.id + ')">Изменить</button> ' +
            '<button onclick="deleteCar(' + car.id + ')">Удалить</button>' +
            '</td>';
        tableBody.appendChild(row);
    });
    counter.textContent = cars.length;
}

async function loadHistogram() {
    const response = await fetch(API + '/histogram');
    const data = await response.json();
    const max = Math.max(1, ...data.map(d => d.count));

    const histogram = document.getElementById('histogram');
    histogram.innerHTML = '';
    data.forEach(d => {
        const column = document.createElement('div');
        column.className = 'column';
        const height = d.count / max * 130;
        column.innerHTML =
            '<span>' + (d.count > 0 ? d.count : '') + '</span>' +
            '<div class="bar" style="height:' + height + 'px"></div>' +
            '<span class="day">' + d.date.substring(5).split('-').reverse().join('.') + '</span>';
        histogram.appendChild(column);
    });
}

form.addEventListener('submit', async event => {
    event.preventDefault();
    errorBox.textContent = '';

    const car = {
        brand: brand.value,
        releaseYear: Number(releaseYear.value),
        registrationDate: registrationDate.value,
        ownerName: ownerName.value
    };

    const id = carId.value;
    const response = await fetch(id ? API + '/' + id : API, {
        method: id ? 'PUT' : 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(car)
    });

    if (!response.ok) {
        errorBox.textContent = await response.text();
        return;
    }

    resetForm();
    loadCars();
    loadHistogram();
});

function editCar(id) {
    const car = cars.find(c => c.id === id);
    carId.value = car.id;
    brand.value = car.brand;
    releaseYear.value = car.releaseYear;
    registrationDate.value = car.registrationDate;
    ownerName.value = car.ownerName;
    saveBtn.textContent = 'Сохранить';
    cancelBtn.classList.remove('hidden');
}

async function deleteCar(id) {
    if (!confirm('Удалить запись?')) {
        return;
    }
    await fetch(API + '/' + id, {method: 'DELETE'});
    loadCars();
    loadHistogram();
}

function resetForm() {
    form.reset();
    carId.value = '';
    saveBtn.textContent = 'Добавить';
    cancelBtn.classList.add('hidden');
    errorBox.textContent = '';
}

cancelBtn.addEventListener('click', resetForm);
searchInput.addEventListener('input', loadCars);
orderSelect.addEventListener('change', loadCars);

loadCars();
loadHistogram();
