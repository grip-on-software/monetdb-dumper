pipeline {
    agent { label 'ant' }

    environment {
        BUILD_TARGET = 'default'
        BUILD_FILE = 'build.xml'
        SCANNER_HOME = tool name: 'SonarQube Scanner 3', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
    }

    options {
        gitLabConnection('gitlab')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }
    triggers {
        gitlab(triggerOnPush: true, triggerOnMergeRequest: true, branchFilterType: 'All')
        cron('H H * * 5')
    }

    post {
        success {
            archiveArtifacts artifacts: 'dist/**', excludes: 'dist/javadoc/**', onlyIfSuccessful: true
            publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: 'dist/javadoc', reportFiles: 'index.html', reportName: 'Javadoc', reportTitles: ''])
            updateGitlabCommitStatus name: env.JOB_NAME, state: 'success'
        }
        failure {
            updateGitlabCommitStatus name: env.JOB_NAME, state: 'failed'
        }
    }

    stages {
        stage('Build') {
            steps {
                updateGitlabCommitStatus name: env.JOB_NAME, state: 'running'
                withCredentials([file(credentialsId: 'monetdb-dumper-properties', variable: 'BUILD_PROPERTIES')]) {
                    withAnt(installation: 'Ant 1.10.1', jdk: 'JDK 8') {
                        sh "ant -buildfile $BUILD_FILE -propertyfile $BUILD_PROPERTIES $BUILD_TARGET"
                    }
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '${SCANNER_HOME}/bin/sonar-scanner -Dsonar.projectKey=monetdb-dumper:$BRANCH_NAME -Dsonar.projectName="MonetDB dumper $BRANCH_NAME"'
                }
            }
        }
    }
}
