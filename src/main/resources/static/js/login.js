"use strict";
let URL = "http://localhost:7000/login";
let button = document.querySelector('#submitButton');
button.addEventListener('click', function(event) {
    event.preventDefault();
    loginUser();
});

window.onload = function() {
    let msg = sessionStorage.getItem("notLoggedIn");
    if (msg) {
        alert(msg);
    }
}

function loginUser() {
    let loginData = {
        username: document.getElementById("usernameInput").value,
        password: document.getElementById("passwordInput").value
    }
    console.log(loginData);
    fetch(URL, {
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(loginData)
    }).then((data) => {
        if (data.status == 400) {
            alert("That username/password is incorrect");
        } else {
            return data.json();
        }
    }).then((response) => {
        sessionStorage.setItem("currentlyLoggedInUser", JSON.stringify(response));
        // redirect to landing page
        window.location.href = "landing.html";
    }).catch((error) => {
        console.log(error);
    })
}