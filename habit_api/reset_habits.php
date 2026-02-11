<?php
header('Content-Type: application/json');
include 'connect.php';

$json = file_get_contents('php://input');
$data = json_decode($json, true);

if (!isset($data['user_id'])) {
    echo json_encode(["success" => false, "message" => "Missing user_id"]);
    exit;
}

$user_id = (int)$data['user_id'];

$stmt = $conn->prepare("UPDATE habits SET progress = 0 WHERE user_id = ?");
$stmt->bind_param("i", $user_id);

if ($stmt->execute()) {
    echo json_encode(["success" => true]);
} else {
    echo json_encode(["success" => false, "message" => $conn->error]);
}

$stmt->close();
$conn->close();
?>
