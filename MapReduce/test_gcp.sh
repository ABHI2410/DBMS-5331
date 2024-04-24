if [ "$1" == "execute" ]; then
    javac -classpath $HADOOP_CLASSPATH -d './classes' './src/IMDb.java'
    jar cf ./jar/imdb.jar -C ./classes/ .
    hdfs dfs -put ./inputs/Spring2024-Project3-IMDbData.txt /
    hdfs dfs -mkdir /project3/
    hadoop jar ./jar/imdb.jar IMDb /Spring2024-Project3-IMDbData.txt /output
    CURRENT_DIR=$(pwd)
    $HADOOP_HOME/bin/hdfs dfs -get /output $(echo $CURRENT_DIR)/output/
 
elif [ "$1" == "clear" ]; then
    hdfs dfs -rm /Spring2024-Project3-IMDbData.txt
    hdfs dfs -rm -R /output
    rm ./classes/*.class
    rm ./jar/*.jar
    rm ./output/*
else
    echo "Invalid argument. Please specify 'execute' or 'clear'."
    exit 1
fi
 
exit 0