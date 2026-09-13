const API_BASE = '';

const form        = document.getElementById('search-form');
const input       = document.getElementById('city-input');
const formError   = document.getElementById('form-error');

const stateEmpty   = document.getElementById('state-empty');
const stateLoading = document.getElementById('state-loading');
const stateError   = document.getElementById('state-error');
const errorMessage = document.getElementById('error-message');
const retryBtn     = document.getElementById('retry-btn');
const result       = document.getElementById('result');

const locationName = document.getElementById('location-name');
const locationMeta = document.getElementById('location-meta');
const currentTemp  = document.getElementById('current-temp');
const currentWind  = document.getElementById('current-wind');
const currentTime  = document.getElementById('current-time');

const daytabs   = document.getElementById('daytabs');
const chartSvg  = document.getElementById('chart-svg');
const hourlyBody = document.getElementById('hourly-body');

let lastWeather = null;
let lastCity = '';
let activeDay = 0;

function show(section){
  [stateEmpty, stateLoading, stateError, result].forEach(s => s.hidden = (s !== section));
}

function formatHour(isoString){
  const d = new Date(isoString);
  return d.toLocaleTimeString([], { hour: 'numeric', minute: '2-digit' });
}

function formatDayLabel(isoString){
  const d = new Date(isoString);
  return d.toLocaleDateString([], { weekday: 'short', month: 'short', day: 'numeric' });
}

form.addEventListener('submit', (e) => {
  e.preventDefault();
  const city = input.value.trim();
  formError.hidden = true;

  if (!city){
    formError.textContent = 'Enter a city name first.';
    formError.hidden = false;
    return;
  }

  fetchWeather(city);
});

retryBtn.addEventListener('click', () => {
  if (lastCity) fetchWeather(lastCity);
});

daytabs.addEventListener('click', (e) => {
  const btn = e.target.closest('.daytabs__btn');
  if (!btn) return;
  activeDay = Number(btn.dataset.day);
  [...daytabs.querySelectorAll('.daytabs__btn')].forEach(b =>
      b.classList.toggle('is-active', b === btn)
  );
  renderDay(activeDay);
});

async function fetchWeather(city){
  lastCity = city;
  show(stateLoading);

  try{
    const res = await fetch(`${API_BASE}/search`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ city })
    });

    const body = await res.json().catch(() => null);

    if (!res.ok){
      const message = (body && body.error) ? body.error : `The service returned an error (${res.status}).`;
      throw new Error(message);
    }

    lastWeather = body;
    activeDay = 0;
    [...daytabs.querySelectorAll('.daytabs__btn')].forEach((b, i) =>
        b.classList.toggle('is-active', i === 0)
    );
    renderResult(city, body);
    show(result);

  } catch(err){
    errorMessage.textContent = err.message || 'Could not reach the weather service. Check that the backend is running.';
    show(stateError);
  }
}

function renderResult(city, data){
  locationName.textContent = city;
  locationMeta.textContent = `${data.latitude.toFixed(2)}°, ${data.longitude.toFixed(2)}° · ${data.timezone}`;

  currentTemp.textContent = `${Math.round(data.current.temperature_2m)}°`;
  currentWind.textContent = `${Math.round(data.current.wind_speed_10m)} km/h`;
  currentTime.textContent = formatHour(data.current.time);

  renderDay(0);
}

function daySlice(data, dayIndex){
  const start = dayIndex * 24;
  const end = start + 24;
  const time = data.hourly.time.slice(start, end);
  const temp = data.hourly.temperature_2m.slice(start, end);
  const humidity = data.hourly.relative_humidity_2m.slice(start, end);
  const wind = data.hourly.wind_speed_10m.slice(start, end);
  return { time, temp, humidity, wind };
}

function renderDay(dayIndex){
  if (!lastWeather) return;
  const data = lastWeather;
  const slice = daySlice(data, dayIndex);

  if (slice.time.length === 0) return;

  const label = formatDayLabel(slice.time[0]);
  document.getElementById('chart-title').textContent = `Temperature on ${label}, °C`;

  const nowIndex = slice.time.indexOf(data.current.time);

  drawChart(slice.temp, nowIndex);
  fillTable(slice, nowIndex, data.hourly_units);
}

function drawChart(temps, nowIndex){
  const width = 720, height = 220;
  const padding = { top: 20, right: 16, bottom: 10, left: 16 };
  const min = Math.min(...temps);
  const max = Math.max(...temps);
  const range = (max - min) || 1;

  const points = temps.map((t, i) => {
    const x = padding.left + (i / (temps.length - 1)) * (width - padding.left - padding.right);
    const y = padding.top + (1 - (t - min) / range) * (height - padding.top - padding.bottom);
    return { x, y, t };
  });

  const linePath = points.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x.toFixed(1)} ${p.y.toFixed(1)}`).join(' ');

  let markers = '';
  [0, Math.floor(points.length / 2), points.length - 1].forEach(i => {
    const p = points[i];
    markers += `<text x="${p.x.toFixed(1)}" y="${height - 2}" font-size="11" style="fill:var(--ink-soft); font-family:var(--font-mono);" text-anchor="${i === 0 ? 'start' : i === points.length - 1 ? 'end' : 'middle'}">${Math.round(p.t)}°</text>`;
  });

  let nowMarker = '';
  if (nowIndex >= 0 && nowIndex < points.length){
    const p = points[nowIndex];
    nowMarker = `
      <line x1="${p.x.toFixed(1)}" y1="${padding.top}" x2="${p.x.toFixed(1)}" y2="${height - padding.bottom}" style="stroke:var(--line);" stroke-width="1" stroke-dasharray="3 3" />
      <circle cx="${p.x.toFixed(1)}" cy="${p.y.toFixed(1)}" r="5" style="fill:var(--rust);" />
    `;
  }

  chartSvg.innerHTML = `
    <path d="${linePath}" fill="none" style="stroke:var(--teal);" stroke-width="2" />
    ${nowMarker}
    ${markers}
  `;
}

function fillTable(slice, nowIndex, units){
  hourlyBody.innerHTML = '';
  slice.time.forEach((t, i) => {
    const tr = document.createElement('tr');
    if (i === nowIndex) tr.classList.add('is-now');
    tr.innerHTML = `
      <td>${formatHour(t)}</td>
      <td>${Math.round(slice.temp[i])}${units.temperature_2m}</td>
      <td>${Math.round(slice.humidity[i])}${units.relative_humidity_2m}</td>
      <td>${Math.round(slice.wind[i])} ${units.wind_speed_10m}</td>
    `;
    hourlyBody.appendChild(tr);
  });
}