<?php

session_start();

/**
 *  If active section is detected then 
 * this variables will be available globally 
 * for the developer to access the user's data
 */
if (isset($_SESSION['userId'])) {
    $isLogged = true;
    $userId = $_SESSION['userId'];
    $firstName = $_SESSION['firstName'];
    $lastName = $_SESSION['lastName'];
    $email = $_SESSION['email'];
    $amazon = $_SESSION['amazon_turk_id'];
} else {
    $isLogged = false;
    $userId = NULL;
    $firstName = NULL;
    $lastName = NULL;
    $email = NULL;
    $amazon = NULL;
}