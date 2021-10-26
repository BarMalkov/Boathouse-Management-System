const newObjectForm = document.querySelector('#formElement');
const hasPrivateInputEl = document.querySelector('#has-Private-Boat');
const timeSlotsContainer = document.querySelector('#reservation-time-slot-container');
const timesInputContainer = document.querySelector('#reservation-start-end-container');
const boatTypesContainer = document.querySelector('#boat-type-container');
const practiceDateContainer = document.querySelector('#practice-date-container');
const submitButton = document.querySelector('#submit-button');
const addParticipantsContainer = document.querySelector("#add-participants-container");
const reservationPracticeDateEl = document.querySelector('#practiceDate');
const startTimeEl = document.querySelector('#startTime');
const endTimeEl = document.querySelector('#endTime');
const participantListEL = document.querySelector("#participantsListContainer");
const participantInputEl = document.querySelector('#participant');
const addParticipantsButton = document.querySelector('#addParticipantButton');
const reservationTimeSlotEl = document.querySelector('#timeSlot');
const reservationPageEl = document.querySelector('#reservationPage');
let objectToAdd;
let timeSlots;
let datesArray = [];

function createNextWeekDates() {
    let datesStrArr=[];
    for (let i = 0; i < 7; i++) {
        let currentDate = new Date;
        currentDate.setDate(new Date().getDate() + (i+1));
        datesArray.push(currentDate)
        datesStrArr.push(currentDate.toDateString());
    }
    console.log(datesArray);
    return datesStrArr;
}

function parseToDate(inputElement){
    const dateData = inputElement.value.split("-");
    objectToAdd[inputElement.name]["year"] = parseInt(dateData[0]);
    objectToAdd[inputElement.name]["month"] = parseInt(dateData[1]);
    objectToAdd[inputElement.name]["day"] = parseInt(dateData[2]);
}

function parseToTime(inputElement){
    const timeData = inputElement.value.split(":");
    objectToAdd[inputElement.name]["hour"] = parseInt(timeData[0]);
    objectToAdd[inputElement.name]["minute"] = parseInt(timeData[1]);
}

function parseToBoolean(inputElement){
    objectToAdd[inputElement.name] = inputElement.id.split("_")[0] === "yes";
}

function handleInput(event){
    if(this.type === "text" && this.id !== "participant"){
        objectToAdd[this.name] = this.value;
    }else if(this.type === "date") {
        parseToDate(this);
    }else if(this.type === "time"){
        parseToTime(this);
    }else if(this.type === "radio"){
        parseToBoolean(this);
        if(this.id === "yes_private" && this.checked)
            hasPrivateInputEl.style.display = "block";
        else if(this.id === "no_private" && this.checked)
            hasPrivateInputEl.style.display = "none";
    }else if(this.tagName === "SELECT" && this.id === "boatTypes"){
        let result = [];
        let len = this.options.length;
        let optionEl;
        for(let i = 0; i < len; i++){
            optionEl = this.options[i];
            if(optionEl.selected)
                result.push(optionEl.value);
        }
        objectToAdd[this.name] = result;
    }else if(this.tagName === "SELECT" && this.id === "timeSlot"){
        let times = timeSlots[this.selectedIndex - 1];
        objectToAdd["startTime"] = times["startTime"];
        objectToAdd["endTime"] = times["endTime"];
    }else if(this.tagName === "SELECT" && this.id === "practiceDate") {
        let dateToParse = datesArray[this.selectedIndex - 1];
        objectToAdd[this.name]["year"] = dateToParse.getFullYear();
        objectToAdd[this.name]["month"] = dateToParse.getMonth() + 1;
        objectToAdd[this.name]["day"] = dateToParse.getDate();
    } else if(this.tagName === "SELECT"){
        objectToAdd[this.name] = event.target.value;
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

function updateSelectInput(input){
    for (let i = 0; i < input.length; i++) {
        let option = document.createElement('option');
        option.value = input[i];
        option.appendChild(document.createTextNode(input[i]));
        reservationPracticeDateEl.appendChild(option);
    }
}

function createTimeInput(data){
    const hour = data["hour"].toString();
    let minutes = data["minute"].toString();
    if( minutes.length === 1){
        minutes = "0" + minutes;
    }

    return hour + ":" + minutes;
}

function parseTimeSlot(timeSlot){
    let result;
    let startTime = createTimeInput(timeSlot["startTime"]);
    let endTime = createTimeInput(timeSlot["endTime"]);
    result =
        timeSlot["activityName"] + " - time slot: " + startTime  + "-" + endTime;
    return result;
}

function updateTimeSlots(jsonObject){
    console.log(jsonObject);
    let i = 0;
    jsonObject.forEach((timeSlot) =>{
        let option = document.createElement('option');
        option.value = timeSlot;
        option.id = i.toString()
        i++;
        console.log(option.id);
        option.appendChild(document.createTextNode(parseTimeSlot(timeSlot)));
        reservationTimeSlotEl.appendChild(option);
    });
}

async function getTimeSlots(){
    try {
        const response = await fetch('activity', {method: 'get'});
        const responseJson = await response.json();
        timeSlots = responseJson;
        console.log(timeSlots);
        updateTimeSlots(responseJson);
        startTimeEl.required = false;
        endTimeEl.required = false;
        timesInputContainer.style.display = "none";
    }catch (e){
        timeSlotsContainer.style.display = "none";
        reservationTimeSlotEl.required = false;
    }
}

async function addParticipantToReservation(event) {
    event.preventDefault();
    if (participantInputEl.value === "")
        alert("you must enter user email first");
    else{
        objectToAdd["participants"].push(participantInputEl.value);
        const response  = await fetch(newObjectForm.action, {
            method: 'post',
            body: JSON.stringify(objectToAdd)});

        const result = await response.text();
        alert(result);
        console.log(objectToAdd);
        if( result === "reservation added successfully"){
            let listItem = document.createElement('li');
            listItem.innerText = participantInputEl.value;
            participantListEL.appendChild(listItem);
        }else{
            let indexToRemove = objectToAdd["participants"].indexOf(participantInputEl.value);
            objectToAdd["participants"].splice(indexToRemove, 1);
        }
    }
}

async function handleReservationInput(){
    addParticipantsContainer.style.display = "none";
    participantInputEl.style.display = "none";
    updateSelectInput(createNextWeekDates());
    getTimeSlots();
    let user = sessionStorage.getItem("loggedInUser");
    const response =  await fetch('loggedInUser', {
        method: 'post',
        body: user
    });
    addParticipantsButton.addEventListener('click', addParticipantToReservation)
}

async function setCurrentUser(){
    let user = sessionStorage.getItem("loggedInUser");
    await fetch('loggedInUser', {
        method: 'post',
        body: user
    });
}

async function handleNewObjectForm(){
    if(reservationPageEl !== null)
        await handleReservationInput();
    if(newObjectForm.action === "updatePersonalInfo")
        await setCurrentUser();
    if(hasPrivateInputEl !== null) hasPrivateInputEl.style.display = "none";
    const response  = await fetch(newObjectForm.action, {method: 'get'});
    objectToAdd = await response.json();
    console.log(objectToAdd);
    setInputHandling();
}

function initParticipantForm(){
    addParticipantsContainer.style.display = "block";
    participantInputEl.style.display = "block";
    timeSlotsContainer.style.display = "none";
    timesInputContainer.style.display = "none";
    boatTypesContainer.style.display = "none";
    practiceDateContainer.style.display = "none";
    submitButton.style.display = "none"
}

async function handleSubmit(event){
    event.preventDefault();
    const response  = await fetch(newObjectForm.action, {
        method: 'post',
        body: JSON.stringify(objectToAdd)});

    const result = await response.text();
    alert(result);
    if( result === "reservation added successfully")
        initParticipantForm();

    console.log(objectToAdd);
}

window.addEventListener('load', handleNewObjectForm);
newObjectForm.addEventListener('submit', handleSubmit);