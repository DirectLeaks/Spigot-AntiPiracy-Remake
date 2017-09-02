<?php

class SpigotAntiPiracy_Listener
{
        public static function loadClassController($class, array &$extend)
        {
                if ($class == 'XenResource_ControllerPublic_Resource')
                {
                        $extend[] = 'SpigotAntiPiracy_ControllerPublic_Resource';
                } else if ($class == 'XenForo_ControllerPublic_Attachment')
                {
                        $extend[] = 'SpigotAntiPiracy_ControllerPublic_Attachment';
                } else if ($class == 'XenForo_ViewPublic_Attachment_View')
                {
                        $extend[] = 'SpigotAntiPiracy_ViewPublic_Attachment_View';
                }
		}
	
}