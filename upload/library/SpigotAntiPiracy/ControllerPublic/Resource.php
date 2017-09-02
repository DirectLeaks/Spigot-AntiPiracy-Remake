<?php
class SpigotAntiPiracy_ControllerPublic_Resource extends XFCP_SpigotAntiPiracy_ControllerPublic_Resource
{
	// Which categories should be injected?
	
	// Example: array(1, 2, 3, 4, 5, 6, 7, 8, 9);
	public static $categoryId = array(1);
	
	
	// This is where the blacklisted IDs will be are queried by the plugin that has been injected to see if a user has had
	// their access revoked to using any plugins.

	// http://www.example.com/api/resource.php?user_id=%%__USER__%%&resource=%%__RESOURCE__%%&nonce_id=%%__NONCE__%% -> If this page returns the user id, the plugin will throw a runtime exception.
	public static $link = 'http://yourdomain.com/api/resource.php?user_id=%%__USER__%%&resource=%%__RESOURCE__%%&nonce_id=%%__NONCE__%%';
	
	public function actionDownload()
	{
		$fetchOptions = array(
			'watchUserId' => XenForo_Visitor::getUserId()
		);
		list($resource, $category) = $this->_getResourceHelper()->assertResourceValidAndViewable(null, $fetchOptions);

		if ($resource['is_fileless'])
		{
			return $this->responseError(new XenForo_Phrase('fileless_resources_cannot_be_downloaded'));
		}

		$versionModel = $this->_getVersionModel();

		$versionId = $this->_input->filterSingle('version', XenForo_Input::UINT);
		$version = $versionModel->getVersionById($versionId, array(
			'join' => XenResource_Model_Version::FETCH_FILE
		));
		if (!$version || $version['resource_id'] != $resource['resource_id'])
		{
			return $this->responseNoPermission();
		}

		if (!$versionModel->canDownloadVersion($version, $resource, $category, $error))
		{
			throw $this->getErrorOrNoPermissionResponseException($error);
		}

		if ($version['download_url'])
		{
			if (XenForo_Helper_String::censorString($version['download_url']) != $version['download_url'])
			{
				return $this->responseError(new XenForo_Phrase('resource_download_is_not_available_try_another'), 403);
			}
		}

		// Watch for download events
		$visitor = XenForo_Visitor::getInstance();
		$this->_getResourceWatchModel()->setResourceWatchStateWithUserDefault(
			$visitor['user_id'], $resource['resource_id'], $visitor['default_watch_state']
		);

		$this->_getVersionModel()->logVersionDownload($version, XenForo_Visitor::getUserId());
		
		if ($version['download_url'])
		{
			return $this->responseRedirect(
				XenForo_ControllerResponse_Redirect::RESOURCE_CANONICAL,
				$version['download_url']
			);
		}
		else
		{
			$this->_request->setParam('attachment_id', $version['attachment_id']);
			$this->_request->setParam('no_canonical', 1);
			
			if (in_array($resource['resource_category_id'], self::$categoryId))
			{
				$this->_request->setParam('inject', 1);
			}
			else
			{
				$this->_request->setParam('inject', 0);
			}
			
			$this->_request->setParam('user_id', $visitor['user_id']);
			$this->_request->setParam('resource_id', $resource['resource_id']);
			$this->_request->setParam('link', self::$link);

			return $this->responseReroute('XenForo_ControllerPublic_Attachment', 'index');
		}
	}
}