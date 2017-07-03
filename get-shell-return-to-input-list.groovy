node ('node-146') {
   echo 'Hello World'
   stage ('Choose') {
       echo "please choose a choice..."
       def myStr,myList
	   def choicesMap = ''

       dir ('/root') {
           sh "pwd && ls"
           myStr = sh returnStdout: true, script: "ls"
           myList = myStr.split('\n')
       }
       echo "List: ${myList}"

       for (int i = 0; i < myList.size(); ++i) {
           //echo "choice: ${myList[i]}"
           if (i+1 < myList.size()) {
               choicesMap += "${myList[i]}\n"
           }else{
               choicesMap += myList[i]
           }
       }
       echo "my choices: ${choicesMap}"

       def CHOICE = input message: '请确认', ok: 'OK', parameters: [choice(choices: choicesMap, description: '', name: 'choice')]
       echo "Your choice: ${CHOICE}"

   }
}
