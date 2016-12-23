#ScalAssistant
ScalAssistant (Scala + Assistant) is a command line assistant written in Scala using Akka. The goal for this project is to have an assistant which can answer questions and perfrom simple tasks right on the command line. It should be noted that the idea and implementation of this project was influenced by a similar project from Alvin Alexander [Akkazon Ekko](https://github.com/alvinj/AkkazonEkko/blob/master/README.md).

##Overview
Currently, ScalAssistant can handle the following:
- Respond to simple greetings
- Retrieve the local date and time
- Retrieve weather information for a specified location (US only)
- Interact with Twitter

##Future
Eventually, ScalAssistant should be able to handle the following:
- Get news headlines
- Get stock information
- Google search
- Use Wikipedia

##Help
After setting up the project and running it using SBT, you can type help in the command line to get a better sense of what the program can do. For example, you can type "help weather" to see what format to use when retrieving the weather from a specific location.