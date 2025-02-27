<?php
/*https://www.site-www.ru/maptrack/get_points.php*/
include("config.php");
//session_start();
$filename = 'get_point_log.txt';
$sysdate = date('d.m.Y H:i:s', time());


if($_SERVER["REQUEST_METHOD"] == "GET") {
	$phone=$_GET["phone"];
	$code=$_GET["code"];
	$is_log=$_GET["is_log"];	
} elseif ($_SERVER["REQUEST_METHOD"] == "POST") {
	$phone=$_POST["phone"];
	$code=$_POST["code"];
	$is_log=$_POST["is_log"];	
}

$sql = "SELECT p.latitude, p.longitude, p.modified_date
  FROM map_point p
       ,map_user u
WHERE p.id_user = u.id_user
      and u.phone = '$phone'
      and u.code = '$code'";
$result = mysqli_query($conn,$sql);

$rows = array();

if ($result){
  $cnt=mysqli_num_rows($result);

  if ($cnt>0) {
    $rows["errc"]=0;
    $rows["data"]=mysqli_fetch_array($result,MYSQLI_ASSOC);   
  } 
  else {
      //
      $rows["errc"] = 1; 
      $rows["errm"] = "данные не найдены"; //
   }

}
else {
  $rows["errc"] = 2;
  $rows["errm"] = mysqli_error($conn);
}

$entry = $sysdate.";phone=".$phone.";code=".$code.";row=".json_encode($rows)."\n";
file_put_contents($filename, $entry, FILE_APPEND|LOCK_EX);
echo(json_encode($rows));


?>