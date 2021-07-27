def call(String gitHubOrg, String gitHubRepo, String deployUrl, String status = 'in_progress', String credentialId = env.credId) {        
  withCredentials([usernamePassword(credentialsId: "${credentialId}", usernameVariable: 'GITHUB_APP', passwordVariable: 'GITHUB_ACCESS_TOKEN')]) {
    sh(script: """
      curl \
        -X POST \
        -H 'authorization: Bearer ${GITHUB_ACCESS_TOKEN}' \
        -H 'Accept: application/vnd.github.antiope-preview+json' \
        -H "Accept: application/vnd.github.v3+json" \
        -H "Accept: application/vnd.github.ant-man-preview+json"  \
        -H "Accept: application/vnd.github.flash-preview+json" \
        https://api.github.com/repos/${gitHubOrg}/${gitHubRepo}/deployments/${env.GITHUB_DEPLOYMENT_ID}/statuses \
        --data '{"state":"${status}","environment_url":"${deployUrl}","log_url":"${BUILD_URL}"}'
    """)
   
  }
} 
