def call(String buildMode = "build", Closure body) {
  def label = "nodejs-${repoOwner}"
  def podYaml = libraryResource 'podtemplates/nodejs/pod.yml'
  podTemplate(name: 'nodejs', label: label, yaml: podYaml, podRetention: always(), idleMinutes: 30) {
    node(label) {
      body()
      if(BRANCH_NAME != "main") {
        buildMode = "build:dev" 
      }
      else {
        buildMode = "build"
      }
      container('nodejs') {
        sh """
          yarn install
          yarn run $buildMode
        """
        stash name: "app", includes: "dist/**,.env*,nginx.conf,Dockerfile,version.txt" 
      }
    }
  }
}
