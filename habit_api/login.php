<?php
include 'connect.php';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    
    $username = $data['username'] ?? '';
    $password = $data['password'] ?? '';

    $stmt = $conn->prepare("SELECT id, password FROM users WHERE username = ?");
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows === 1) {
        $user = $result->fetch_assoc();
        $hashed_password = $user['password'];

        // Weryfikacja zahaszowanego hasła
        if (password_verify($password, $hashed_password)) {
            http_response_code(200); // OK
            echo json_encode(array("success" => true, "message" => "Logowanie udane.", "user_id" => $user['id']));
        } else {
            http_response_code(401); // Unauthorized
            echo json_encode(array("success" => false, "message" => "Nieprawidłowe hasło."));
        }
    } else {
        http_response_code(404); // Not Found
        echo json_encode(array("success" => false, "message" => "Użytkownik nie istnieje."));
    }
    $stmt->close();
} else {
    http_response_code(405);
    echo json_encode(array("success" => false, "message" => "Nieprawidłowa metoda żądania."));
}
$conn->close();
?>