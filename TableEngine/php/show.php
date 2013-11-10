  <html>
  <head>
<meta http-equiv="content-type" content="text/html; UTF-8">
  <title>Server v.1.1 (13-04-21)</title>
  </head>
  <body>
  <h2>Show Server</h2>
  <textarea name="output" rows=8 columns=500><?php

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
	
  if (($_POST["name"] != null)) //der user hat daten eingegeben -> checke ob sie korrekt sind
  	{
	if (file_exists($userdata))
  		{
  		$serverfile = fopen($userdata, "r");	 //read the datafile with mysql-user-data
  		$txt = fread($serverfile,filesize($userdata));
  		$server = substr($txt,0, strpos($txt, ";"));
  		$txt = substr($txt, strpos($txt, ";")+1);
  		$db = substr($txt,0, strpos($txt, ";"));
  		$txt = substr($txt, strpos($txt, ";")+1);
  		$username = substr($txt,0, strpos($txt, ";"));
		$txt = substr($txt, strpos($txt, ";")+1);
  		$password = substr($txt,0, strpos($txt, ";"));
		$txt = substr($txt, strpos($txt, ";")+1);
		$maxtables = substr($txt,0);
  		fclose($serverfile);
  		$mysqlconnection = mysql_connect($server, $username, $password);
		mysql_select_db($db,$mysqlconnection);
		if ($_POST["name"] == $username && $_POST["pass"] == $password)
			{
			echo"<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><br>";
			echo "<pre>syncserver<br>";
			$column = 1;
			echo "<table border=1>";
			while ($column <= $maxtables)
				{
				echo "<tr><th>" . $column . "</th>";
				$query = "SELECT * FROM Table_" . $column;
				$result = mysql_query($query, $mysqlconnection);
				if ($result)
					{
					while($row = mysql_fetch_object($result))
						{
						echo "<th>" . $row->Object . "</th>";
						}
					}
				else 
					{
					echo "<th>failed - " . mysql_error() . "</th>";
					}
				echo "</tr>";
				$column = $column +1;
				}
			echo "</table>";
			}
		else 
			{
			echo "login denied:" . $_POST["name"] . "/" . $username . $_POST["pass"] . "/" . $password;
			}
		mysql_close($mysqlconnection);
		}
		echo "</textarea>";
	}
  else 
	{
	echo "</textarea><br>Login required:<br>
		<form action=\"show.php\" method=\"post\">
  				name:	<input type=\"text\" size=\"17\" name=\"name\">
  				<br>
  				password:	<input type=\"text\" size=\"17\" name=\"pass\">
  				<br><br>
  				<input type=\"submit\" value=\"OK\">
  				</form><br>Please enter your mysql data and push *ok*";
	}	
  ?><br><br> 
  <a href="setup.php">Setup Server</a>
  <a href="reset.php">Reset Server</a>
  </body>
  </html>