def call(String platform)
{
    echo("[PIPELINE]")

	env.BUILD_VERSION = ""
	env.COMMIT_LIST = "No changes."


	def slackNotifications = load("slackNotifications.groovy")

	String projectPath = "YourProjectPath"
	String buildPath = sprintf("Build/%1s/game_client", platform)
	String assetBundlesPath = sprintf("AssetBundles/%1s", platform)
	String storagePath = "YourStoragePath"
	String status = "Succeed"
	String stageTitlePrefix = sprintf("[%1s]: ", platform)

    if(env.UPGRADE_VERSION == "none")
    {
        env.HAS_BEEN_UPGRADED = "true"
    }

	try
	{
		stage(sprintf(stageTitlePrefix + "Preparing")
		{
			slackNotifications.Started()
			def preparation = load("preparation.groovy")
			preparation(projectPath)
		}
		
		stage(stageTitlePrefix + "Building")
		{
			def build = load("build.groovy")
			build(projectPath, platform.toLowerCase())
		}
		
		stage(stageTitlePrefix + "Versionning")
		{
			def version = load("version.groovy")
			version(projectPath)
		}
		
		stage(stageTitlePrefix + "Archiving")
		{
			def archive = load("archive.groovy")
			archive(projectPath, buildPath, assetBundlesPath, storagePath, platform)
		}
		
		stage(stageTitlePrefix + "Deploying")
		{
			def deploy = load("deploy.groovy")
			deploy(storagePath, platform)
		}
		
		stage(stageTitlePrefix + "Success")
		{
			slackNotifications.Succeed()
		}
	}
	catch (Exception e) // If there was an exception thrown, the build failed
	{
		stage(stageTitlePrefix + "Failure")
		{
			status = "Failed"
			slackNotifications.Failed()
		}
	}

	stage(stageTitlePrefix + "Cleaning")
	{
		def clean = load("clean.groovy")
		clean(projectPath)

		if (status == "Failed")
		{
            currentBuild.result = 'FAILURE'
            sh("exit 1")
        }
	}
}

return this;
