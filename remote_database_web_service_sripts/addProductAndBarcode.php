 <?php
 
	$name = str_replace("%20", " ", $_GET['p_name']);
	$category = str_replace("%20", " ", $_GET['p_category']);
	$description = str_replace("%20", " ", $_GET['p_descript']);
	$price = str_replace("%20", " ", $_GET['p_price']);
	$barcode = str_replace("%20", " ", $_GET['b_barcode']);


	$servername = "zesloka.tk";
	$username = "user";
	$password = "parolee";
	$dbname = "piens_un_maize";

	
	$conn = new mysqli($servername, $username, $password, $dbname);
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	}

	
	$sql = "	INSERT INTO product (p_name, p_category, p_descript , p_price)
				VALUES ('$name', '$category', '$description' , $price);";

	if ($conn->query($sql) === TRUE) {
		echo $name . " | " . $category . " | " . $description . " | " . $price . " :: succsessfully dadded";
	} else {
		echo "Error: " . $sql. " " . $conn->error;
	}
	
	$sql = "	INSERT INTO barcode (b_barcode, b_productID)
				VALUES('$barcode', LAST_INSERT_ID());";
				
	if ($conn->query($sql) === TRUE) {
		echo $name . " | " . $category . " | " . $description . " | " . $price . " :: succsessfully dadded";
	} else {
		echo "Error: " . $sql. " " . $conn->error;
	}

	$conn->close();
	
?> 