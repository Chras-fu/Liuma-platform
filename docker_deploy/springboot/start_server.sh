#!/bin/bash
i=0
while ! nc -v -z mysql 3306 ; do
	let times=$i+1
    echo "current times:  ${times} 次"
    sleep 8
done

echo "\nThe mysql has been deployed!!!!"

java -jar -Dspring.config.location=/home/application.properties /home/LiuMa-1.0.3.jar

