  <html>
  <head>
<meta http-equiv="content-type" content="text/html; UTF-8">
  <title>Server v.1.1 (13-04-21)</title>
  </head>
  <body>
  <h2>Reset Server</h2>
  <textarea name="output" rows=50 cols=120><?php
  echo "<syncserver>";	
  $userdata = "user.dat";
  $success = false;
  if (file_exists($userdata))
  {
  	  $success = unlink($userdata);
  }
  else 
  	{
  		$success = true;
  	}
  echo (int) $success;
  echo "</syncserver>\nRESET";	
  if ($success) {
	echo " SUCCESSFUL";
	} else {
	echo " NOT SUCCESSFUL";
	}
  ?>
  </textarea><br>
  <a href="setup.php">Setup Server</a>
  <a href="show.php">Show Server</a>
  </body>
  </html>