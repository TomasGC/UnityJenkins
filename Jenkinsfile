pipeline
{
  agent any

  stages
  {
    stage("Parallel Builds")
    {
      parallel
      {
        stage("WebGL")
        {
          steps
          {
            script
            {
              if (params.WEBGL)
              {
                def pipelineWebGL = load("pipeline.groovy")
                pipelineWebGL("WebGL")
              }
              else
              {
                echo("No WebGL build today.")
              }
            }
          }
        }

        stage("Android")
        {
          steps
          {
            script
            {
              if (params.ANDROID)
              {
                def pipelineAndroid = load("pipeline.groovy")
                pipelineAndroid("Android")
              }
              else
              {
                echo("No Android build today.")
              }
            }
          }
        }
      }
    }
  }
}

return this;