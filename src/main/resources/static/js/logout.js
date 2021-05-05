"use strict";
let URL = "http://localhost:7000/logout";

window.onload = function() {
    let currUser = JSON.parse(sessionStorage.getItem("currentlyLoggedInUser"));
    if (currUser) {
        fetch(URL, {
            method: 'POST',
            credentials: 'include'
        }).then((data) => {
            return data.json();
        }).then((response) => {
            sessionStorage.clear();
            alert(response.message);
        }).catch((error) => {
            console.log(error);
        });
    } else {
        // If no one is logged in, create sessionStorage value for message and redirect
        let message = "That page cannot be accessed without being logged in";
        sessionStorage.setItem("notLoggedIn", message);
        window.location.href = "index.html";
    }
}
