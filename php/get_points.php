<?php
/*https://www.site-www.ru/maptrack/get_points.php*/
include("config.php");
//session_start();
$filename = 'log.txt';
$sysdate = date('d.m.Y H:i:s', time());

//SELECT p.Id_user,u.code,u.name,p.latitude,p.longitude,p.modified_date FROM map_point p ,map_user u WHERE p.Id_user=u.Id_user
$sql = "SELECT p.Id_user,u.code,u.name,p.latitude,p.longitude,p.modified_date FROM map_point p ,map_user u WHERE p.Id_user=u.Id_user";
$result = mysqli_query($conn,$sql);
$rows = array();
if ($result){
  $rows["errc"]=0;
  $rows["data"]=array();
  while($row = mysqli_fetch_array($result,MYSQLI_ASSOC)) {
    $rows["data"][] = $row;
  }	
  //$rows = mysqli_fetch_array($result,MYSQLI_ASSOC);	
}
else {
     $rows["errc"] = mysqli_errno($conn);
     $rows["errm"] = mysqli_error($conn);
}
echo (json_encode($rows));

?>