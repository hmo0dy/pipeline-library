def call(Map config) {
  echo "gitHubComment"
  withCredentials([usernamePassword(credentialsId: "${config.credId}", usernameVariable: 'GITHUB_APP', passwordVariable: 'GITHUB_ACCESS_TOKEN')]) {
    def commentId = sh(script: """
      curl -s -H "Authorization: Bearer ${GITHUB_ACCESS_TOKEN}" \
        -X POST -d '{"body": "${config.message}"}' \
        "https://api.github.com/repos/${config.repoOwner}/${config.repo}/issues/${config.issueId}/comments" \
      | jq -r '.id' | tr -d '\n' 
    """, returnStdout: true)
    echo "gitHubComment commentId: ${commentId}"
    return commentId
  }
}        
