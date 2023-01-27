<?php
	session_start();

function openSQL($_dbName)
{
    $servername = "localhost";
    $username   = "root";
    $password   = "";
    $dbname     = $_dbName;
    
    // Create connection
    $conn = new mysqli($servername, $username, $password, $dbname);
    // Check connection
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
    
    
    
    return $conn;
}

function getMetaStatsInfo(){
	$conn = openSQL("reddit");
	if($stmt = $conn->query("SELECT * FROM redditgrabber_meta")){
		if ($stmt->num_rows > 0) {
			while ($row = $stmt->fetch_assoc()) {
				$path = $row["path"];
				
				return $path;
			}
		}
	}else{
		echo $conn->error;
	}	
}

function tableExist($name){
	$conn = openSQL("reddit");
	if($stmt = $conn->query("SHOW TABLES")){
		if($stmt->num_rows > 0){
			while ($row = $stmt->fetch_array()) {
				if($row[0]==$name){
					return true;
				}
			}
		}
		return false;
	}else{
		echo $conn->error;
	}
}

function listAllTables(){
	$conn = openSQL("reddit");
	if($stmt = $conn->query("SHOW TABLES")){
		if($stmt->num_rows > 0){
			$output = "";
			while ($row = $stmt->fetch_array()) {
				if($row[0]!="redditgrabber_meta"){
					$output .= "SELECT * FROM `".$row[0]."` UNION ";
				}
			}
			$output = rtrim($output, " UNION ");
			$output .= " ORDER BY `date` DESC";
			return $output;
		}
		
		return $output;
	}else{
		echo $conn->error;
	}
	
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

function UR_exists($url){
   $headers=get_headers($url);
   return stripos($headers[0],"200 OK")?true:false;
}

function displayEntrys($sql){
	$conn   = openSQL("reddit");
    
    $result = $conn->query($sql);
	if(!$result){
	
	} else
    if ($result->num_rows > 0) {
        // output data of each row
        while ($row = $result->fetch_assoc()) {
			$keyId = $row["keyId"];
			$user = $row["user"];
			$userUri = $row["userUri"];
			$id = $row["id"];
			$uri = $row["uri"];
			$date = $row["date"];
			$title = $row["title"];
			$media = explode(";", $row["media"]);
			
			$subredditPos = strpos($uri, "/r/");
			//(strpos($uri, "/r/")!=false) ? strpos($uri, "/r/") : strpos($uri, "/u/");
			$actual_link = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";
			
					
			$filenameJPG = substr($media[0], 0, strrpos($media[0], ".")).".jpg";
			$mediaUrlPreviewURL = "";
			$subreddit = "";
			if(tableExist(str_replace("/u/","",$user))){
				$subreddit = str_replace("/u/","",$user);
				$mediaUrlPreviewURL = rtrim($actual_link, "/ajax.php")."/cl/".$subreddit."/".$filenameJPG;
				
				if (!UR_exists($mediaUrlPreviewURL)) {
					$subreddit = substr($uri, $subredditPos+3, strlen($uri));
					$subreddit = substr($subreddit, 0, strpos($subreddit, "/"));
				}
			}else{
				//echo "<script>console.log('".$uri."');</script>";
				$subreddit = substr($uri, $subredditPos+3, strlen($uri));
				//echo "<script>console.log('".$subreddit."');</script>";
				$subreddit = substr($subreddit, 0, strpos($subreddit, "/"));
				//echo "<script>console.log('".$subreddit."');</script>";
			}

			$mediaUrl = "./cl/".$subreddit."/".$media[0];
			
			$mediaUrlPreviewURL = rtrim($actual_link, "/ajax.php")."/cl/".$subreddit."/previews/".$filenameJPG;
			$mediaUrlPreview = "./cl/".$subreddit."/previews/".$filenameJPG;
			
			$mediaUrlOut = $mediaUrl;
			if (UR_exists($mediaUrlPreviewURL)) {
				
				$mediaUrlOut = $mediaUrlPreview;
			}
			
			$postedTo = "";
			
			//echo "<script>console.log('".$uri." cnt "."u_".str_replace("/u/","",$user)."');</script>";
			
			if(!str_contains($uri, "u_".str_replace("/u/","",$user))){
				$postedTo = substr($uri, $subredditPos+3, strlen($uri));
				//echo "<script>console.log('".$postedTo."');</script>";
				$postedTo = substr($postedTo, 0, strpos($postedTo, "/"));
				//echo "<script>console.log('".$postedTo."');</script>";
				$postedTo = "<a href='https://www.reddit.com/r/$postedTo/' target='_blank'>r/$postedTo</a> - ";
			}
			
			if(str_contains($mediaUrl, ".gif")){
				 echo "<div class='entry' onclick=\"actionPHP('id:".$subreddit.":".$keyId."')\"".
						"onmouseover=\"$(this).css('background-image', 'url(".$mediaUrl.")');\" ".
						"onmouseout=\"$(this).css('background-image', 'url(".$mediaUrlOut.")');\" style='background-image: url(".$mediaUrlOut.");'   >";
			}else if(str_contains($mediaUrl, ".mp4")){
				 echo "<div class='entry' onclick=\"actionPHP('id:".$subreddit.":".$keyId."')\" onmouseover=\"loadAndStartVideo(this, '".$mediaUrl."');\" onmouseout=\"stopVideo(this, '".$mediaUrlOut."');\" style='background-image: url(".$mediaUrlOut.");' >";
			}else{
				 echo "<div class='entry' onclick=\"actionPHP('id:".$subreddit.":".$keyId."')\" style='background-image: url(".$mediaUrlOut.");'>";
			}
			

           
			/*
            echo "<p>".$keyId."</p>";
            echo "<p>".$user."</p>";
            echo "<p>".$userUri."</p>";
            echo "<p>".$id."</p>";
            echo "<p>".$uri."</p>";
            echo "<p>".$date."</p>";
            echo "<p>".$title."</p>";
            echo "<p>".$media[0]."</p>";
            echo "<p>".$mediaUrl."</p>";
			*/
			
				if(count($media)>2) {
					echo "<div class='gallery svg'></div>";
					echo "<div class='gallery_count'>".(count($media)-1)."</div>";
				}
				
				if(str_contains($mediaUrl, ".mp4")){
					echo "<video style='display: none;' loop='true' muted='muted'>";
					echo "<source src='' type='video/mp4'>";
					echo "</video>";
					
					echo "<div class='mediatype'>MP4</div>";
				}				
				if(str_contains($mediaUrl, ".gif")){
					echo "<div class='mediatype'>GIF</div>";
				}
				echo "<div class='title'>".$title."</div>";
				echo "<div class='user_info'>".$postedTo."<a href='".$userUri."' target='_blank'>".$user."</a></div>";
				
            echo "</div>";
			
        }
    } else {
        echo "<div>No entrys present...</div>";
    }
}

function displaySingleEntry($sql){
	$conn   = openSQL("reddit");
    
    $result = $conn->query($sql);
    
    if ($result->num_rows > 0) {
        // output data of each row
        while ($row = $result->fetch_assoc()) {
			$keyId = $row["keyId"];
			$user = $row["user"];
			$userUri = $row["userUri"];
			$id = $row["id"];
			$uri = $row["uri"];
			$date = $row["date"];
			$title = $row["title"];
			$media = explode(";", $row["media"]);
			
			
			$filenameJPG = substr($media[0], 0, strrpos($media[0], ".")).".jpg";
			$subredditPos = strpos($uri, "/r/");
			
			$subreddit = substr($uri, $subredditPos+3, strlen($uri));
			//echo "<script>console.log('".$subreddit."');</script>";
			$subreddit = substr($subreddit, 0, strpos($subreddit, "/"));
			
			
			
			$actual_link = (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? "https" : "http") . "://$_SERVER[HTTP_HOST]$_SERVER[REQUEST_URI]";
						
			if(tableExist(str_replace("/u/","",$user))){
				$subreddit = str_replace("/u/","",$user);
				$mediaUrlPreviewURL = rtrim($actual_link, "/ajax.php")."/cl/".$subreddit."/".$filenameJPG;
				
				if (!UR_exists($mediaUrlPreviewURL)) {
					$subreddit = substr($uri, $subredditPos+3, strlen($uri));
					//echo "<script>console.log('".$subreddit."');</script>";
					$subreddit = substr($subreddit, 0, strpos($subreddit, "/"));
				}
			}else{
					$subreddit = substr($uri, $subredditPos+3, strlen($uri));
					//echo "<script>console.log('".$subreddit."');</script>";
					$subreddit = substr($subreddit, 0, strpos($subreddit, "/"));
			}

			$mediaUrl = "./cl/".$subreddit."/".$media[0];
			
			if(count($media)>2) {
				echo "<div class='big_entry_gallery'>";
			}else{
				echo "<div class='big_entry'>";
			}
			
			/*
            echo "<p>".$keyId."</p>";
            echo "<p>".$user."</p>";
            echo "<p>".$userUri."</p>";
            echo "<p>".$id."</p>";
            echo "<p>".$uri."</p>";
            echo "<p>".$date."</p>";
            echo "<p>".$title."</p>";
            echo "<p>".$media[0]."</p>";
            echo "<p>".$mediaUrl."</p>";
			*/	
				echo "<div class='topbar'><a class='subLink' href='https://www.reddit.com/".$subreddit."' target='_blank'>r/".$subreddit."</a> <span id='dot'>•</span> Posted by <a href='".$userUri."' target='_blank'>".ltrim($user, '/')."</a> ".getTimeDisplay($date)."<br><span id='title'>".$title."</span><a href='".$uri."' target='_blank'><div id='reddit' class='svg'></div></a></div>";
				
				if(str_contains($mediaUrl, ".mp4")){
					echo "<video loop controls autoplay muted='muted' class='big_vid'>";
					echo "<source src='$mediaUrl' type='video/mp4'>";
					echo "</video>";
				}else{
					echo "<img src='$mediaUrl'>";
				}
				
				if(count($media)>2) {
					for ($i = 1; $i < count($media)-1; $i++) {
						$mediaUrlFOR = "./cl/".$subreddit."/".ltrim($media[$i]);
						echo "<img src='$mediaUrlFOR'>";
					}
				}
				
            echo "</div>";
			
        }
    } else {
        echo "<div>No entrys present...</div>";
    }
}

function getTimeDisplay($posted){
	$now = new DateTime('now');
	$posted = new DateTime($posted);

	$diff = $posted->diff($now);

	$hours = $diff->h;
	$hours = $hours + ($diff->days*24);

	if($hours<=23){
		return $hours." hours ago";
	}else if($hours<=720){
		return floor($hours/24)." days ago";
	}else if($hours<=8640){
		return floor($hours/732)." months ago";
	}else {
		return floor($hours/8760)." years ago";
	}
}

if (isset($_POST['action'])) {
	
	if(DBexist()){
		if ($_POST['action'] == "all") {
			$sql = listAllTables();
			if($sql!=false){
				displayEntrys($sql);
			}
		} else if(str_contains($_POST['action'], 'id:')){
			
			$info = explode(":", $_POST['action']);
			
			$sql    = "SELECT * FROM `$info[1]` WHERE `$info[1]`.`keyId` = $info[2]";
			displaySingleEntry($sql);
		} else {
			$sql    = "SELECT * FROM `" . $_POST['action']."` ORDER BY `date` DESC";
			
			displayEntrys($sql);
		}
	}
	$_SESSION['last_site'] = $_POST['action'];
}
?>