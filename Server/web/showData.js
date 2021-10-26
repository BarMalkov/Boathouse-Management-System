const tableBodyEl = document.querySelector('#theTbody');
const tableHeadersEl = document.querySelector('#theThead');
const editButtonEl = document.querySelector('#edit');
const removeButtonEl = document.querySelector('#remove');
const importFileInput = document.querySelector('#import-Data');
const exportFileButton = document.querySelector('#export-Data');
const importFileButton = document.querySelector('#import-Data-button');
const importExportContainer = document.querySelector('#import-export-container');
const assignReservationButtonEl = document.querySelector('#assign-reservation');
const assignBoatButtonEl = document.querySelector('#assign-boat');
const filterDataEl = document.querySelector('#filterData');
const BOAT_PAGE_HEADERS = ["#", "Serial Number", "Boat Name", "Private", "In Repair",
    "Boat Type", "Has Coxswain", "Costal", "Wide"];
const ACTIVITY_PAGE_HEADERS = ["#", "Activity Name", "Start Time", "End Time", "Boat Type"];
const USER_PAGE_HEADERS = ["#", "ID", "Username", "Email", "Phone number", "Age",
    "Level", "Registration Date", "Expiration Date", "Boat Owner", "Boat ID", "Manager", "Comments"];
const ASSIGNING_PAGE_HEADERS = ["#", "Practice Date", "Start Time", "End Time", "Participants",
    "Boat Name", "Boat Type", "Has Coxswain", "Costal", "Wide"];
const assignObjectToShowParams = [ "practiceDate", "startTime", "endTime", "participants",
    "boatName", "boatType", "hasCoxswain", "isCostal", "isWide"]
const RESERVATION_PAGE_HEADERS = ["#", "Practice Date", "Start Time", "End Time", "Boat Types",
    "Date of Reservation", "Time of Reservation", "Approved", "Participants"];
const NOT_TO_SHOW = ["reservationsID", "password", "reservationOwner", "reservationID"];
const TIME_INPUT_ATTRIBUTES = ["startTime", "endTime", "timeOfReservation"];
const DATE_INPUT_ATTRIBUTES = ["practiceDate", "dateOfReservation", "dateOfRegistration", "dateOfExpiration"];


let allObjectsArray;
let currentServlet;
let uriToRedirect;
let attributeForSession;
let reservationToAssign = new Object();
let fileText;
let importExportServlet;
let nextWeekDates = [];


function setObjectType(){
    currentServlet = sessionStorage.getItem("servletAttribute")
}

async function setCurrentRedirect(currentServlet){
    switch (currentServlet){
        case "activity":
            uriToRedirect = "editActivity";
            attributeForSession = "activityToEdit";
            importExportContainer.style.display = "block";
            break;
        case "user":
            uriToRedirect = "editUser";
            attributeForSession = "userToEdit";
            importExportContainer.style.display = "block";
            break;
        case "boat":
            uriToRedirect = "editBoat";
            attributeForSession = "boatToEdit";
            importExportContainer.style.display = "block";
            break;
        case "myReservations":
            uriToRedirect = "editReservation";
            attributeForSession = "reservationToEdit";
            let user = sessionStorage.getItem("loggedInUser");
            await fetch('loggedInUser', {
                method: 'post',
                body: user
            });
            break;
        case "allAssigns":
            uriToRedirect = "editAssigning";
            attributeForSession = "assigningToEdit";
            filterDataEl.style.display = "block";
            break;
        case "allAlerts":
            editButtonEl.style.display = "none";
    }
}

function parseTimeInput(data){
    const hour = data["hour"].toString();
    let minutes = data["minute"].toString();
    if( minutes.length === 1){
        minutes = "0" + minutes;
    }

    return hour + ":" + minutes;
}

function parseDateInput(data) {
    const year = data["year"].toString();
    const month = data["month"].toString();
    const day = data["day"].toString();

    return day + "/" + month + "/" + year;
}

function parseValueToShow(value, data){
    if (data === true)
        return "Yes";
    else if(data === false)
        return "No";
    else if (TIME_INPUT_ATTRIBUTES.includes(value))
         return parseTimeInput(data);
    else if (DATE_INPUT_ATTRIBUTES.includes(value))
        return parseDateInput(data);
    else
        return data;
}

function updateHeadersRow(arrayOfStrings){
    let rowEl = tableHeadersEl.appendChild(document.createElement("tr"));
    arrayOfStrings.forEach((str)=>{
        let cellEl = rowEl.appendChild(document.createElement("th"));
        let cellText = document.createTextNode(str);
        cellEl.appendChild(cellText);
        rowEl.appendChild(cellEl);
    });
}

function updateTableHeaders(){
    switch (currentServlet){
        case "boat":
            updateHeadersRow(BOAT_PAGE_HEADERS);
            break;
        case "activity":
            updateHeadersRow(ACTIVITY_PAGE_HEADERS);
            break;
        case "user":
            updateHeadersRow(USER_PAGE_HEADERS);
            break;
        case "myReservations":
            updateHeadersRow(RESERVATION_PAGE_HEADERS);
            break;
        case "allReservations":
            updateHeadersRow(RESERVATION_PAGE_HEADERS);
            break;
        case "allAssigns":
            updateHeadersRow(ASSIGNING_PAGE_HEADERS);
            break;
        case "newAssigning":
            updateHeadersRow(BOAT_PAGE_HEADERS);
            break;
    }
}

function updateTableRows(objectsJson){
    updateTableHeaders();
    objectsJson.forEach((objectElement) => {
        let rowEl = tableBodyEl.appendChild(document.createElement("tr"));
        const radioButtonEl = document.createElement('input');
        radioButtonEl.type = "radio";
        radioButtonEl.name = "objectElement";
        let cellEl = rowEl.appendChild(document.createElement("td"));
        cellEl.appendChild(radioButtonEl)
        for(let value in objectElement) {
            if(NOT_TO_SHOW.includes(value)) continue;
            let cellEl = rowEl.appendChild(document.createElement("td"));
            let cellText;
            if(value === "boatTypes" || value === "participants"){
                cellText = document.createElement("label");
                let data = objectElement[value];
                data.forEach((boatType) =>{
                    cellText.innerHTML+= boatType.toString();
                    cellText.innerHTML+= '<br>'
                })
            }else{
                cellText = document.createTextNode(parseValueToShow(value, objectElement[value]));
            }
            cellEl.appendChild(cellText);
        }
    });
}

function createAssignsToShow(responseJson){
    let arrayOfObjects = new Array();
    responseJson.forEach((assigning) =>{
        let assignedBoat = assigning["assignedBoats"];
        let assignedReservation = assigning["reservation"];
        let objectToShow = new Object();
        assignObjectToShowParams.forEach((parameter)=>{
            if(parameter in assignedBoat){
                objectToShow[parameter] = assignedBoat[parameter];
            }
            else{
                objectToShow[parameter] = assignedReservation[parameter];
            }
        })
        arrayOfObjects.push(objectToShow);
    })
    return arrayOfObjects;
}

function findIndexOfSelectedObject(){
    let indexToEdit = -1;
    const allRadioButtons = document.querySelectorAll('input[type="radio"]');
    const radioButtonsArray = Array.prototype.slice.call(allRadioButtons);
    console.log(radioButtonsArray);
    radioButtonsArray.forEach((radioButton)=>{
        if(radioButton.checked){
            indexToEdit = radioButtonsArray.indexOf(radioButton);
        }
    });
    if(indexToEdit === -1){
        window.alert("You must choose an object from the table first")
    }
    return indexToEdit;
}

async function updateBoatsForReservation() {
    removeButtonEl.style.display = "none";
    tableHeadersEl.innerHTML = "";
    tableBodyEl.innerHTML = "";
    assignReservationButtonEl.style.display = "none";
    assignBoatButtonEl.style.display = "block";
    try {
        const response = await fetch("boatsToAssign", {method: 'get'});
        const responseJson = await response.json();
        allObjectsArray = responseJson;
        updateTableRows(allObjectsArray);
    }catch (e) {
        alert("No boats to show for this reservation");
        window.location.replace("/Hello/showData.html");
    }
}

async function assignReservation() {
    let chosenReservation = allObjectsArray[findIndexOfSelectedObject()];
    if(chosenReservation["isApproved"]){
        alert("You can't assign an approved reservation");
        return;
    }
    for (let value in chosenReservation) {
        reservationToAssign[value] = chosenReservation[value];
    }
    try {
        const  response = await fetch("checkAutoAssign", {
            method: 'post',
            body: JSON.stringify(reservationToAssign)
        });
        const responseText = await response.text();
        if(responseText === "true"){
            alert("Your reservation was assigned with one of the participants " +
                "private boat");
            window.location.replace("/Hello/showData.html");
            return;
        }

    }catch (e) {}
    try {
        await fetch("boatsToAssign", {
            method: 'post',
            body: JSON.stringify(reservationToAssign)
        });
        updateBoatsForReservation();
    }catch (e) {
        alert("No boats to show for this reservation")
    }
}

async function assignBoat(){
    let assignedBoats = allObjectsArray[findIndexOfSelectedObject()];
    let reservation = reservationToAssign;
    let newAssigning = {reservation, assignedBoats};
    const response = await fetch("newAssigning", {
        method: 'post',
        body: JSON.stringify(newAssigning)});
    const responseText = await response.text();
    alert(responseText);
    location.reload();
}

function setUpAllReservationConfig(){
    editButtonEl.style.display = "none";
    assignReservationButtonEl.addEventListener('click', assignReservation);
    assignBoatButtonEl.addEventListener('click', assignBoat)
}

function createNextWeekDates() {
    let datesStrArr=[];
    for (let i = 0; i < 7; i++) {
        let currentDate = new Date;
        currentDate.setDate(new Date().getDate() + (i+1));
        nextWeekDates.push(currentDate)
        datesStrArr.push(currentDate.toDateString());
    }
    return datesStrArr;
}

function createDataFilter(){
    let datesArray = createNextWeekDates();
    for (let i = 0; i < datesArray.length; i++) {
        let option = document.createElement('option');
        option.value = datesArray[i];
        option.appendChild(document.createTextNode(datesArray[i]));
        filterDataEl.appendChild(option);
    }
}

async function getObjects() {
    setObjectType();
    assignReservationButtonEl.style.display = "none"
    assignBoatButtonEl.style.display = "none";
    importExportContainer.style.display = "none";
    filterDataEl.style.display = "none";
    setCurrentRedirect(currentServlet);
    if (currentServlet === "newAssigning") {
        currentServlet = "allReservations";
        assignReservationButtonEl.style.display = "block";
        removeButtonEl.style.display = "none";
    }
    try {
        const response = await fetch(currentServlet, {method: 'get'});
        const responseJson = await response.json();
        allObjectsArray = responseJson;
        if(currentServlet === "allAssigns"){
            editButtonEl.style.display = "none";
            updateTableRows(createAssignsToShow(responseJson));
            createDataFilter();
        }else if(currentServlet ==="allReservations"){
            setUpAllReservationConfig();
            createDataFilter();
            filterDataEl.style.display = "block";
        }
        if(currentServlet !== "allAssigns")
            updateTableRows(responseJson);

    }catch (e) {
        alert("No data to show");
    }
}

async function removeObject(objectToRemove){
    const response  = await fetch(currentServlet, {
        method: 'post',
        body: JSON.stringify(objectToRemove)});

    const result = await response.text();
    alert(result);
    location.reload();
}

function removeSelectedObject(){
    const selectedObjectIndex = findIndexOfSelectedObject();
    if(selectedObjectIndex !== -1) {
        const objectToRemove = allObjectsArray[selectedObjectIndex];
        if(currentServlet === "allReservations" || currentServlet === "myReservations") {
            if(objectToRemove["isApproved"] === true) {
                alert("you can't delete an approved reservation");
                return;
            }
        }
        removeObject(objectToRemove);
    }
}

function editSelectedObject(){
    const selectedObjectIndex = findIndexOfSelectedObject();
    if(selectedObjectIndex !== -1) {
        const userToEdit = allObjectsArray[selectedObjectIndex];
        if (currentServlet === "allReservations" || currentServlet === "myReservations"){
            if(userToEdit["isApproved"]) {
                alert("you can't edit an approved reservation");
                return;
            }
        }
        sessionStorage.setItem(attributeForSession, JSON.stringify(userToEdit));
        window.location.replace("/Hello/editData.html");
    }
}

async function readUserFile(event){
    const fileReader = new FileReader();
    fileReader.onload = getFileFromUser;
    fileReader.readAsText(event.target.files[0]);
}

function getFileFromUser(event){
    fileText = event.target.result;
    console.log(fileText);
}

function getImportExportServlet() {
    switch (currentServlet) {
        case "boat":
            importExportServlet = 'importExportBoats';
            break;
        case "user":
            importExportServlet = 'importExportUsers';
            break;
        case "activity":
            importExportServlet = 'importExportActivities';
    }
}

async function importXmlToServer(){
    getImportExportServlet();
    if(fileText === "")
        alert("The file is empty");
    else {
        const response = await fetch(importExportServlet, {
            method: 'post',
            body: fileText
        });

        const result = await response.text();
        alert(result);
        location.reload();
    }
}

function downloadFileText(fileName, fileContent){

    let downloadEl = document.createElement('a');
    downloadEl.setAttribute('href', 'data:text/xml;charset=utf-8,' +
    encodeURIComponent(fileContent));
    downloadEl.setAttribute('download', fileName);
    downloadEl.style.display = "none";
    document.body.appendChild(downloadEl);
    downloadEl.click();
    document.body.removeChild(downloadEl);
}

async function exportXmlToUser(){
    getImportExportServlet();
    const response  = await fetch(importExportServlet, {method: 'get'});
    const fileContent = await response.text();
    const fileName = currentServlet + "data.xml";
    console.log(fileContent);
    downloadFileText(fileName, fileContent);
}

function updateNextWeekReservation(){
    let temporaryObjectArray = [];
    for(let i = 0; i < allObjectsArray.length ; i++){
        let obj = allObjectsArray[i];
        if(currentServlet === "allAssigns"){
            obj = obj["reservation"];
        }
        let jsDateObj = new Date();
        let dateObj = obj["practiceDate"];
        let month = dateObj["month"];
        let day = dateObj["day"];
        if(month > (jsDateObj.getMonth()+1)){
            temporaryObjectArray.push(allObjectsArray[i]);
        }else if(day >= jsDateObj.getDate()){
            temporaryObjectArray.push(allObjectsArray[i]);
        }
    }
    updateTableRows(temporaryObjectArray);
}

function updateLastWeekReservation(){
    let temporaryObjectArray = [];
    for(let i = 0; i < allObjectsArray.length ; i++){
        let obj = allObjectsArray[i];
        if(currentServlet === "allAssigns"){
            obj = obj["reservation"];
        }
        let jsDateObj = new Date();
        let dateObj = obj["practiceDate"];
        let month = dateObj["month"];
        let day = dateObj["day"];
        if(month < (jsDateObj.getMonth() + 1)){
            temporaryObjectArray.push(allObjectsArray[i]);
        }else if(day < jsDateObj.getDate()){
            temporaryObjectArray.push(allObjectsArray[i]);
        }
    }
    updateTableRows(temporaryObjectArray);
}

function filterDataToShow(event){
    let index = event.target.selectedIndex;
    tableHeadersEl.innerHTML = "";
    tableBodyEl.innerHTML = "";
    if(index === 0){
        updateTableRows(allObjectsArray);
        return;
    }
    else if(index === 1){
        updateNextWeekReservation();
        return;
    }
    else if(index === 2){
        updateLastWeekReservation();
        return;
    }
    let temporaryObjectArray = [];
    index = index - 3;
    let jsDateObj = nextWeekDates[index];
    for(let i = 0; i < allObjectsArray.length ; i++){
        let obj = allObjectsArray[i];
        if(currentServlet === "allAssigns"){
            obj = obj["reservation"];
        }
        let dateObj = obj["practiceDate"];
        let month = dateObj["month"];
        let day = dateObj["day"];
        if(day === jsDateObj.getDate() && month === (jsDateObj.getMonth()+1)){
            temporaryObjectArray.push(allObjectsArray[i]);
        }
    }
    updateTableRows(temporaryObjectArray);
}

removeButtonEl.addEventListener("click", removeSelectedObject);
editButtonEl.addEventListener("click", editSelectedObject);
window.addEventListener('load', getObjects);
importFileInput.addEventListener('change', readUserFile, false);
filterDataEl.addEventListener('change', filterDataToShow)
exportFileButton.addEventListener('click', exportXmlToUser);
importFileButton.addEventListener('click', importXmlToServer)