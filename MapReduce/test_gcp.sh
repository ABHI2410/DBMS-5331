if [ "$1" == "execute" ]; then
    javac -classpath $HADOOP_CLASSPATH -d './classes' './src/IMDb.java'
    jar cf ./jar/imdb.jar -C ./classes/ .
    hdfs dfs -put ./inputs/Spring2024-Project3-IMDbData.txt /
    hadoop jar ./jar/imdb.jar IMDb /Spring2024-Project3-IMDbData.txt /output
    hdfs dfs -get /output ./
 
elif [ "$1" == "clear" ]; then
    hdfs dfs -rm /Spring2024-Project3-IMDbData.txt
    hdfs dfs -rm -R /output
    rm ./classes/*.class
    rm ./jar/*.jar
    rm -R ./output/*
else
    echo "Invalid argument. Please specify 'execute' or 'clear'."
    exit 1
fi
 
exit 0
