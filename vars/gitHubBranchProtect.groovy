def call(String gitHubOrg, String gitHubRepo, String contexts = '["pmd","checkstyle"]', String credentialId = 'cloudbees-ci-workshop-github-app') {        
  withCredentials([usernamePassword(credentialsId: "${credentialId}", usernameVariable: 'GITHUB_APP', passwordVariable: 'GITHUB_ACCESS_TOKEN')]) {
    sh """
      curl \
        -X PUT \
        -H 'authorization: Bearer ${GITHUB_ACCESS_TOKEN}' \
        -H 'Accept: application/vnd.github.antiope-preview+json' \
        "https://api.github.com/repos/${gitHubOrg}/${gitHubRepo}/branches/main/protection" \
        --data '{"required_status_checks":{"strict":false,"contexts":${contexts}},"enforce_admins":null,"required_pull_request_reviews":null,"restrictions":null}'
    """
  }
} 
