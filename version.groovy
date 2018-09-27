def call(String projectPath)
{
	echo("[VERSION]")

	def utils = load("utils.groovy")

	dir(projectPath)
	{
        if (env.HAS_BEEN_UPGRADED != "true")
		{
            env.HAS_BEEN_UPGRADED = "true"

            String versionFilePath = sprintf("%1s/Assets/Embed/Resources/Core/Engine.prefab", projectPath)
            utils.GetVersionFromPrefab(versionFilePath)
			utils.UpgradeVersion(projectPath)
		}
	}
}

return this;

