<?php
	$user_id = $_GET['user_id'];
	$resource_id = $_GET['resource_id'];
	$nonce_id = $_GET['nonce_id'];
	$banned_uids = file('library/banned-IDs.txt');
	$banned_rids = file('library/banned-resourceIDs.txt');
	$banned_nids = file('library/banned-nonces.txt');
	$log = fopen('library/antipiracy.log', 'a');
	
	foreach($banned_uids as $line1) {
		if ($user_id == $line1) {
			echo "false";
			fwrite($log, date('Y/m/d H:i:s') . ' - ' . $_SERVER['REMOTE_ADDR'] . ' connected with banned UID: ' . $user_id . "\n");
			fclose($log);
			exit;
		}
	}
	
	foreach($banned_rids as $line2) {
		if ($resource_id == $line2) {
			fwrite($log, date('Y/m/d H:i:s') . ' - ' . $_SERVER['REMOTE_ADDR'] . ' connected with banned RID: ' . $resource_id . "\n");
			fclose($log);
			exit;
		}
	}
	
	foreach($banned_nids as $line3) {
		if ($nonce_id == $line3) {
			fwrite($log, date('Y/m/d H:i:s') . ' - ' . $_SERVER['REMOTE_ADDR'] . ' connected with banned NID: ' . $nonce_id . "\n");
			fclose($log);
			exit;
		}
	}
	
	fwrite($log, date('Y/m/d H:i:s') . ' - ' . $_SERVER['REMOTE_ADDR'] . ' passed with UID/RID/NID: ' . $user_id . '/' . $resource_id . '/' . $nonce_id . "\n");
	fclose($log);
	exit;
?>