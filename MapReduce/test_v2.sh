if [ "$1" == "execute" ]; then
 
    javac -classpath $HADOOP_CLASSPATH -d './classes' './src/IMDb.java'
    jar cf ./jar/imdb.jar -C ./classes/ .
    $HADOOP_HOME/bin/hdfs dfs -put ./inputs/Spring2024-Project3-IMDbData.txt /
    $HADOOP_HOME/bin/hadoop jar ./jar/imdb.jar IMDb /Spring2024-Project3-IMDbData.txt /output
    CURRENT_DIR=$(pwd)
    $HADOOP_HOME/bin/hdfs dfs -get /output/part-r-00000 $(echo $CURRENT_DIR)/output/
    cp ./output/part-r-00000 ./output/output.txt
    # rm ./output/part-r-00000
    cat ./output/output.txt
 
elif [ "$1" == "clear" ]; then
    $HADOOP_HOME/bin/hdfs dfs -rm /Spring2024-Project3-IMDbData.txt
    $HADOOP_HOME/bin/hdfs dfs -rm -R /output
    rm ./classes/*.class
    rm ./jar/*.jar
    rm ./output/*
else
    echo "Invalid argument. Please specify 'execute' or 'clear'."
    exit 1
fi
 
exit 0