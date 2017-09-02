<?php

class SpigotAntiPiracy_ControllerPublic_Attachment extends XFCP_SpigotAntiPiracy_ControllerPublic_Attachment
{
	public function actionIndex()
	{
		$attachmentId = $this->_input->filterSingle('attachment_id', XenForo_Input::UINT);
		$attachment = $this->_getAttachmentOrError($attachmentId);

		$tempHash = $this->_input->filterSingle('temp_hash', XenForo_Input::STRING);

		$attachmentModel = $this->_getAttachmentModel();
		
		$userID = $this->_input->filterSingle('user_id', XenForo_Input::UINT);
		$resourceID = $this->_input->filterSingle('resource_id', XenForo_Input::UINT);
		$inject = $this->_input->filterSingle('inject', XenForo_Input::UINT);
		$link = $this->_input->filterSingle('link', XenForo_Input::STRING);
		
		if (!$attachmentModel->canViewAttachment($attachment, $tempHash))
		{
			return $this->responseNoPermission();
		}

		$filePath = $attachmentModel->getAttachmentDataFilePath($attachment);
		if (!file_exists($filePath) || !is_readable($filePath))
		{
			return $this->responseError(new XenForo_Phrase('attachment_cannot_be_shown_at_this_time'));
		}

		if (!$this->_input->filterSingle('no_canonical', XenForo_Input::UINT))
		{
			$this->canonicalizeRequestUrl(
				XenForo_Link::buildPublicLink('attachments', $attachment)
			);
		}

		$eTag = $this->_request->getServer('HTTP_IF_NONE_MATCH');
		$eTagExpected =  '"' . $attachment['attach_date'] . '"';
		if ($eTag && ($eTag == $eTagExpected || $eTag == "W/$eTagExpected"))
		{
			$this->_routeMatch->setResponseType('raw');
			return $this->responseView('XenForo_ViewPublic_Attachment_View304');
		}

		$attachmentModel->logAttachmentView($attachmentId);

		$this->_routeMatch->setResponseType('raw');

		$viewParams = array(
			'attachment' => $attachment,
			'attachmentFile' => $filePath,
			'user_id' => $userID,
			'resource_id' => $resourceID,
			'inject' => $inject,
			'link' => $link
		);

		return $this->responseView('XenForo_ViewPublic_Attachment_View', '', $viewParams);
	}
}