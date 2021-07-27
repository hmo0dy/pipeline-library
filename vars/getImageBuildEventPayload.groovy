// vars/getImageBuildEventPayload.groovy
//will get the image build payload values from a imageBuild published event and set them as environment variables
def call() {
    env.EVENT_PUSH_IMAGE_NAME = currentBuild?.getBuildCauses()[0]?.event?.name?.toString()
    env.EVENT_PUSH_IMAGE_TAG = currentBuild?.getBuildCauses()[0]?.event?.tag?.toString()
    sh "echo ${EVENT_PUSH_IMAGE_NAME}"
    sh "echo ${EVENT_PUSH_IMAGE_TAG}"
}