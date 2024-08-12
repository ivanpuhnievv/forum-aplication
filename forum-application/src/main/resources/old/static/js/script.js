// function openMenu() {
//   const el = document.querySelector('.menu');
//   if (el.classList.contains('close-menu')) {
//     el.classList.remove('close-menu');
//     el.classList.add('open-menu');
//   } else if (el.classList.contains('open-menu')) {
//     el.classList.add('close-menu');
//     el.classList.remove('open-menu');
//   } else {
//     el.classList.add('open-menu');
//   }
// }

(function () {
    // document.querySelector('.menu').style.bottom = '-10%';
    const el = document.querySelector('.menu');
    el.style.bottom = '-10%';
    el.style.opacity = '0';
})();

function toggleMenu() {
    const el = document.querySelector('.menu');
    if (el.style.bottom == '-10%') {
        el.style.bottom = '100px';
        el.style.opacity = '1';
        document.querySelector('.toggle__menu-open').style.opacity = '0';
        document.querySelector('.toggle__menu-close').style.opacity = '1';
    } else {
        el.style.bottom = '-10%';
        el.style.opacity = '0';
        document.querySelector('.toggle__menu-open').style.opacity = '1';
        document.querySelector('.toggle__menu-close').style.opacity = '0';
    }
}

function addActiveClass(context) {
    const navItems = document.querySelectorAll('.menu-icon');
    const navItem = document.getElementById(context);
    navItems.forEach((nav) => {
        nav.classList.remove('nav-active');
    });
    navItem.classList.add('nav-active');
}

function changeMenu(context) {
    const title = document.querySelector('.title');

    switch (context) {
        case 'home':
            title.innerText = 'Home';
            toggleMenu();
            addActiveClass(context);
            break;
        case 'about':
            window.location.href = '/auth/login';
            title.innerText = 'About me';
            toggleMenu();
            addActiveClass(context);
            break;
        case 'skills':
            title.innerText = 'Skills';
            toggleMenu();
            addActiveClass(context);
            break;
        case 'projects':
            title.innerText = 'Projects';
            toggleMenu();
            addActiveClass(context);
            break;
        case 'contact':
            title.innerText = 'Contact';
            toggleMenu();
            addActiveClass(context);
            break;
        default:
            break;
    }
}

const navItems = document.querySelectorAll('.menu-icon');

const toolTips = document.querySelectorAll('.tooltip');

function addToolTip(key) {
    removeTooltips();
    toolTips.forEach((tooltip) => {
        if (tooltip.getAttribute('data-key') == key) {
            tooltip.style.opacity = '1';
        }
    });
}

function removeTooltips() {
    toolTips.forEach((tooltip) => {
        tooltip.style.opacity = '0';
    });
}

navItems.forEach((item) => {
    item.addEventListener('mouseover', () => {
        addToolTip(item.getAttribute('id'));
    });
});

navItems.forEach((item) => {
    item.addEventListener('mouseleave', () => {
        removeTooltips();
    });
});
