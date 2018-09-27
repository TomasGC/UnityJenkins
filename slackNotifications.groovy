import org.jenkinsci.plugins.builduser.BuildUser
import net.sf.json.JSONArray
import net.sf.json.JSONObject

def NotifyBuild(String buildStatus)
{
    echo "[SLACK] " + buildStatus

    String color = "#FF0000" // RED
    String title = "[%1s]: %2s - #%3s %4s"
    String user = ""
    wrap([$class: 'BuildUser']) { user = BUILD_USER }

    JSONObject attachment = new JSONObject()
    JSONArray attachments = new JSONArray()

    if (buildStatus == 'Started')
    {
        color = '#007FFF' // BLUE
        title = sprintf(title, buildStatus, env.JOB_NAME, env.BUILD_NUMBER, "")
    }
    else if (buildStatus == 'Succeed')
    {
        colorCode = '#00FF00' // GREEN
        String buildTime = "after " + currentBuild.durationString.replace(' and counting', '')
        title = sprintf(title, buildStatus, env.JOB_NAME, env.BUILD_NUMBER, buildTime)
        String text = sprintf("Build %1s from %2s is now built for %3s\n\nThe commits list is there: %4s", env.BUILD_VERSION, env.BRANCH_NAME, env.SERVER_NAME, env.COMMITS_LIST_FILE_PATH)
        attachment.put("text", text)
    }
    else
    {
        title = sprintf(title, buildStatus, env.JOB_NAME, env.BUILD_NUMBER, "")
    }

    attachment.put("fallback","Something went wrong...")
    attachment.put("title", title)
    attachment.put("title_link", env.BUILD_URL)
    attachment.put("author_name", user)

    attachments.add(attachment)

    // Send notifications
    slackSend (color: color, attachments: attachments.toString())
}

def Started()
{
    NotifyBuild('Started')
}

def Succeed()
{
    NotifyBuild('Succeed')
}

def Failed()
{
    NotifyBuild('Failed')
}

return this;
