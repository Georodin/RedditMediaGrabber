<html>
<?php
	session_start();

	function openSQL($_dbName)
	{
		$servername = "localhost";
		$username = "root";
		$password = "";
		$dbname = $_dbName;
		
		// Create connection
		$conn = new mysqli($servername, $username, $password, $dbname);
		// Check connection
		if ($conn->connect_error) {
		  die("Connection failed: " . $conn->connect_error);
		}
		
		return $conn;
	}

	function DBexist(){
		$conn = openSQL("");
		if($stmt = $conn->query("SHOW DATABASES")){
		$flag = false;
		  while ($row = $stmt->fetch_array()) {
			if($row[0]=="reddit"){
				$flag = true;
			}
		  }
		}else{
		echo $conn->error;
		}
		$conn->close();
		
		return $flag;
	}

	function listTables(){
		$conn = openSQL("reddit");
		if($stmt = $conn->query("SHOW TABLES")){
			if($stmt->num_rows==0){
				echo "No meta info table found... Please start RedditGrabber once.";
			}else if($stmt->num_rows==1){
				echo "No content table found... Please make a first pull with the RedditGrabber.";
			}else{
				echo "<input type='submit' class='button' name='all' value='all' />";
				while ($row = $stmt->fetch_array()) {
					if($row[0]!="redditgrabber_meta"){
						echo "<input type='submit' class='button' name='".$row[0]."' value='".$row[0]."' />";
					}
				}
			}
		}else{
			echo $conn->error;
		}	
	}

	function getMetaStatsInfo(){
		$conn = openSQL("reddit");
		if($stmt = $conn->query("SELECT * FROM redditgrabber_meta")){
			if ($stmt->num_rows > 0) {
				while ($row = $stmt->fetch_assoc()) {
					$first = $row["first"];
					$last = $row["last"];
					$size = $row["size"];
					$files = $row["files"];
					$pulls = $row["pulls"];
					$path = $row["path"];
					
					echo "<p id='metaInfo'>first pull: ".$first." | last pull: ".$last." | size: ".$size." | files: ".$files." | pulls: ".$pulls." | path: ".$path."</p>";
				}
			}
		}else{
			echo $conn->error;
		}	
	}
?>
	<head>
		<script src="jquery-3.6.3.min.js"></script>
		<link rel="stylesheet" href="style.css">
		<script>
			var dark = false;
			var start = true;
			
			var firstAction = "all";
			
			$(document).ready(function(){
				$('.button').click(function(){
					actionPHP($(this).val());
				});
				
				
				actionPHP(firstAction);
				
			});
			
			function toggleDarkmode(){
				dark = !dark;
				document.cookie = "darkmode="+dark;
				checkDarkmode();
			}
			
			function checkDarkmode(){
				if(dark){
					addDarkmode();
				}else{
					removeDarkmode();
				}
			}
			
			function addDarkmode(){
				$("body").addClass("dark-mode-body");
				$("#header").addClass("dark-mode-header");
				$(".svg").addClass("dark-mode-moon");
				$(".button").addClass("dark-mode-button");
				$(".big_entry, .big_entry_gallery, .entry").addClass("dark-mode-entry");
				$(".topBar").addClass("dark-mode-topBar");
			}

			function removeDarkmode(){
				$("body").removeClass("dark-mode-body");
				$("#header").removeClass("dark-mode-header");
				$(".svg").removeClass("dark-mode-moon");
				$(".button").removeClass("dark-mode-button");
				$(".big_entry, .entry, .big_entry_gallery").removeClass("dark-mode-entry");
				$(".topBar").removeClass("dark-mode-topBar");
			}
			
			function actionPHP(clickBtnValue){
				var ajaxurl = 'ajax.php',
				data =  {'action': clickBtnValue};
				$.post(ajaxurl, data, function (response) {
					$("#content").html(response);
					if(start){
						let cookies = document.cookie;
						if(cookies!=""){
							let darkmode = cookies.split("=")[1];
							
							if(darkmode!="false"){
								toggleDarkmode();
							}
						}else{
							document.cookie = "darkmode=false";
						}
						start = false;
					}else{
						checkDarkmode();
					}

				});
			}
			
			function loadAndStartVideo(ref, url){
				
				var videoElement = $("video", ref)[0];
				$("video", ref).css("display", "block");
				console.log("ref out: "+ref);
				
				videoElement.addEventListener('playing', (event) => {
					$(ref).css("background-image", "none");
				});
				
				videoElement.addEventListener('loadeddata', (e) => {

				   if(videoElement.readyState >= 3){
					   	
						console.log("ref in: "+ref);
				   }
				   
				   console.log("state: "+videoElement.readyState);

				});
				
				if($("source", ref).attr("src")!=url){
					$("source", ref).attr("src", url);
					$("video", ref)[0].load();
				}
				

				
				$("video", ref)[0].play();
			}
			
			function stopVideo(ref, url){
				$(ref).css("background-image", "url("+url+")");
				$("video", ref).css("display", "none");
				$("video", ref)[0].pause();
			}
		</script>
	</head>
	<body>
		<div id="header">
			<h1>Reddit Viewer</h1><?php getMetaStatsInfo() ?><div id="moon" class="svg" onclick="toggleDarkmode()"></div>
		</div>
		<?php

		/*

		$sql = "SELECT * FROM `pics` ORDER BY `pics`.`keyId` ASC";
		$result = $conn->query($sql);

		if ($result->num_rows > 0) {
		  // output data of each row
		  while($row = $result->fetch_assoc()) {
			echo "id: " . $row["id"]. " - Name: " . $row["user"]. " " . $row["userUri"]. "<br>";
		  }
		} else {
		  echo "0 results";
		}
		*/



		/*
		if($stmt = $conn->query("SHOW TABLES")){
		  echo "No of records : ".$stmt->num_rows."<br>";
		  while ($row = $stmt->fetch_array()) {
		echo $row[0]."<br>";
		  }
		}else{
		echo $conn->error;
		}
		*/
		
		if(DBexist()){
			listTables();
		}else{
			echo "Database not initialised... Please start the RedditGrabber once.";
		}
		
		if (isset($_SESSION['last_site'])) {
			echo "<script>firstAction = '".$_SESSION['last_site']."';</script>";
		}
		
		?>
		<div id="content"></div>
	</body>
</html>