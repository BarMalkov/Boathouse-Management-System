const tableBodyEl = document.querySelector('#theTbody');
const tableHeadersEl = document.querySelector('#theThead');
const formEl = document.querySelector('#formElement');
const BOAT_PAGE_HEADERS = ["Serial Number", "Boat Name", "Private",
    "In Repair", "Boat Type", "Has Coxswain", "Costal", "Wide"];
const ACTIVITY_PAGE_HEADERS = ["Activity Name", "Start Time", "End Time", "Boat Type"];
const USER_PAGE_HEADERS = ["ID", "Username", "Email", "Phone number", "Age",
    "Level", "Registration Date", "Expiration Date", "Boat Owner", "Boat ID", "Manager", "Comments"];
const RESERVATION_PAGE_HEADERS = ["Practice Date", "Start Time", "End Time", "Boat Types",
    "Date of Reservation", "Time of Reservation", "Approved", "Participants"];
const BOAT_TYPES = ["Single", "Dual_One_Paddle", "Dual_One_Paddle_Coxed", "Dual_Two_Paddle",
    "Dual_Two_Paddle_Coxed", "Quad_One_Paddle", "Quad_One_Paddle_Coxed",
    "Quad_Two_Paddle", "Quad_Two_Paddle_Coxed", "Eight_One_Paddle", "Eight_Two_Paddle"];
const LEVELS = ["BEGINNER", "INTERMEDIATE", "EXPERT"];
const TIME_INPUT_ATTRIBUTES = ["startTime", "endTime", "timeOfReservation"];
const DATE_INPUT_ATTRIBUTES = ["dateOfReservation", "dateOfRegistration", "dateOfExpiration"];
const NOT_TO_EDIT = ["userName", "email", "password", "phoneNumber", "age", "isApproved",
    "dateOfReservation", "timeOfReservation", "reservationOwner", "reservation", "reservationsID", "hasCoxswain",
"isCostal", "isWide"];
const SELECTION_OBJECTS = ["participants", "practiceDate", "boatType", "boatTypes", "level"];
const NOT_TO_SHOW = ["reservationsID", "password", "reservationOwner", "reservationID"];
const WEEKDAYS = 7;
let objectToEdit;
let objectBeforeEdit;
let objectTypeStr;
let servletForNewRequest;
let participantInputEl;
let datesObjArray=[];
let datesArray = [];
function setCurrentServlet(){

    switch(sessionStorage.getItem("servletAttribute")){
        case "activity":
            objectTypeStr = "activity";
            servletForNewRequest = "editActivity";
            break;
        case "boat":
            objectTypeStr = "boat";
            servletForNewRequest = "editBoat";
            break;
        case "user":
            objectTypeStr = "user";
            servletForNewRequest = "editUser";
            break;
        case "myReservations":
            objectTypeStr = "reservation";
            servletForNewRequest = "editReservation";
    }
}

function getObjectToEdit(){
    objectToEdit = JSON.parse(sessionStorage.getItem(objectTypeStr + "ToEdit"));
    objectBeforeEdit = {};
    let innerElToEdit;
    let innerElBeforeEdit;
    for (let value in objectToEdit){
        if(TIME_INPUT_ATTRIBUTES.includes(value) || DATE_INPUT_ATTRIBUTES.includes(value)
        || value === "boatTypes" || value === "participants" || value === "practiceDate"){
            if(TIME_INPUT_ATTRIBUTES.includes(value) || DATE_INPUT_ATTRIBUTES.includes(value)) {
                objectBeforeEdit[value] = {};
            }
            else
                objectBeforeEdit[value] = [];
            innerElToEdit = objectToEdit[value];
            console.log(innerElToEdit);
            if(value === "boatTypes" || value === "participants"){
                innerElBeforeEdit = new Array();
            }else {
                innerElBeforeEdit = Object.create(innerElToEdit);
            }
            for(let innerValue in innerElToEdit){
                innerElBeforeEdit[innerValue] = innerElToEdit[innerValue];
            }
            objectBeforeEdit[value] = innerElBeforeEdit;
            console.log(objectBeforeEdit[value]);
        }else {
            objectBeforeEdit[value] = objectToEdit[value];
        }
    }
    console.log(objectBeforeEdit);

}

function createNextWeekDates() {
    for (let i = 0; i < WEEKDAYS; i++) {
        let currentDate = new Date;
        currentDate.setDate(new Date().getDate() + (i + 1));
        datesObjArray.push(currentDate);
        datesArray.push(currentDate.toDateString());
    }
    console.log(datesArray);
    return datesArray;
}

async function addParticipantToReservation(event) {
    event.preventDefault();
    if (participantInputEl.value === "")
        alert("you must enter user email first");
    else if (objectToEdit["participants"].includes(participantInputEl.value)) {
        alert("user already belong to reservation");
    }
    else{
        objectToEdit["participants"].push(participantInputEl.value);
        let selectEl = document.querySelector('#participants');
        console.log(selectEl);
        let optionItem = document.createElement('option');
        optionItem.value = participantInputEl.value;
        optionItem.innerText = participantInputEl.value;
        selectEl.appendChild(optionItem);
    }
}

function removeParticipantFromReservation(event){
    event.preventDefault();
    let selectEl = document.querySelector('#participants');
    let len = selectEl.options.length;
    let optionEl;
    for(let i = 0; i < len; i++){
        optionEl = selectEl.options[i];
        if(optionEl.selected) {
            optionEl.remove(i);
            objectToEdit["participants"].splice(i, 1);
        }
    }
}

function addButtonsToParticipantsEdit(cellEl){
    let removeButtonContainer = document.createElement("div");
    removeButtonContainer.class = "button-container";
    let removeButton = document.createElement("button");
    removeButton.id = 'removeUsers';
    removeButton.textContent = "Remove";
    removeButtonContainer.appendChild(removeButton);
    let addButtonContainer = document.createElement("div");
    addButtonContainer.class = "button-container";
    participantInputEl = document.createElement("input");
    participantInputEl.name = "participant";
    participantInputEl.id = "participant";
    let addingButton = document.createElement("button");
    addingButton.id = 'addUser';
    addingButton.textContent = "Add";
    addButtonContainer.appendChild(addingButton);
    cellEl.appendChild(removeButtonContainer);
    cellEl.appendChild(participantInputEl);
    cellEl.appendChild(addButtonContainer);
    addingButton.addEventListener('click', addParticipantToReservation);
    removeButton.addEventListener('click', removeParticipantFromReservation);
}

function createSelectionElement(arrayOfObjects, cellEl, value){
    let selectionElement = document.createElement('select');
    if(value === "boatTypes" || value === "participants"){
        selectionElement.setAttribute('multiple', '');
        selectionElement.size = 11;
    }
    selectionElement.name = value;
    selectionElement.id = value;
    console.log(selectionElement.name);
    if(value !== "boatTypes" && value !== "participants") {
        let first = document.createElement('option');
        first.label = "please Select";
        first.value = "";
        selectionElement.appendChild(first);
    }
    let optionHolder = document.createDocumentFragment();
    for (let i = 0; i < arrayOfObjects.length; i++) {
        let option = document.createElement('option');
        option.value = arrayOfObjects[i];
        option.appendChild(document.createTextNode(arrayOfObjects[i]));
        optionHolder.appendChild(option);
    }
    selectionElement.appendChild(optionHolder);
    cellEl.appendChild(selectionElement);
    if(value === "participants"){
        addButtonsToParticipantsEdit(cellEl, value);
    }
}

function createSelectionInput(cellEl, value){
    let arrayOfObjects;
    switch(value){
        case "practiceDate":
            arrayOfObjects = createNextWeekDates();
            break;
        case "level":
            arrayOfObjects = LEVELS;
            break;
        case "boatType":
            arrayOfObjects = BOAT_TYPES;
            break;
        case "boatTypes":
            arrayOfObjects = BOAT_TYPES;
            break;
        case "participants":
            arrayOfObjects = objectToEdit["participants"];
            break;
        default:
            arrayOfObjects = null;
    }

    createSelectionElement(arrayOfObjects, cellEl, value);
}

function createBooleanInput(cellEl, value){
    let cellInput = document.createElement("input");
    let cellInput2 = document.createElement("input");
    cellInput.type = "radio";
    cellInput.id = "yes";
    cellInput2.type = "radio";
    cellInput2.id = "no";
    cellInput.name = value;
    cellInput2.name = value;
    let yesOption = document.createElement("label");
    yesOption.innerHTML = "Yes";
    let noOption = document.createElement("label");
    noOption.innerHTML = "No";
    cellEl.appendChild(cellInput);
    cellEl.appendChild(yesOption);
    cellEl.innerHTML += "<br>"
    cellEl.appendChild(cellInput2);
    cellEl.appendChild(noOption);
}

function createTimeInput(cellEl, value){
    let timeInput = document.createElement("input");
    timeInput.type = "time";
    timeInput.name = value;
    timeInput.id = value;
    timeInput.value = "13:30";
    cellEl.appendChild(timeInput);
}

function createDateInput(cellEl, value){
    let timeInput = document.createElement("input");
    timeInput.type = "date";
    timeInput.name = value;
    timeInput.id = value;
    timeInput.value = "2020-07-22";
    cellEl.appendChild(timeInput);
}

function showTimeInput(data){
    const hour = data["hour"].toString();
    let minutes = data["minute"].toString();
    if( minutes.length === 1){
        minutes = "0" + minutes;
    }

    return hour + ":" + minutes;
}

function showDateInput(data) {
    const year = data["year"].toString();
    const month = data["month"].toString();
    const day = data["day"].toString();

    return day + "/" + month + "/" + year;
}

function createTextInput(cellEl, value){
    let cellInput = document.createElement("input");
    cellInput.type = "text";
    cellInput.name = value;
    cellInput.value = objectToEdit[value];
    cellEl.appendChild(cellInput);
}

function appendCellAccordingToInputType(value, cellEl, objectToEdit){
    if (objectToEdit[value] === true || objectToEdit[value] === false) {
        createBooleanInput(cellEl, value);
    }
    else if (TIME_INPUT_ATTRIBUTES.includes(value)) {
        createTimeInput(cellEl, value);

    }else if (DATE_INPUT_ATTRIBUTES.includes(value)) {
        createDateInput(cellEl, value);

    }else if (SELECTION_OBJECTS.includes(value)) {
        createSelectionInput(cellEl, value);
    }
    else{
        createTextInput(cellEl, value);
    }
}

function parseNotToEdit(value, data){
    if (data === true)
        return "Yes";
    else if(data === false)
        return "No";
    else if (TIME_INPUT_ATTRIBUTES.includes(value))
        return showTimeInput(data);
    else if (DATE_INPUT_ATTRIBUTES.includes(value))
        return showDateInput(data);
    else
        return data;
}

function updateCellData(rowEl, objectToEdit){
    for(let value in objectToEdit) {
        if(NOT_TO_SHOW.includes(value))
            continue;
        let cellEl = rowEl.appendChild(document.createElement("td"));
        if(NOT_TO_EDIT.includes(value)){
            let cellData = document.createElement("label");
            cellData.innerText = parseNotToEdit(value, objectToEdit[value]);
            cellEl.appendChild(cellData);
        }else {
            appendCellAccordingToInputType(value, cellEl, objectToEdit);
        }
    }
}

function updateDataRow(objectToEdit){
    let rowEl = tableBodyEl.appendChild(document.createElement("tr"));
    updateCellData(rowEl, objectToEdit);
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
    switch (objectTypeStr){
        case "boat":
            updateHeadersRow(BOAT_PAGE_HEADERS);
            break;
        case "activity":
            updateHeadersRow(ACTIVITY_PAGE_HEADERS);
            break;
        case "user":
            updateHeadersRow(USER_PAGE_HEADERS);
            break;
        case "reservation":
            updateHeadersRow(RESERVATION_PAGE_HEADERS);
            break;
    }
}

function parseToDate(inputElement){
    const dateData = inputElement.value.split("-");
    objectToEdit[inputElement.name]["year"] = parseInt(dateData[0]);
    objectToEdit[inputElement.name]["month"] = parseInt(dateData[1]);
    objectToEdit[inputElement.name]["day"] = parseInt(dateData[2]);
}

function parseToTime(inputElement){
    const timeData = inputElement.value.split(":");
    objectToEdit[inputElement.name]["hour"] = parseInt(timeData[0]);
    objectToEdit[inputElement.name]["minute"] = parseInt(timeData[1]);
}

function parseToBoolean(inputElement){
    objectToEdit[inputElement.name] = inputElement.id === "yes";
}

function handleInput(event){
    if(this.type === "text" && this.id !== "participant"){
        if(this.value === "") {
            alert("you must enter text");
            this.value = objectToEdit[this.name];
        }
        else
            objectToEdit[this.name] = this.value;
    }else if(this.type === "date"){
        parseToDate(this);
    }else if(this.type === "time"){
        parseToTime(this);
    }else if(this.type === "radio"){
        parseToBoolean(this);
    }else if(this.tagName === "SELECT" && this.id === "boatTypes"){
        let result = [];
        let len = this.options.length;
        let optionEl;
        for(let i = 0; i < len; i++){
            optionEl = this.options[i];
            if(optionEl.selected)
                result.push(optionEl.value);
        }
        objectToEdit[this.name] = result;
    }else if(this.tagName === "SELECT" && this.id !== "participants"){
        if(this.name === "practiceDate"){
            let dateToParse = datesObjArray[this.selectedIndex - 1];
            objectToEdit[this.name]["month"] = dateToParse.getMonth() + 1;
            objectToEdit[this.name]["year"] = dateToParse.getFullYear();
            objectToEdit[this.name]["day"] = dateToParse.getDate();
        }else {
            objectToEdit[this.name] = event.target.value;
        }
    }
}

function setInputHandling(){
    let allInputElements = document.getElementsByTagName('input');
    for (let i = 0; i < allInputElements.length; i++) {
        allInputElements[i].addEventListener('change', handleInput);
    }
    let allSelectElements = document.getElementsByTagName('select');
    console.log(allSelectElements);
    for (let i = 0; i < allSelectElements.length; i++) {
        allSelectElements[i].addEventListener('change', handleInput);
    }
}

async function sendFormToServlet(event){
    event.preventDefault();
    let objectArray = [objectBeforeEdit, objectToEdit];
    console.log(objectArray);
    console.log(servletForNewRequest);
    const response  = await fetch(servletForNewRequest, {
        method: 'post',
        body: JSON.stringify(objectArray)});

    const result = await response.text();
    alert(result);
    window.location.replace("/Hello/showData.html");
}

function updateTable(){
    setCurrentServlet();
    getObjectToEdit();
    updateTableHeaders();
    updateDataRow(objectToEdit);
    setInputHandling();
}

formEl.addEventListener('submit', sendFormToServlet);
window.addEventListener('load', updateTable);
