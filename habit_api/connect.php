<?php
// connect.php
$servername = "sql308.host.adellya.my.id";
$username = "Ilya_41356627_habit_db"; 
$password = "5obohnq5";     
$dbname = "habit_db"; // UPEWNIJ SIĘ, ŻE NAZWA PASUJE DO TWOJEJ BAZY

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    // W przypadku błędu na serwerze, zwróć JSON
    http_response_code(500); 
    echo json_encode(array("success" => false, "message" => "Błąd połączenia z bazą danych."));
    exit;
}
// Ustawienie nagłówka na JSON
header('Content-Type: application/json');
?>