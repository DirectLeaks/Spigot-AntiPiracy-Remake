<?php

class SpigotAntiPiracy_FileOutput
{
	protected $_fileName = '';
	protected $_contents = null;
	
	protected $_userID = '%%__USER__%%';
	protected $_resourceID = '%%__RESOURCE__%%';
	protected $_link = '';

	public function __construct($fileName, $userID, $resourceID, $link)
	{
		if (!file_exists($fileName))
		{
			throw new XenForo_Exception('File does not exist');
		}
		if (!is_readable($fileName))
		{
			throw new XenForo_Exception('File is not readable');
		}

		$this->_fileName = $fileName;
		$this->_userID = $userID;
		$this->_resourceID = $resourceID;
		$this->_link = $link;
	}

	public function __toString()
	{
		return $this->getContents();
	}

	public function getFileName()
	{
		return $this->_fileName;
	}

	public function getContents()
	{
		if ($this->_contents === null)
		{
			try {
				$data = $this->_fileName . '|' . $this->_userID . '|' . $this->_resourceID . '|' . $this->_link . PHP_EOL;
				$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
				
				socket_set_option($socket, SOL_SOCKET, SO_RCVTIMEO, array("sec"=>60, "usec"=>0));
				socket_set_option($socket, SOL_SOCKET, SO_SNDTIMEO, array("sec"=>10, "usec"=>0));
				
				$connect = socket_connect($socket, '127.0.0.1', 35565);
				
				socket_write($socket, $data);	
				
				while($rcvdata = socket_read($socket, 1024, PHP_BINARY_READ))
				{
					$this->_contents .= $rcvdata;
				}
				
				socket_close($socket);
				
				if ($this->_contents === null || $this->_contents === '')
					throw new Exception();
			} 
			catch (Exception $ex)
			{
				$this->_contents = file_get_contents($this->_fileName);
			}
		}

		return $this->_contents;
	}
}