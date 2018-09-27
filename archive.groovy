def call(String projectPath, String buildPath, String assetBundlesPath, String storagePath, String platform)
{
	echo("[ARCHIVE]")

    def utils = load("utils.groovy")

    String versionFilePath = sprintf("%1s/Assets/Embed/Resources/Core/Engine.prefab", projectPath)
    utils.GetVersionFromPrefab(versionFilePath)

    String storageVersionFolderPath = storagePath + "/" + env.BUILD_VERSION
    String storageBuildsPath = storageVersionFolderPath + "/Builds"
    String storageAssetBundlesPath = storageVersionFolderPath + "/AssetBundles"

    if (!utils.CheckFolderExists(storageVersionFolderPath))
    {
        sh("mkdir -p '${storageBuildsPath}'")
        sh("mkdir -p '${storageAssetBundlesPath}'")
    }

    if (platform.toLowerCase() == "android")
    {
        dir(projectPath)
        {
            String apk = buildPath + ".apk"
            if (fileExists(apk))
            {
                def storageBuildPath = storageBuildsPath + "/" + env.BRANCH_NAME + "-" + env.SERVER_NAME + ".apk"
                sh("mv '${apk}' '${storageBuildPath}'")
            }
        }
    }
    else
    {
        String localBuildPath = projectPath + "/" + buildPath + "/Build"
        if(utils.CheckFolderExists(localBuildPath))
        {
            def storageBuildPath = storageBuildsPath + "/"  + env.BRANCH_NAME + "-" + env.SERVER_NAME
            if(!utils.CheckFolderExists(serverBuildPath))
            {
                sh("mkdir -p '${storageBuildPath}'")
            }

            dir(localBuildPath)
            {
                sh("mv * '${storageBuildPath}'")
            }
        }
    }

    if (params.HAS_ASSETBUNDLES)
    {
        String localAssetBundlesPath = projectPath + "/" + assetBundlesPath
        if(utils.CheckFolderExists(localAssetBundlesPath))
        {
            String storageAssetBundlePath = storageAssetBundlesPath + "/" + env.BRANCH_NAME + "-" + env.SERVER_NAME
            if(!utils.CheckFolderExists(serverAssetBundlePath))
            {
                sh("mkdir -p '${storageAssetBundlePath}'")
            }

            dir(localAssetBundlesPath)
            {
                sh("mv * '${storageAssetBundlePath}'")
            }
        }
    }
    dir(projectPath)
    {
        env.COMMITS_LIST_FILE_PATH = env.BRANCH_NAME + "-" + env.SERVER_NAME + "-commits_list.txt"
        if (fileExists(env.COMMITS_LIST_FILE_PATH))
        {
            sh("mv -f ${env.COMMITS_LIST_FILE_PATH} '${storagePath}/${env.BUILD_VERSION}'")

            // Here we update the address in order to use it with slack.
            env.COMMITS_LIST_FILE_PATH = "file://" + storagePath +  "/" + env.BUILD_VERSION + "/" + env.BRANCH_NAME + "-" + env.SERVER_NAME + "-commits_list.txt"
        }
    }
}

return this;
