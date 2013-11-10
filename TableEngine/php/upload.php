<?php
	$userdata = "user.dat";
	$nopassword = "";
	$nooccupacy = "empty";
	$waiting_time_in_min = 1;
	echo "<syncserver>";//introduces the part that is necessary for the java app to read
	
	//standard error messages -> are recognized by java app as well!
	$error_server_not_set_up = "#001 - Server not set up -> Goto /setup.php and enter connection information";
	$error_mysql_connection_failed = "#002 - Server not correctly set up -> Goto /reset.php and redo setup";
	$error_password = "#003 - Wrong password";
	$error_table_not_found = "#004 - Requested table does not exist. Table must be between 1 and number of tables.";
	$message_update_successfull = "1 - Update successfull";
	$error_update_failed = "#005 - Update not successfull";
	$message_upload_successfull = "1 - Upload successfull";
	$error_upload_failed = "#006 - Upload not successfull";
	
  if(move_uploaded_file($_FILES['uploadedfile']['tmp_name'], basename( $_FILES['uploadedfile']['name']))) { //einfach die angebotene Datei richtig benennen
		
		echo $message_upload_successfull;
		
  } else {
  
		echo $error_upload_failed;
	
  }
  echo "</syncserver>";	
  ?>