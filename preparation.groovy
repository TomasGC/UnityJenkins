def call(String projectPath)
{
	echo("[PREPARATION]")

    def utils = load("utils.groovy")

	dir(projectPath)
	{
		if (env.CUSTOM_BRANCH_NAME != "none")
		{
			env.BRANCH_NAME = env.CUSTOM_BRANCH_NAME
		}

		utils.CleanRepo(projectPath)
        sh("git checkout -b ${env.BRANCH_NAME} origin/${env.BRANCH_NAME} || echo 0")
        //sh("git branch -D Default")
        utils.UpdateRepo(projectPath)

		GetCommitList(projectPath)

		utils.MergeFromUp(projectPath)
	}
}

return this;
