<?php
include 'connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    
    $username = $data['username'] ?? '';
    $password = $data['password'] ?? '';

    // Hashowanie hasła! Zawsze haszuj hasła!
    $hashed_password = password_hash($password, PASSWORD_DEFAULT);

    $stmt = $conn->prepare("INSERT INTO users (username, password) VALUES (?, ?)");
    $stmt->bind_param("ss", $username, $hashed_password);

    if ($stmt->execute()) {
        http_response_code(201); // Created
        echo json_encode(array("success" => true, "message" => "Rejestracja udana."));
    } else {
        if ($conn->errno == 1062) {
             http_response_code(409); // Conflict
             echo json_encode(array("success" => false, "message" => "Nazwa użytkownika jest już zajęta."));
        } else {
             http_response_code(500);
             echo json_encode(array("success" => false, "message" => "Błąd rejestracji."));
        }
    }
    $stmt->close();
} else {
    http_response_code(405); // Method Not Allowed
    echo json_encode(array("success" => false, "message" => "Nieprawidłowa metoda żądania."));
}
$conn->close();
?>