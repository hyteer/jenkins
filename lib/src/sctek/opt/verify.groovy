def testVerify() {
        stage ('Verify'){
            try {
                input id: 'Deploy', message: 'Is Blue node fine? Proceed with Green node deployment?', ok: 'Deploy!'
                echo "passed..."
            } catch (error) {
                echo "there is an error..."
            }
        }
    }
