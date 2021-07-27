def call(repoName, repoOwner, sha, targetUrl, description, context, state="success") {        
  withCredentials([usernamePassword(credentialsId: "${credId}", usernameVariable: 'USERNAME', passwordVariable: 'TOKEN')]) {
    sh """
      curl -s -H "Authorization: token ${TOKEN}" \
        -X POST -d '{"state": "${state}", "target_url": "${targetUrl}", "description": "${description}", "context": "ci/cloudbees/${context}"}' \
        "https://api.github.com/repos/${repoOwner}/${repoName}/statuses/${sha}"
    """
  }
} 
