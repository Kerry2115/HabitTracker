<?php
include 'connect.php';
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $data = json_decode(file_get_contents("php://input"), true);
    $user_id = $data['user_id'];
    $name = $data['name'];

    $stmt = $conn->prepare("INSERT INTO habits (user_id, name) VALUES (?, ?)");
    $stmt->bind_param("is", $user_id, $name);

    if ($stmt->execute()) {
        echo json_encode(["success" => true, "id" => $stmt->insert_id]);
    } else {
        echo json_encode(["success" => false]);
    }
}
?>