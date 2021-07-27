// vars/blogBackendKubeDeploy.groovy
def call(repoName, repoOwner, dockerRegistryDomain, deploymentDomain, gcpProject = "core-workshop", Closure body) {
    def label = "kubectl"
    def podYaml = libraryResource 'podtemplates/kubectl.yml'
    def deployYaml = libraryResource 'k8s/basicDeploy.yml'
    
    podTemplate(name: 'kubectl', label: label, yaml: podYaml) {
      node(label) {
        body()
        repoName = repoName.toLowerCase()
        repoOwner = repoOwner.toLowerCase()
        sh("sed -i 's#REPLACE_IMAGE#${dockerRegistryDomain}/${repoOwner}/${repoName}:${env.VERSION}#' .kubernetes/backend.yaml")
        sh("sed -i 's#REPLACE_HOSTNAME#staging.${repoName}.${deploymentDomain}#' .kubernetes/backend.yaml")
        container("kubectl") {
          sh "cat .kubernetes/postgres.yaml"
          sh "kubectl apply -f .kubernetes/postgres.yaml"
          sh "cat .kubernetes/backend.yaml"
          sh "kubectl apply -f .kubernetes/backend.yaml"
          sh "echo 'deployed to http://staging.${repoName}.${deploymentDomain}'"
        }
      }
    }
}
