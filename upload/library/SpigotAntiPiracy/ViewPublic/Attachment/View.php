<?php

class SpigotAntiPiracy_ViewPublic_Attachment_View extends XFCP_SpigotAntiPiracy_ViewPublic_Attachment_View
{
	public function renderRaw()
	{
		$attachment = $this->_params['attachment'];

		if (!headers_sent() && function_exists('header_remove'))
		{
			header_remove('Expires');
			header('Cache-control: private');
		}

		$extension = XenForo_Helper_File::getFileExtension($attachment['filename']);
		$imageTypes = array(
			'gif' => 'image/gif',
			'jpg' => 'image/jpeg',
			'jpeg' => 'image/jpeg',
			'jpe' => 'image/jpeg',
			'png' => 'image/png'
		);

		if (in_array($extension, array_keys($imageTypes)))
		{
			$this->_response->setHeader('Content-type', $imageTypes[$extension], true);
			$this->setDownloadFileName($attachment['filename'], true);
		}
		else
		{
			$this->_response->setHeader('Content-type', 'application/octet-stream', true);
			$this->setDownloadFileName($attachment['filename']);
		}

		$this->_response->setHeader('Content-Length', $attachment['file_size'], true);
		$this->_response->setHeader('X-Content-Type-Options', 'nosniff');
		
		if ($this->_params['inject'] && $this->endsWith($attachment['filename'], 'jar'))
		{
			$output = new SpigotAntiPiracy_FileOutput($this->_params['attachmentFile'], $this->_params['user_id'], $this->_params['resource_id'], $this->_params['link']);
			$this->_response->setHeader('Content-Length', strlen($output), true);
			return $output;
		}
		else
		{
			$this->_response->setHeader('ETag', '"' . $attachment['attach_date'] . '"', true);
			return new XenForo_FileOutput($this->_params['attachmentFile']);
		}
	}
	
	public function endsWith($haystack, $needle)
	{
		$length = strlen($needle);
		if ($length == 0) {
			return true;
		}
		
		return (substr($haystack, -$length) === $needle);
	}
}