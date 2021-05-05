"use strict";
let URL = "http://localhost:7000/reimbursements";


window.onload = function() {
    let currUser = JSON.parse(sessionStorage.getItem("currentlyLoggedInUser"));
    if (currUser) {
        let button = document.querySelector('#submitReimb');
        button.addEventListener('click', function(event) {
            event.preventDefault();
            createReimb();
        });
    } else {
        // If no one is logged in, create sessionStorage value for message and redirect
        let message = "That page cannot be accessed without being logged in";
        sessionStorage.setItem("notLoggedIn", message);
        window.location.href = "index.html";
    }
}

function createReimb() {
    let fd = new FormData();

    fd.append("amount", document.getElementById("amountInput").value);
    fd.append("description", document.getElementById("descriptionInput").value);
    let file = document.getElementById("receiptInput").files[0];
    if (file) {
        fd.append("receipt", document.getElementById("receiptInput").files[0], document.getElementById("receiptInput").files[0].name);
    } else {
        fd.append("receipt", "");
    }    
    fd.append("type", document.getElementById("typeInput").value);

    for (let item of fd.entries()) {
        console.log("form data ", item);
    }
    fetch(URL, {
        method: 'POST',
        credentials: 'include',
        body: fd
    }).then((data) => {
        return data.json();
    }).then((response) => {
        console.log(response);
        if (response.message) {
            alert(response.message);
        } else {
            // redirect to landing page
            window.location.href = "landing.html";
        }
    }).catch((error) => {
        console.log(error);
    });
}