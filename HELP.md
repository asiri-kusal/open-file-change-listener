# Getting Started

#Get Property
METHOD: GET
http://localhost:8080/get-property-content/<your property key> 
example : http://localhost:8080/get-property-content/TEST_KEY

#Get any file content
METHOD: GET
/get-file-content

#Example code
 @Autowired
 private FileWatcherConfiguration configuration;
 
 Get file content
 return type String
 
 configuration.getFileContent()
 
 Get Properties
 Return type Properties
 configuration.getConfiguration()