  <html>
  <head>
<meta http-equiv="content-type" content="text/html; UTF-8">
  <title>Server v.1.1 (13-04-21)</title>
  </head>
  <body>
  <h2>Setup of Server</h2>
  <?php
	$userdata = "user.dat";
	$nopassword = "";
	$nooccupacy = "empty";
	$waiting_time_in_min = 1;
	
	//standard error messages -> are recognized by java app as well!
	$error_server_not_set_up = "#001 - Server not set up -> Goto /setup.php and enter connection information";
	$error_mysql_connection_failed = "#002 - Server not correctly set up -> Goto /reset.php and redo setup";
	$error_password = "#003 - Wrong password";
	$error_table_not_found = "#004 - Requested table does not exist. Table must be between 1 and number of tables.";
	$message_update_successfull = "1 - Update successfull";
	$error_update_failed = "#005 - Update not successfull";
	$message_upload_successfull = "1 - Upload successfull";
	$error_upload_failed = "#006 - Upload not successfull";

	if (($_POST["username"] != null)) //user has entered data -> checke whether correct or not
  		{
  			$mysqlconnection = mysql_connect($_POST["server"], $_POST["username"], $_POST["password"]);
			echo "Connecting server...";
			flush();
  			if ($mysqlconnection != false)
  				{
  				echo "done;<br>Writing datafile..."; //Connection successfull -> write datafile (all other calls from this server load from this data file!!!)
				flush();
  				$serverfile = fopen($userdata, "w");
				fwrite($serverfile, $_POST["server"]);
  				fwrite($serverfile, ";");  			
  				fwrite($serverfile, $_POST["db"]);
  				fwrite($serverfile, ";");
  				fwrite($serverfile, $_POST["username"]);
  				fwrite($serverfile, ";");
  				fwrite($serverfile, $_POST["password"]);
				fwrite($serverfile, ";");
  				fwrite($serverfile, $_POST["tablenr"]);
  				fclose($serverfile);
  				echo "done;<br>Creating database..."; //Create the db if necessary.
				flush();
  				if (mysql_query("CREATE DATABASE " . $_POST["db"],$mysqlconnection))
  					{
 					echo "done;<br>";
  					}
				else
  					{
 					echo "failed - " . mysql_error() . "<br>";
  					}
				flush();
  				mysql_select_db($_POST["db"], $mysqlconnection);
				if ($_POST["tablenr"] > 0)
					{
					$maxtables = $_POST["tablenr"];
					}
					
				//Setup the index table
				$query = "CREATE TABLE Table_index
					(
					objID int NOT NULL AUTO_INCREMENT,
					PRIMARY KEY(objID),
					Object TEXT
					)";
				echo "Creating index table...";
				flush();
				if (mysql_query($query, $mysqlconnection))
					{
					echo "done;<br>";
					}
				else 
					{
					echo "failed - " . mysql_error() . "<br>";
						$query = "TRUNCATE TABLE Table_index";
						mysql_query($query, $mysqlconnection);					
					}
				flush();
				//Setup the Tables themselves
 				for($i=1; $i<=$maxtables; $i++)
 					{
					//Create table if necessary
	 				$query = "CREATE TABLE Table_" . $i ."
						(
						objID int NOT NULL AUTO_INCREMENT,
						PRIMARY KEY(objID),
						Object TEXT
						)";
					
					echo "Creating Table_" . $i . "...";
					flush();
					if (mysql_query($query, $mysqlconnection))
						{
						echo "done;<br>";
						}
					else 
						{
						echo "failed - " . mysql_error() . "<br>";
						$query = "TRUNCATE TABLE Table_" . $i;
						mysql_query($query, $mysqlconnection);
						}
					flush();
					// add first row -> contains date, table name and password
					$table = "Table_" . $i; //which table to open
					$query = "INSERT INTO " . $table . "
						VALUE (NULL, NULL)";
					echo "Filling Table_" . $i . "...";
					flush();
					if (mysql_query($query, $mysqlconnection))
						{
						echo "row 1 completed...";
						}
					else 
						{
						echo "failed - " . mysql_error() . "<br>";						
						}
					flush();
					// add second row -> contains name data
					$table = "Table_" . $i; //which table to open
					$query = "INSERT INTO " . $table . "
						VALUE (NULL, NULL)";
					if (mysql_query($query, $mysqlconnection))
						{
						echo "row 2 completed....";
						}
					else 
						{
						echo "failed - " . mysql_error() . "<br>";						
						}
					flush();
					// add third row -> contains real string data
					$table = "Table_" . $i; //which table to open
					$query = "INSERT INTO " . $table . "
						VALUE (NULL, NULL)";
					if (mysql_query($query, $mysqlconnection))
						{
						echo "row 3 completed....";
						}
					else 
						{
						echo "failed - " . mysql_error() . "<br>";						
						}
					flush();
						// add fourth row -> contains last update timestamp
					$table = "Table_" . $i; //which table to open
					$query = "INSERT INTO " . $table . "
						VALUE (NULL, NULL)";
					if (mysql_query($query, $mysqlconnection))
						{
						echo "row 4 completed....";
						}
					else 
						{
						echo "failed - " . mysql_error() . "<br>";						
						}
					flush();
					// add row to index table
					$query = "INSERT INTO Table_index
						VALUE (NULL, NULL)";
					if (mysql_query($query, $mysqlconnection))
						{
						echo "index added...done;<br>";
						}
					}
					flush();
				}
  			else 
  				{
  					echo "failed - could not connect to mysql -> refresh (F5)<br>";
  				}
			flush();
  			mysql_close($mysqlconnection);
  		}
  	else 
  		{
  			if (file_exists($userdata))
  				{//read the datafile with mysql-user-data
				$serverfile = fopen($userdata, "r");	 
				$txt = fread($serverfile,filesize($userdata));
				$server = substr($txt,0, strpos($txt, ";"));
				$txt = substr($txt, strpos($txt, ";")+1);
				$db = substr($txt,0, strpos($txt, ";"));
				$txt = substr($txt, strpos($txt, ";")+1);
				$username = substr($txt,0, strpos($txt, ";"));
				$txt = substr($txt, strpos($txt, ";")+1);
				$password = substr($txt,0, strpos($txt, ";"));
				$txt = substr($txt, strpos($txt, ";")+1);
				$maxtables = substr($txt,0); // info about the server -> how many tables exist?
				fclose($serverfile);
				
				$mysqlconnection = mysql_connect($server, $username, $password);
				echo "Connecting server...";  			
				if ($mysqlconnection != false)
					{
					echo "done;<br>";
					echo "Creating database...";
					if (mysql_query("CREATE DATABASE " . $db,$mysqlconnection))
						{
						echo "done;<br>";
						}
					else
						{
						echo "failed - " . mysql_error() . "<br>";
						}
					mysql_select_db($db, $mysqlconnection);
					//Setup the index table
					$query = "CREATE TABLE Table_index
						(
						objID int NOT NULL AUTO_INCREMENT,
						PRIMARY KEY(objID),
						Object TEXT
						)";
					echo "Creating index table...";
					if (mysql_query($query, $mysqlconnection))
						{
						echo "done;<br>";
						}
					else 
						{
						echo "failed - " . mysql_error() . "<br>";
							$query = "TRUNCATE TABLE Table_index";
							mysql_query($query, $mysqlconnection);					
						}
					for($i=1; $i<=$maxtables; $i++)
						{
						$query = "CREATE TABLE Table_" . $i ."
							(
							objID int NOT NULL AUTO_INCREMENT,
							PRIMARY KEY(objID),
							Object TEXT
							)";
						echo "Creating Table_" . $i . "...";
						if (mysql_query($query, $mysqlconnection))
							{
							echo "done;<br>";
							}
						else 
							{
							echo "failed - " . mysql_error() . "<br>";
							$query = "TRUNCATE TABLE Table_" . $i;
							mysql_query($query, $mysqlconnection);
							}
						// add first row -> contains date, table name and password
						$table = "Table_" . $i; //which table to open
						$query = "INSERT INTO " . $table . "
							VALUE (NULL, NULL)";
						echo "Filling Table_" . $i . "...";
						if (mysql_query($query, $mysqlconnection))
							{
							echo "row 1 completed...";
							}
						else 
							{
							echo "failed - " . mysql_error() . "<br>";						
							}
						// add second row -> contains name data
						$table = "Table_" . $i; //which table to open
						$query = "INSERT INTO " . $table . "
							VALUE (NULL, NULL)";
						if (mysql_query($query, $mysqlconnection))
							{
							echo "row 2 completed....";
							}
						else 
							{
							echo "failed - " . mysql_error() . "<br>";						
							}
						// add third row -> contains real string data
						$table = "Table_" . $i; //which table to open
						$query = "INSERT INTO " . $table . "
							VALUE (NULL, NULL)";
						if (mysql_query($query, $mysqlconnection))
							{
							echo "row 3 completed....";
							}
						else 
							{
							echo "failed - " . mysql_error() . "<br>";						
							}
							// add fourth row -> contains last update timestamp
					$table = "Table_" . $i; //which table to open
					$query = "INSERT INTO " . $table . "
						VALUE (NULL, NULL)";
					if (mysql_query($query, $mysqlconnection))
						{
						echo "row 4 completed....";
						}
					else 
						{
						echo "failed - " . mysql_error() . "<br>";						
						}
						// add row to index table
						$query = "INSERT INTO Table_index
							VALUE (NULL, NULL)";
						if (mysql_query($query, $mysqlconnection))
							{
							echo "index added...done;<br>";
							}
						}
					}
				else 
					{
						echo "failed - could not connect to mysql -> refresh (F5)<br>";
					}
				mysql_close($mysqlconnection);
				
				echo "server set: ";
  				$serverfile = fopen($userdata, "r");	
  				$txt = fread($serverfile,filesize($userdata));
  				echo substr($txt,0, strpos($txt, ";"));
  				fclose($serverfile);
 				}
  			else 
  				{
  				echo "<h2>Enter MySQL Information</h2><form action=\"setup.php\" method=\"post\">
  				mysql-server:	<input type=\"text\" size=\"17\" name=\"server\">
  				<br>
  				mysql-db:	<input type=\"text\" size=\"17\" name=\"db\">
  				<br>
  				mysql-username:	<input type=\"text\" size=\"17\" name=\"username\">
  				<br>
  				mysql-password:	<input type=\"password\" size=\"17\" name=\"password\">
  				<br>
				table-number:	<input type=\"text\" size=\"17\" name=\"tablenr\">
  				<br><br>
  				<input type=\"submit\" value=\"OK\">
  				</form><br>Please enter your mysql data and push *ok*";
  				}
  		}	
  ?><br><br> 
  <a href="setup.php">Setup Server</a>
  <a href="reset.php">Reset Server</a>
  <a href="show.php">Show Server</a>
  </body>
  </html>