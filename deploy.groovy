def call(String storagePath, String platform)
{
	echo("[DEPLOY STARTED]")

	def utils = load("utils.groovy")

    if (params.DEPLOY)
    {
        echo("[DEPLOY " + platform + " BUILD]")

        String storageBuildPath = storagePath + "/" + env.BUILD_VERSION + "/Builds/" + env.BRANCH_NAME + "-" + env.SERVER_NAME

        switch (platform.toLowerCase())
        {
            case "webgl":
                utils.Deploy(storageBuildPath, "/var/www/html/Builds") // Probably to change
                break
        }
    }

    if (params.HAS_ASSETBUNDLES)
    {
        echo("[DEPLOY ASSET BUNDLES]")

        String storageAssetBundlePath = storagePath + "/" + env.BUILD_VERSION + "/AssetBundles/" + env.BRANCH_NAME + "-" + env.SERVER_NAME
        String serverAssetBundlesPath = "/var/www/html/Bundles/" + platform // Probably to change.

        utils.Deploy(storageAssetBundlePath, serverAssetBundlesPath)
    }

	echo("[DEPLOY ENDED]")
}

return this;
