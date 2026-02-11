<?php
header('Content-Type: application/json');
include 'connect.php';

$json = file_get_contents('php://input');
$data = json_decode($json, true);

if (!isset($data['id']) || !isset($data['progress'])) {
    echo json_encode(["success" => false, "message" => "Missing id/progress"]);
    exit;
}

$id = (int)$data['id'];
$progress = (float)$data['progress'];

// IMPORTANT: progress in DB is FLOAT, so send 1.0 or 0.0
$stmt = $conn->prepare("UPDATE habits SET progress = ? WHERE id = ?");
$stmt->bind_param("di", $progress, $id);

if ($stmt->execute()) {
    echo json_encode(["success" => true]);
} else {
    echo json_encode(["success" => false, "message" => $conn->error]);
}
$stmt->close();
$conn->close();
?>
