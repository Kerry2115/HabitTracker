<?php
include 'connect.php';
if (isset($_GET['user_id'])) {
    $user_id = $_GET['user_id'];
    $stmt = $conn->prepare("SELECT * FROM habits WHERE user_id = ?");
    $stmt->bind_param("i", $user_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $habits = [];
    while ($row = $result->fetch_assoc()) {
        $habits[] = $row;
    }
    echo json_encode($habits);
}
?>