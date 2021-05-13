"use strict";
let URL = "http://localhost:7000/signup";
let button = document.querySelector('#signupButton');
button.addEventListener('click', function(event) {
    event.preventDefault();
    signupUser();
});

function signupUser() {
    let signupData = {
        username: document.getElementById("usernameInput").value,
        password: document.getElementById("passwordInput").value,
        firstName: document.getElementById("firstNameInput").value,
        lastName: document.getElementById("lastNameInput").value,
        email: document.getElementById("emailInput").value,
        role: document.getElementById("roleInput").value
    }
    fetch(URL, {
        method: 'POST',
        credentials: 'include',
        body: JSON.stringify(signupData)
    }).then((data) => {
        return data.json();
    }).then((response) => {
        // If a message is present, its an error
        if (response.message) {
            alert(response.message);
        } else {
            sessionStorage.setItem("currentlyLoggedInUser", JSON.stringify(response));
            // redirect to landing page
            window.location.href = "landing.html";
        }
    }).catch((error) => {
        console.log(error);
    })
}