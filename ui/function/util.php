<?php
require_once '../includes/config.php';

function get_request($URL)
{
    //Initialize cURL.
    $ch = curl_init($URL);

    //Set CURLOPT_RETURNTRANSFER so that the content is returned as a variable.
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

    //Set CURLOPT_FOLLOWLOCATION to true to follow redirects.
    curl_setopt($ch, CURLOPT_FOLLOWLOCATION, true);

    //Execute the request.
    $data = curl_exec($ch);

    // Error Handling
    validate($ch, $data);


    var_dump(curl_getinfo($ch));

    //Close the cURL handle.
    curl_close($ch);

    //Print the data out onto the page.
    return json_decode($data);
}

function getEmployeeDetails($id)
{
    /** @var string $EMPLOYEES_API_URL */
    $URL = $_SERVER['HTTP_HOST'] . $EMPLOYEES_API_URL . "/" . $id;
    return get_request($URL);
}

function validate($ch, $json_data)
{
    if (curl_errno($ch))
        throw new Exception(curl_error($ch));
    else if (curl_getinfo($ch)["http_code"] === "404")
        throw new Exception("Page not found");
    else if (curl_getinfo($ch)["http_code"] !== "200") {
        $data = json_decode($json_data);
        throw new Exception(curl_getinfo($ch)["http_code"] . " - " . $data['message']);
    }
}