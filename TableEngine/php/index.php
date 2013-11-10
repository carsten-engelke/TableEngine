  <html>
  <head>
<meta http-equiv="content-type" content="text/html; UTF-8">
  <title>Server v.1.1 (13-04-21)</title>
  </head>
  <body>
  <h2>Server</h2>
  <textarea name="output" rows=50 cols=120><?php error_reporting(0);

	// uses a table in mysql with the following purposes:
	// 
	// aTS =last access time stamp
	// sTS = last set time stamp
	// NAME = name of table
	// PW = password of table
	// ID = id (of application using this table)
	// CONTENT = content of table (the actual synchronized stuff)
	//
	//			|	Table_index					|	Table_1		|	Table_2		|	...			|	Table_max	|
	//			-------------------------------------------------------------------------
	//	Row 1	|	aTS-PW-ID (for Table_1)		|	aTS-PW-ID	|	aTS-PW-ID	|	aTS-PW-ID	|	aTS-PW-ID	|
	//	Row 2	|	aTS-PW-ID (for Table_2)		|	NAME		|	NAME		|	NAME		|	NAME		|
	//	Row 3	|	aTS-PW-ID (for Table_3)		|	CONTENT		|	CONTENT		|	CONTENT		|	CONTENT		|
	//	Row 4	|	aTS-PW-ID (for Table_3)		|	sTS			|	sTS			|	sTS			|	sTS			|
	//	Row ...	|	aTS-PW-ID (for Table_...)	|
	// 	Row max	|	aTS-PW-ID (for Table_max)	|
	//
	$userdata = "user.dat";
	$nopassword = "";
	$nooccupacy = "empty";
	$waiting_time_in_min = 1;
	echo "\n<syncserver>";//introduces the part that is necessary for the java app to read

	//standard error messages -> are recognized by java app as well!
	$error_server_not_set_up = "#001 - Server not set up -> Goto /setup.php and enter connection information";
	$error_mysql_connection_failed = "#002 - Server not correctly set up -> Goto /reset.php and redo setup";
	$error_id_password = "#003 - Password ("  . $_POST["pw"] . ") or ID (" . $_POST["id"] . ") do not match";
	$error_table_not_found = "#004 - Requested table does not exist. Table must be between 1 and number of tables:" . $_POST["table"];
	$message_update_successfull = "1 - Update successfull";
	$error_update_failed = "#005 - Update not successfull";
	$message_upload_successfull = "1 - Upload successfull";
	$error_upload_failed = "#006 - Upload not successfull";
	
	if (file_exists($userdata)) {	//server was set up correctly -> read out access data to SQL
	
		//read the datafile with mysql-user-data
  		$sqlserverfile = fopen($userdata, "r");	 
  		$txt = fread($sqlserverfile,filesize($userdata));
		$pos = explode(";", $txt);
  		$sqlserver = $pos[0];
  		$sqldb = $pos[1];
  		$sqlname = $pos[2];
  		$sqlpw = $pos[3];
		$maxtables = $pos[4]; // info about the server -> how many tables exist?
  		fclose($sqlserverfile);
		
		 //setup mysql-connection and open database
  		$mysqlconnection = mysql_connect($sqlserver, $sqlname, $sqlpw);
  		mysql_select_db($sqldb,$mysqlconnection);
		
		//update server at first using Table_index
		for($i = 1; $i <= $maxtables; $i++) {
			
			$result = mysql_query("SELECT * FROM Table_index
				WHERE objID=" . $i, $mysqlconnection);
			$indexstr = mysql_fetch_object($result)->Object;
			if (($indexstr == $nooccupacy) || ($indexstr == "")) { //if the table is empty or not initiated -> initiate and make empty
				
				mysql_query("TRUNCATE TABLE Table_" . $i, $mysqlconnection);
				mysql_query("INSERT INTO Table_" . $i . "
						VALUE (NULL, \"" . $nooccupacy . "\")", $mysqlconnection);
				mysql_query("INSERT INTO Table_" . $i . "
						VALUE (NULL, \"" . $nooccupacy . "\")", $mysqlconnection);
				mysql_query("INSERT INTO Table_" . $i . "
						VALUE (NULL, NULL)", $mysqlconnection);
				mysql_query("INSERT INTO Table_" . $i . "
						VALUE (NULL, \"" . $nooccupacy . "\")", $mysqlconnection);
				mysql_query("UPDATE Table_index
							SET Object=\"" . $nooccupacy . "\"
							WHERE objID=" . $i, $mysqlconnection);
							
			} else { //table is in use -> check for expiry
			
				$pos = explode("-", $indexstr);
				$timestamp = $pos[0]; //firstpart is timestamp from last access
				if ((time() - $timestamp) > ($waiting_time_in_min * 60)) { // table expired -> make empty
					
					mysql_query("TRUNCATE TABLE Table_" . $i, $mysqlconnection);
					mysql_query("INSERT INTO Table_" . $i . "
						VALUE (NULL, \"" . $nooccupacy . "\")", $mysqlconnection);
					mysql_query("INSERT INTO Table_" . $i . "
						VALUE (NULL, \"" . $nooccupacy . "\")", $mysqlconnection);
					mysql_query("INSERT INTO Table_" . $i . "
						VALUE (NULL, NULL)", $mysqlconnection);
					mysql_query("INSERT INTO Table_" . $i . "
						VALUE (NULL, \"" . $nooccupacy . "\")", $mysqlconnection);
					mysql_query("UPDATE Table_index
						SET Object=\"" . $nooccupacy . "\"
						WHERE objID=" . $i, $mysqlconnection);
				}
			}
		}
		
		if ($_POST["action"] == "info") { //if action="info" -> get overview over total database
			
			$infostr = "<info max=" . $maxtables . ">";
			for($i = 1; $i <= $maxtables; $i++) {
				
				$result = mysql_query("SELECT * FROM Table_" . $i . " WHERE objID=1", $mysqlconnection); // SQL command for data request -> objID=1 contains information about last access and password
				if ($result) { // if result is not empty due to errors
					
					$row = mysql_fetch_object($result);
					$tablestr = $row->Object; // result contains two pieces of information -> ID (so to speak the row) and OBJECT (the string-information)
					$pos = explode("-", $tablestr);
					$timestamp = $pos[0]; //firstpart is timestamp from last access
					$password = $pos[1];
					$id = $pos[2];
					$result = mysql_query("SELECT * FROM Table_" . $i ."
						WHERE objID=2", $mysqlconnection);
					$tablename = mysql_fetch_object($result)->Object;//read table name
					if ($password == "" || $tablestr == $nooccupacy) {
					
						$infostr = $infostr . "<slot=" . $i ." pw=false id=" . $id . ">" . $tablename . "</slot>"; // output
						
					} else {
					
						$infostr = $infostr .  "<slot=" . $i ." pw=true id=" . $id . ">" . $tablename . "</slot>"; // output
					}
				} else {
				
					echo $error_mysql_connection_failed;
					break;
				}
			}
			echo $infostr . "</info>";
		}
		
		if ($_POST["action"] == "table") { // if action="table" -> show whole server content.
		
			$adminstr = "<table rows=" . $maxtables . " colums=4>";
		    for($col = 1; $col <= $maxtables; $col++) {
			
				$adminstr =  $adminstr . "<col>";
				for ($row = 1; $row <= 4; $row++) {
				
					$adminstr =  $adminstr . "<row>";
					$result = mysql_query("SELECT * FROM Table_" . $col . " WHERE objID=" . $row, $mysqlconnection); // SQL command for data request -> objID=1 contains information about last access and password
					if ($result) {
						
						$adminstr =  $adminstr . $result->Object;
					} else {
						
						$adminstr =  $adminstr . "ERROR in COL " . $col . " ROW " . $row;
					}
					$adminstr =  $adminstr . "</row>";
				}
				$adminstr =  $adminstr . "</col>";
			}
			$adminstr = $adminstr . "</table>";
			echo $adminstr;
		}
		
  		if ($_POST["action"] == "get") { //if action="get" -> get data from mysql-database
  			
			if ($_POST["table"] > 0 && $_POST["table"] <= $maxtables) { // check if the requested table exists
				
				// selects table -> so command has to be e.g. "action=get&table=23" -> table may be number 1 - maxtables
				$result = mysql_query("SELECT * FROM Table_" . $_POST["table"] ."
					WHERE objID=1", $mysqlconnection); // SQL command for data request -> objID=1 contains information about last access and password
				if ($result) { // if result is not empty due to errors
					
					$row = mysql_fetch_object($result);
					$tablestr = $row->Object; // result contains two pieces of information -> ID (so to speak the row) and OBJECT (the string-information)
					list($timestamp, $password, $id) = explode("-", $tablestr);
					if ((($_POST["id"] == $id) || ($id == "")) && (($_POST["pw"] == $password) || ($password == ""))) { //check if the given password and id are identical to the posted or the table was empty
					
						$newtimestamp = time();
						
						// read out now the last send timestamp (that is the last call of "action=set"
						$result = mysql_query("SELECT * FROM Table_" . $_POST["table"] ."
							WHERE objID=4", $mysqlconnection); // get info about last update (row 4)
						if ($result) {
						
							echo mysql_fetch_object($result)->Object;
						
						} else {
						
							echo $error_mysql_connection_failed;
						}
						// read out now the info saved in the specified 
						echo "-";
						$result = mysql_query("SELECT * FROM Table_" . $_POST["table"] ."
							WHERE objID=3", $mysqlconnection);
						if ($result) {
						
							echo mysql_fetch_object($result)->Object; //Put out string in Object
							
						} else {
						
							echo $error_mysql_connection_failed;
							
						}
						// while($row = mysql_fetch_object($result)) {
							// echo $row->Object; //again string is saved in OBJECT
						// }
						
						// now save the access in first row -> to prevent this table to be cleared out when it is called
						mysql_query("UPDATE Table_" . $_POST["table"] . "
							SET Object=\"" . $newtimestamp . "-" . $password . "-" . $id . "\"
							WHERE objID=1", $mysqlconnection);
						mysql_query("UPDATE Table_index
							SET Object=\"" . $newtimestamp . "-" . $password . "-" . $id . "\"
							WHERE objID=" . $_POST["table"], $mysqlconnection);
							
					} else {
					
						echo $error_id_password;
					}
					
				} else {
				
					echo $mysql_connection_failed;
				}
				
			} else {
			
				echo $error_table_not_found;
			}
		}
		
  		if ($_POST["action"] == "set") { //if action="set" -> set a certain data-set to (value="content") in mysql-database
  			
			if ($_POST["table"] > 0 && $_POST["table"] <= $maxtables) { // check if the requested table exists
				
				// selects table -> so command has to be e.g. " action=set&table=23&value="my Stored Content" " -> table may be in the range of 1 to maxtables
				$result = mysql_query("SELECT * FROM Table_" . $_POST["table"] ."
					WHERE objID=1", $mysqlconnection); // SQL command for data request -> objID=1 contains information about last access and password or String "empty"
				if ($result) { // if result is not empty due to errors
					
					$row = mysql_fetch_object($result);
					$tablestr = $row->Object; // result contains two pieces of information -> ID (so to speak the row) and OBJECT (the string-information)
					list($timestamp, $password, $id) = explode("-", $tablestr);
					$result = mysql_query("SELECT * FROM Table_" . $_POST["table"] ."
						WHERE objID=2", $mysqlconnection);
					$tablename = mysql_fetch_object($result)->Object; //read table name
					if ((($_POST["id"] == $id) || ($id == "")) && (($_POST["pw"] == $password) || ($password == ""))) { //check if the given password and id are identical to the posted or the table was empty
						
						$newvalue = substr($_POST["value"], strpos($_POST["value"], "<syncserver>") + 12, strpos($_POST["value"], "</syncserver>") - strpos($_POST["value"], "<syncserver>") + 12);
						
						$newtimestamp = time();
						
						$result = mysql_query("UPDATE Table_" . $_POST["table"] . "
							SET Object=\"" . $newvalue . "\"
							WHERE objID=3", $mysqlconnection);
						if ($result) {
						
							echo $newtimestamp . "-" . $message_update_successfull;
							
						} else {
						
							echo $error_update_failed . mysql_error();
							
						}
						// consider renaming the table
						if ($tablename == $nooccupacy) { // if table was empty -> create new name
							
							$generatedname = "SyncServer No. " . $newtimestamp;
						
						} else {
							
							$generatedname = $tablename; // if table was in use -> use same name
						
						}
						if ($_POST["name"] != ""){
						
							$generatedname = $_POST["name"]; // since user wants to rename -> do so
						}
						// now save the access in first row -> to prevent this table to be cleared out when "update.php" is called up
						mysql_query("UPDATE Table_" . $_POST["table"] . "
							SET Object=\"" . $newtimestamp . "-" . $_POST["pw"] . "-" . $_POST["id"] . "\"
							WHERE objID=1", $mysqlconnection);
						mysql_query("UPDATE Table_" . $_POST["table"] . "
							SET Object=\"" . $generatedname . "\"
							WHERE objID=2", $mysqlconnection);
						mysql_query("UPDATE Table_" . $_POST["table"] . "
							SET Object=\"" . $newtimestamp . "\"
							WHERE objID=4", $mysqlconnection);
						mysql_query("UPDATE Table_index
							SET Object=\"" . $newtimestamp . "-" . $_POST["pw"] . "-" . $_POST["id"] . "\"
							WHERE objID=" . $_POST["table"], $mysqlconnection);
						
					} else {
					
						echo $error_id_password;
						
					}
				} else {
				
					echo $mysql_connection_failed;
				}
			} else {
			
				echo $error_table_not_found;
				
			}
		}
		
  		if ($_POST["action"] == "timestamp") { //if action="check" -> check for the last update of this server (so the last call of action="set")
		
			if ($_POST["table"] > 0 && $_POST["table"] <= $maxtables) { // check if the requested table exists
				
				// selects table -> so command has to be e.g. "action=get&table=23" -> table may be in range of 1 to $maxtables
				$result = mysql_query("SELECT * FROM Table_" . $_POST["table"] ."
					WHERE objID=1", $mysqlconnection); // SQL command for data request -> objID=1 contains information about last access and password
				if ($result) { // if result is not empty due to errors
					
					$row = mysql_fetch_object($result);
					$tablestr = $row->Object; // result contains two pieces of information -> ID (so to speak the row) and OBJECT (the string-information)
					list($timestamp, $password, $id) = explode("-", $tablestr);
					$result = mysql_query("SELECT * FROM Table_" . $_POST["table"] ."
						WHERE objID=4", $mysqlconnection); // get info about last update (row 4)
					if ((($_POST["id"] == $id) || ($id == "")) && (($_POST["pw"] == $password) || ($password == ""))) { //check if the given password and id are identical to the posted or the table was empty
						
						$newtimestamp = time();

						if ($result) {
						
							echo mysql_fetch_object($result)->Object;
							
						} else {
						
							echo $error_mysql_connection_failed;
						}
						
						// now save the access in first row -> to prevent this table to be cleared out when "update.php" is called up
						mysql_query("UPDATE Table_" . $_POST["table"] . "
							SET Object=\"" . $newtimestamp . "-" . $password . "-" . $id . "\"
							WHERE objID=1", $mysqlconnection);
						mysql_query("UPDATE Table_index
							SET Object=\"" . $newtimestamp . "-" . $password . "-" . $id . "\"
							WHERE objID=" . $_POST["table"], $mysqlconnection);
								
					} else {
					
						echo $error_id_password;
						
					}
				} else {
				
					echo $mysql_connection_failed;
					
				}
			} else {
			
				echo $error_table_not_found;
				
			}
		}
		
		if ($_POST["action"] == "key") {
			
			$key = $_POST["key"];
			$id = $_POST["keyid"];
			$origkey = $key;
			
			if (($id < 100) || ($id > 999)) {
				$id = 456;
			}
			
			$i = substr($key, 0, 1);
			$e = substr($key, 1, 1);
			$p = substr($key, 2, 1);
			if ($p != 0) {
				$key = substr($key, 0, 3 + $i + $p) . "." . substr($key, 3 + $i + $p);
			}
			if ($e != 0) {
				$key = substr($key, 0, strlen($key) - $e) . "E" . substr($key, strlen($key) - $e);
			}
			$input = substr($key, 3, $i);
			$output = substr($key, 3 + $i);
			$pow = 1 + $id /1000;
			
			$result = "0 (" . $origkey . ")";
			if (fmod($input, $id) == 0) {
				if (ceil(pow($input, $pow)) == $output) {
					if (fmod($output,4) == 2) {
						$result = "1 (" . $origkey . ")";
					}
				}
			}
			echo $result;
		}
		
		mysql_close($mysqlconnection);
		
  	} else {
		
		echo $error_server_not_set_up;
		
	}
	echo "</syncserver>\n";// end of communication part -> rest will not be read by java app
	flush();
  ?></textarea><br><br> 
  <a href="setup.php">Setup Server</a>
  <a href="reset.php">Reset Server</a>
  <a href="show.php">Show Server</a>
  </body>
  </html>