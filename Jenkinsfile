library "pipeline-library@$BRANCH_NAME"
def gitHubCredId = 'field-workshops-github-app'
pipeline {
  agent none
  options {
    timeout(time: 20, unit: 'MINUTES') 
    disableConcurrentBuilds()
  }
  stages {
    stage('GitHub Tests') {
      agent {label 'default-jnlp' }
      stages {
        stage('GitHub Setup') {
          steps {
            withCredentials([usernamePassword(credentialsId: "${gitHubCredId}",
                usernameVariable: 'GITHUB_APP',
                passwordVariable: 'GITHUB_ACCESS_TOKEN')]) {
              sh(script: """
                curl -H 'Accept: application/vnd.github.antiope-preview+json' \
                     -H 'authorization: Bearer ${GITHUB_ACCESS_TOKEN}' \
                     -H "Accept: application/vnd.github.baptiste-preview+json" \
                     https://api.github.com/repos/cloudbees-days/simple-java-maven-app/generate \
                     --data '{"owner":"cloudbees-days","name":"${BUILD_TAG}"}'
               """)
              
              waitUntil {
                script {
                  def status = sh (script: """
                    curl -s -o /dev/null -w '%{http_code}' \
                      -H 'authorization: Bearer ${GITHUB_ACCESS_TOKEN}' \
                      -H 'Accept: application/vnd.github.baptiste-preview+json' \
                      https://api.github.com/repos/cloudbees-days/${BUILD_TAG}/git/ref/heads/master
                    """, returnStdout: true)
                  echo "after creating ${BUILD_TAG} repo - returned status: ${status}"
                  return (status=="200")
                }
              }
              
              sh(script: """
                mkdir -p pipeline-library-test
                cd pipeline-library-test
                git init
                git config user.email "cbci.bot@workshop.cb-sa.io"
                git config user.name "CloudBees CI Bot"
                git remote add origin https://x-access-token:${GITHUB_ACCESS_TOKEN}@github.com/cloudbees-days/${BUILD_TAG}.git
                git pull origin master
                git fetch
                git checkout -B test-branch
                cp example.cloudbees-ci.yml cloudbees-ci.yml
                git add cloudbees-ci.yml
                git commit -a -m 'adding marker file'
                git push -u origin test-branch
                
                echo "create pull request"
                curl -H 'Accept: application/vnd.github.antiope-preview+json' \
                     -H 'authorization: Bearer ${GITHUB_ACCESS_TOKEN}' \
                     --data '{"title":"add marker file","head":"test-branch","base":"master"}' \
                     https://api.github.com/repos/cloudbees-days/${BUILD_TAG}/pulls
              """)
            }
            script {
              def commentId = gitHubComment(message: "test pr comment", credId: gitHubCredId, issueId: 1, repoOwner: 'cloudbees-days', repo: BUILD_TAG)
              withCredentials([usernamePassword(credentialsId: "${gitHubCredId}",
                  usernameVariable: 'GITHUB_APP',
                  passwordVariable: 'GITHUB_ACCESS_TOKEN')]) {
                def actualCommentBody =  sh(script: """
                    curl \
                      -H "Accept: application/vnd.github.v3+json" \
                      -H 'authorization: Bearer ${GITHUB_ACCESS_TOKEN}' \
                      https://api.github.com/repos/cloudbees-days/${BUILD_TAG}/issues/comments/${commentId} \
                    | jq -r '.body' | tr -d '\n' 
                  """, returnStdout: true)
                echo "actualCommentBody: ${actualCommentBody}"
                if(!actualCommentBody.equals("test pr comment")) {
                  error "Failed PR Comment Test"
                }
              }
            }
          }
        }
        stage('Repo Files') {
          steps {
            dir('pipeline-library-test') {
              customYamlProps()
            }
            script {
              if(!lineCoverage.equals("100")) {
                error "Failed customYamlProps test" 
              }
            }
          }
        }
      }
    }
  }
  post {
    always {
      node('default-jnlp') {
        withCredentials([usernamePassword(credentialsId: "${gitHubCredId}",
            usernameVariable: 'GITHUB_APP',
            passwordVariable: 'GITHUB_ACCESS_TOKEN')]) {
          sh(script: """
            curl \
              -X DELETE \
              -H "Accept: application/vnd.github.v3+json" \
              -H 'authorization: Bearer ${GITHUB_ACCESS_TOKEN}' \
              https://api.github.com/repos/cloudbees-days/${BUILD_TAG}
          """)
        }
      }
    }
  }
} 
