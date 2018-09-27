def call(String projectPath, String platform)
{
	echo("[BUILD]")
	
	dir(projectPath)
	{
		String upgradeType = '-' + env.UPGRADE_VERSION
		String serverType = '-' + env.SERVER_NAME
		String platformType = '-' + platform
		
		String unityPath = "PathToUnity"
		String unityFunction = "PathToUnityFunction"
		String assetBundles = ''
				
		if (params.HAS_ASSETBUNDLES)
		{
			assetBundles = "-ab"
		}
		
		sh("${unityPath} -quit -batchmode -projectPath ${projectPath} -executeMethod ${unityFunction} ${upgradeType} ${platformType} ${serverType} ${assetBundles}")
	}
}

return this;
