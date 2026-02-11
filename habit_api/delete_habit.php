<?php
include 'connect.php';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    $habit_id = $data['id'];

    $stmt = $conn->prepare("DELETE FROM habits WHERE id = ?");
    $stmt->bind_param("i", $habit_id);

    if ($stmt->execute()) {
        echo json_encode(["success" => true]);
    } else {
        echo json_encode(["success" => false]);
    }
}
?>