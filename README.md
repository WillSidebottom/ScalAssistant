#ScalAssistant
ScalAssistant (Scala + Assistant) is a command line assistant written in Scala using Akka. The goal for this project is to have an assistant which can answer questions and perfrom simple tasks right on the command line. It should be noted that the idea and implementation of this project was influenced by a similar project from Alvin Alexander [Akkazon Ekko](https://github.com/alvinj/AkkazonEkko/blob/master/README.md).

##Overview
Currently, ScalAssistant can handle the following:
- Respond to simple greetings
- Retrieve the local date and time
- Retrieve weather information for a specified location (US only) (See API keys below)
- Interact with Twitter(See API Keys below)

##Future
Eventually, ScalAssistant should be able to handle the following:
- Get news headlines
- Get stock information
- Google search
- Use Wikipedia

##Help
After setting up the project and running it using SBT, you can type help in the command line to get a better sense of what the program can do. For example, you can type "help weather" to see what format to use when retrieving the weather from a specific location.

##API Keys
To interact with Twitter and retrieve weather data, you will need to provide the app with the appropriate api keys. Retrieving the weather is done using the WeatherUnderground API. You will need to create an account on their website to have them provide an API key for you. The same goes for Twitter. You will need to sign up and create an application in order to get keys for OAuth. This app looks for the keys in the src/main/resources directory.