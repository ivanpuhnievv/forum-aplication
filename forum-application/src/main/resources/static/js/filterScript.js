 function filterProfiles() {
    const input = document.getElementById('searchInput').value.toLowerCase();
    const filter = document.getElementById('filterSelect').value;
    const profiles = document.querySelectorAll('.profile-card');

    profiles.forEach(profile => {
    const username = profile.querySelector('h3').innerText.toLowerCase();
    const socialLinks = profile.querySelectorAll('.icon');
    let hasSocialLink = false;

    socialLinks.forEach(link => {
    if (filter === '' || link.classList.contains(`icon-${filter}`)) {
    hasSocialLink = true;
}
});

    if (username.includes(input) && (filter === '' || hasSocialLink)) {
    profile.parentElement.style.display = 'block';
} else {
    profile.parentElement.style.display = 'none';
}
});
}

    function sortProfiles() {
    const sortBy = document.getElementById('sortSelect').value;
    const profilesContainer = document.querySelector('.row.justify-content-between');
    const profiles = Array.from(profilesContainer.querySelectorAll('.col-12'));

    profiles.sort((a, b) => {
    let aValue, bValue;

    if (sortBy === 'username') {
    aValue = a.querySelector('h3').innerText.toLowerCase();
    bValue = b.querySelector('h3').innerText.toLowerCase();
} else if (sortBy === 'firstName') {
    aValue = a.querySelector('span').innerText.toLowerCase();
    bValue = b.querySelector('span').innerText.toLowerCase();
}

    if (aValue < bValue) return -1;
    if (aValue > bValue) return 1;
    return 0;
});

    profiles.forEach(profile => profilesContainer.appendChild(profile));
}