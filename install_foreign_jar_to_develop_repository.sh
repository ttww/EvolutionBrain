#!/bin/sh
#
# This script is used for adding Pixmed specific jars to the develop repository.
# It needs to be run each time the develop repository is cleaned (which should never happens :-) )
#
# Updates of the Pixmed jars itself should be made with "mvn depoly". (See pom.xml)
#
# 16.03.2011	tw	new
#
#
groupId=com.EvolutionBrain

libs=(
	jogl.all,lib/jogl.all.jar,unknown-SNAPSHOT
	gluegen-rt,lib/gluegen-rt.jar,unknown-SNAPSHOT
	jocl,lib/jocl.jar,unknown-SNAPSHOT
	nativewindow.all,lib/nativewindow.all.jar,unknown-SNAPSHOT
	jmdns,lib/jmdns.jar,unknown-SNAPSHOT
	jogl-native,lib/,unknown-SNAPSHOT
)


url=dav:http://develop.spontech-spine.com/maven


for i in ${libs[@]}
do
	la=(${i//,/ })
	artifactId=${la[0]}
	jar=${la[1]}
	version=${la[2]}

	echo "Install	groupId=$groupId	artifactId=$artifactId	version=$version	jar=$jar"

	echo
	read i
	echo

	mvn -f NOT_USES_POM deploy:deploy-file 				\
	-DgroupId=$groupId				\
	-DartifactId=$artifactId			\
	-Dversion=$version				\
	-Dpackaging=jar					\
	-Dfile=$jar					\
	-DrepositoryId=spontech-maven-repository 	\
	-Durl=$url

	echo
	echo "done<cr>"
	read i
done

