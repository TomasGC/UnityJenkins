def call(String projectPath)
{
	echo("[CLEAN]")

	def utils = load("utils.groovy")

	dir(projectPath)
	{
		utils.CleanRepo(projectPath)
	}
}

return this;

