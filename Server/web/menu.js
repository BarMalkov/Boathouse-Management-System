const a_parent =  document.querySelectorAll(".a_parent");
const dd_menu_a = document.querySelectorAll(".dd_menu_a");
const mangerDataEl = document.querySelectorAll(".managerData");
const SERVLET_ATTRIBUTE = "servletAttribute";
const alertsContainerEl = document.querySelector('.alerts-container');
const logOutButton = document.querySelector('#logOutButton');

logOutButton.addEventListener('click', logOut);

async function logOut(){
    const response = await fetch('login', {method: 'get'});
    if(response.redirected){
        sessionStorage.clear();
        window.location.href = response.url;
    }

}

a_parent.forEach(function(aitem){

    aitem.addEventListener("click", function(){
        a_parent.forEach(function(aitem){
            aitem.classList.remove("active");
        })
        dd_menu_a.forEach(function(dd_menu_item){
            dd_menu_item.classList.remove("active");
        })
        aitem.classList.add("active");
    })
})

dd_menu_a.forEach(function(dd_menu_item){

    dd_menu_item.addEventListener("click", function(){
        sessionStorage.setItem(SERVLET_ATTRIBUTE, this.id);
        dd_menu_a.forEach(function(dd_menu_item){
            dd_menu_item.classList.remove("active");
        })

        dd_menu_item.classList.add("active");
    })
})

async function showDataAccordingToUser(){
    let user = sessionStorage.getItem("loggedInUser");
    if(user !== null) {
        console.log(user);
        await fetch('loggedInUser', {
            method: 'post',
            body: user
        });
    }
    else {
        setLoggedInUser();
    }
    const response  = await fetch('mainMenu', {method: 'get'});
    const responseText = await response.text();
    if(responseText === "true")
        sessionStorage.setItem("isManager", "true");
    else {
        sessionStorage.setItem("isManager", "false");
        mangerDataEl.forEach((element)=>{
            element.style.display = "none";
        });
    }
    getNotifications();
}

async function setLoggedInUser(){
    const response =  await fetch('loggedInUser', {method: 'get'});
    const user = await response.text();
    console.log(user);
    sessionStorage.setItem("loggedInUser", user);
}


async function getNotifications(){
    const response = await fetch('allAlerts', {method: 'get'});
    const data = await response.json();
    updateList(data);
}

async function removeNotifications(){
    await fetch('removeAlerts', {method: 'get'});
}

function updateList(data){
    alertsContainerEl.innerText = '';
    data.forEach((notification)=>{
        const alertEl = createAlertEl(notification);
        alertsContainerEl.append(alertEl);
        alertsContainerEl.innerHTML += "<br>";
    });
    if(sessionStorage.getItem("isManager") === "true"){
        appendManagerData("Add-Alert", "/Hello/newAlert.html");
        appendManagerData("Remove-Alert", "/Hello/showData.html");
        const removeButton = document.querySelector('#Remove-Alert');
        removeButton.addEventListener('click', setAlertServlet);
    }
}

function setAlertServlet(){
    sessionStorage.setItem(SERVLET_ATTRIBUTE, "allAlerts");
}

function appendManagerData(hrefContent, redirect) {
    const listItemEl = document.createElement("li");
    const divEl = document.createElement('div');
    divEl.class = "wrap";
    const addAlertButton = document.createElement('a');
    addAlertButton.href = redirect;
    addAlertButton.textContent = hrefContent;
    addAlertButton.id = hrefContent;
    const content = document.createElement('span');
    divEl.appendChild(addAlertButton);
    listItemEl.appendChild(divEl);
    alertsContainerEl.appendChild(listItemEl);
}



function createAlertEl(notification){
    const listItemEl = document.createElement("li");
    const divEl = document.createElement('div');
    divEl.class="wrap";
    const spanEl = document.createElement('span');
    spanEl.class="text";
    spanEl.innerText = notification["message"];
    divEl.appendChild(spanEl);
    listItemEl.appendChild(divEl);
    return listItemEl;
}

window.addEventListener('load', showDataAccordingToUser)
setInterval(getNotifications, 20000);
setInterval(removeNotifications, 30000);