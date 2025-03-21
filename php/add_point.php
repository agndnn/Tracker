<?php
/*http://www.site-www.ru/maptrack/add_point.php?code=1&lat=56.2979&lon=43.2435*/
$lat=0;
$lon=0;
$id_user=0;
$is_log=0;

//function log_msg($p_msg)
//{
//	if ($is_log==1){
		//echo $p_msg."\n";
	//}
//}
	
include("config.php");
try {

		//session_start();
		$filename = 'add_point_log.txt';
		$sysdate = date('d.m.Y H:i:s', time());

		if($_SERVER["REQUEST_METHOD"] == "GET") {
			$lat=$_GET["lat"];
			$lon=$_GET["lon"];
			$usercode=$_GET["code"];	
			$phone=$_GET["phone"];	
			$is_log=$_GET["is_log"];	
		} elseif ($_SERVER["REQUEST_METHOD"] == "POST") {
			$lat=$_POST["lat"];
			$lon=$_POST["lon"];
			$usercode=$_POST["code"];	
			$phone=$_POST["phone"];	
			$is_log=$_POST["is_log"];	
		}
		$conn->autocommit(FALSE);

		$sql = "SELECT p.id_user,p.latitude,p.longitude 
						FROM map_point p
							,map_user u
				WHERE p.id_user=u.id_user 
					AND u.code = '$usercode'
					AND u.phone = '$phone'";
		$result = mysqli_query($conn,$sql);
		if ($result){
			if (mysqli_num_rows($result)>=1){ //пользователь существует
				$row = mysqli_fetch_array($result,MYSQLI_ASSOC);
				$id_user = $row['id_user'];
				$sql="update map_point set latitude=$lat, longitude=$lon, modified_date=SYSDATE() where id_user=$id_user";
				//log_msg ($sql.'<br/>');
				$result = mysqli_query($conn,$sql);			
				if (!$result){
					//log_msg( "sqlerror ".mysqli_errno($conn).": ".mysqli_error($conn)."<br/>sql=".$sql);
					throw new Exception($conn->error);
				}
				if ($row['latitude']!=$lat or $row['longitude']!=$lon) { //координаты изменились
					$sql="insert into map_point_hist(id_user, latitude, longitude, created_date) values($id_user, $lat, $lon, SYSDATE())";
					//log_msg ($sql.'<br/>');
					$result = mysqli_query($conn,$sql);
					if (!$result){
						//log_msg( "sqlerror ".mysqli_errno($conn).": ".mysqli_error($conn)."<br/>sql=".$sql);
						throw new Exception($conn->error);
					}
				}
				
			} 
			else{
				throw new Exception("No registration error");
			}
			/*
			else { //пользователь не существует
				$sql="insert into map_user(code,name,created_date) values('$usercode','$usercode',SYSDATE())";
				//log_msg ($sql.'<br/>');
				$result = mysqli_query($conn,$sql);
				if ($result){		
					$id_user = mysqli_insert_id($conn);
					//log_msg ("id_user=".$id_user."<br/>");
					$sql="insert into map_point(id_user, latitude, longitude, created_date) values($id_user, $lat, $lon, SYSDATE())";
					//log_msg ($sql.'<br/>');
					$result = mysqli_query($conn,$sql);
					if (!$result){
						//log_msg( "sqlerror ".mysqli_errno($conn).": ".mysqli_error($conn)."<br/>sql=".$sql);
						$conn->rollback();
						exit(1);			
					}
					
					$sql="insert into map_point_hist(id_user, latitude, longitude, created_date) values($id_user, $lat, $lon, SYSDATE())";
					//log_msg ($sql.'<br/>');
					$result = mysqli_query($conn,$sql);
					if (!$result){
						//log_msg( "sqlerror ".mysqli_errno($conn).": ".mysqli_error($conn)."<br/>sql=".$sql);
						$conn->rollback();
						exit(1);			
					}
					
				}
				else{
					//log_msg( "sqlerror ".mysqli_errno($conn).": ".mysqli_error($conn)."<br/>sql=".$sql);
					$conn->rollback();
					exit(1);
				}
			}*/
		}
		else {
			//log_msg( "sqlerror ".mysqli_errno($conn).": ".mysqli_error($conn)."<br/>sql=".$sql);
			throw new Exception($conn->error);
		}
		$conn->commit();
		$rows["errc"] = 0; // Успех
		$rows["errm"] = "";
		$rows["user_id"] = $user_id;
	
} catch (Exception $e) {
  // В случае ошибки возвращаем код ошибки и сообщение
  $rows["errc"] = 1; // Ошибка
  $rows["errm"] = $e->getMessage();
  $rows["user_id"] = -1;
  $conn->rollback();
}

$conn->commit();
$conn->close();


$entry = $sysdate.";".$usercode.";".$lat.";".$lon.";". $rows["errc"].";". $rows["errm"]."\n";

//log_msg ("<br/>OK <br/>");
//log_msg ($entry);


file_put_contents($filename, $entry, FILE_APPEND|LOCK_EX);

include("get_points.php");
?>