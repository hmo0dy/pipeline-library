// vars/containerBuildPushGeneric.groovy
def call(String imageName, String imageTag = env.BUILD_NUMBER, String gcpProject = "core-workshop", Closure body) {
  def dockerReg = "gcr.io/${gcpProject}"
  def label = "kaniko-${repoOwner}"
  def podYaml = libraryResource 'podtemplates/kaniko.yml'
  def customBuildArg = ""
  def buildModeArg = ""
  podTemplate(name: 'kaniko', inheritFrom: 'default-jnlp', label: label, yaml: podYaml, podRetention: always(), idleMinutes: 30) {
    node(label) {
      body()
      try {
        env.VERSION = readFile 'version.txt'
        env.VERSION = env.VERSION.trim()
        env.VERSION = "${env.VERSION}-${BUILD_NUMBER}"
        imageTag = env.VERSION
      } catch(e) {}
      if(env.EVENT_PUSH_IMAGE_TAG) {
        customBuildArg = "--build-arg BASE_IMAGE=${env.EVENT_PUSH_IMAGE_NAME}:${env.EVENT_PUSH_IMAGE_TAG}"
      }
      if(env.BRANCH_NAME != "main") {
        buildModeArg = "--build-arg BUILD_MODE=build:dev" 
      }
      imageName = imageName.toLowerCase()
      container(name: 'kaniko', shell: '/busybox/sh') {
        withEnv(['PATH+EXTRA=/busybox:/kaniko']) {
          sh label: "container build and push", script: """#!/busybox/sh
            /kaniko/executor -f ${pwd()}/Dockerfile -c ${pwd()} ${buildModeArg} ${customBuildArg} --build-arg buildNumber=${BUILD_NUMBER} --build-arg commitAuthor='${COMMIT_AUTHOR}' --build-arg shortCommit=${env.SHORT_COMMIT} --cache=true -d ${dockerReg}/${imageName}:${imageTag}
          """
        }
      }
    }
  }
}
