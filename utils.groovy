def MergeFromUp(String projectPath) // Probably to change.
{
    echo("[UTILS MERGEFROMUP]")

    dir(projectPath)
    {
        if(env.BRANCH_NAME == "Staging")
        {
            sh("git merge remotes/origin/Live")
        }
        else if(env.BRANCH_NAME == "Development")
        {
            sh("git merge remotes/origin/Staging")
        }
    }
}

def CheckBranchExists(String projectPath, String branchName)
{
    echo("[UTILS CHECKBRANCHEXISTS]")

	dir(projectPath)
    {
		String extractedBranch = sh(returnStdout: true, script: "git branch --list ${branchName}")

		if (extractedBranch.contains(branchName) )
        {
			sh("git branch -D ${branchName}")
		}
	}
}

def UpgradeVersion(String projectPath)
{
    echo("[UTILS UPGRADEVERSION]")

    dir(projectPath)
    {
        sh("git add Assets/Embed/Resources/Core/Engine.prefab")
        sh("git commit -m '[VERSION] ${env.BUILD_VERSION}'")
        sh("git push")
        sh("git tag -a '${env.BUILD_VERSION}' -m '[VERSION] ${env.BUILD_VERSION}'")
        sh("git push --tags")
    }
}

def CleanRepo(String projectPath)
{
    echo("[UTILS CLEANREPO]")

    dir(projectPath)
    {
        sh("git reset --hard")
        sh("git clean -f -d")

        //sh("git checkout -b Default || echo 0")
        sh("git checkout Default")
        CheckBranchExists(projectPath, env.BRANCH_NAME)
    }
}

def UpdateRepo(String projectPath)
{
    echo("[UTILS UPDATEREPO]")
    
    dir(projectPath)
    {
        sh("git fetch --prune --tags")
        sh("git pull")
        sh("git pull --tags --recurse-submodules")
    }
}

def Deploy(String localPath, String serverPath) // Probably to change.
{
    String password = "sshpass -p 'password'"
    String serverAddress = "username@defaultAddress"

    switch (env.SERVER_NAME)
    {
        case "environment1":
            serverAddress = "username@environment1Address"
            break

        case "environment2":
            serverAddress = "username@environment2Address"
            break

        case "environment3":
            serverAddress = "username@environment3Address"
            break

        case "staging":
            serverAddress = "username@stagingAddress"
            break
        
        case "live":
            serverAddress = "username@liveAddress"
            break
    }

    dir(localPath)
    {
        sh("${password} scp -r . ${serverAddress}:${serverPath}")
    }

    sh("${password} ssh ${serverAddress} 'chmod -R 655 ${serverPath}'")
}

def CheckFolderExists(String folderPath)
{
    def folderExists = false;
    dir(folderPath)
    {
        folderExists = fileExists(".")
        return folderExists;
    }

    return folderExists;
}

def GetVersionFromPrefab(String versionFilePath) // Probably to change.
{
    String major = sh(returnStdout: true, script: "grep 'majorVersion:' ${versionFilePath} | sed 's/^.*: //'")
    String minor = sh(returnStdout: true, script: "grep 'minorVersion:' ${versionFilePath} | sed 's/^.*: //'")
    String code = sh(returnStdout: true, script: "grep 'versionCode:' ${versionFilePath} | sed 's/^.*: //'")

    env.BUILD_VERSION = sprintf("v%1s.%2s(%3s)", major, minor, code).trim().replaceAll("\\s","")
}

def GetCommitList(String projectPath)
{
    dir(projectPath)
    {
        String lastTagID = sh(returnStdout: true, script: "git describe --abbrev=0 --tags").trim().replaceAll("\\s","")
		String lastTagCommitID = sh(returnStdout: true, script: "git show-ref -s '${lastTagID}'").trim().replaceAll("\\s","")
		String lastCommitID = sh(returnStdout: true, script: "git rev-parse HEAD").trim().replaceAll("\\s","")

		String commitList = sh(returnStdout: true, script: "git log --pretty=format:'%s by %an' ${lastTagCommitID}..${lastCommitID} --no-merges")

        sh("echo '${commitList}' > ${env.COMMITS_LIST_FILE_PATH}")
    }
}

return this;
