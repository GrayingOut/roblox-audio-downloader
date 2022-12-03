
# Download maven dependencies
mvn dependency:copy-dependencies

# Compile java source into bin folder
javac -cp "src/main/java;target/dependency/*" -d "bin" src/main/java/me/grayingout/App.java

# Run compiled java - with passed command line arguments
java -cp "bin;target/dependency/*" me.grayingout.App ($args -join " ")
