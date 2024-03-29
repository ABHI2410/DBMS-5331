#!/bin/bash

if [ "$1" == "execute" ]; then
    sudo cp -R -fr /home/abhishek/DBMS-5331/MapReduce/ /home/hdoop/DBMS-5331/
    sudo chown -R hdoop:hdoop /home/hdoop/DBMS-5331/MapReduce
    javac -classpath $HADOOP_CLASSPATH -d './classes' './src/IMDb.java'
    jar cf ./jar/imdb.jar -C ./classes/ .
    $HADOOP_HOME/bin/hdfs dfs -put ./inputs/Spring2024-Project3-IMDbData.txt /user/project3/
    $HADOOP_HOME/bin/hadoop jar ./jar/imdb.jar IMDb /user/project3/Spring2024-Project3-IMDbData.txt /user/project3/output
    $HADOOP_HOME/bin/hdfs dfs -get /user/project3/output/part-r-00000 /home/hdoop/DBMS-5331/MapReduce/output/
    cp ./output/part-r-00000 ./output/output.txt
    rm ./output/part-r-00000
    sudo cp -R -fr /home/hdoop/DBMS-5331/MapReduce /home/abhishek/DBMS-5331/
    sudo chown -R abhishek:abhishek /home/abhishek/DBMS-5331/MapReduce
elif [ "$1" == "clear" ]; then
    $HADOOP_HOME/bin/hdfs dfs -rm /user/project3/Spring2024-Project3-IMDbData.txt 
    $HADOOP_HOME/bin/hdfs dfs -rm -R /user/project3/output
    rm ./classes/*.class
    rm ./jar/*.jar
    rm ./output/*
else 
    echo "Invalid argument. Please specify 'execute' or 'clear'."
    exit 1
fi

exit 0
