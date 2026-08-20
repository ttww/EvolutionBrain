#!/bin/sh
#
# Script for downloding all jogl stuff and put it in the local macen repos
# Thomas Welsch  /  ttww at gmx.de
#
# 09.03.2011	tw	new
#

version=09.04.2011
groupId=com.EvolutionBrain

workspace=/Users/tw/workspace
dest=$workspace/EvolutionBrain/lib

deployUrl=dav:http://develop.spontech-spine.com/maven

# Prefer english messages, even on mac os x (jarCmd....) :-)
LANG=en_US.UTF-8
export LANG
jarCmd="jar -J-Duser.language=us -J-Duser.country=US -J-Duser.variant=US"

curDir=`pwd` 

url=http://jogamp.org/deployment/webstart
arch="linux-amd64 linux-i586 macosx-universal windows-amd64 windows-i586"

native_jars="gluegen-rt-natives jogl-natives nativewindow-natives jocl-natives joal-natives"

all_jars="jogl.all nativewindow.all gluegen-rt jocl joal"

cd $dest

# -----------------------------------------------------------------------
# FETCH   FETCH   FETCH   FETCH   FETCH   FETCH   FETCH   FETCH   FETCH  
# -----------------------------------------------------------------------


# -----------------------------------------------------------------------
# Fetching all jar files (not architecture dependend)
# -----------------------------------------------------------------------
for jar in $all_jars
do
continue
        fn=$jar.jar
        du=$url/$fn
        echo "Download $fn..."
        curl -R -# -o $fn $du 
done


# -----------------------------------------------------------------------
# Fetching all jar files with native code (architecture dependend)
# -----------------------------------------------------------------------
mkdir -p arch arch_jars
cd arch
mkdir -p `echo $arch`
cd ..

for a in $arch
do
	for n in $native_jars
	do
		fn=$n-$a.jar
		du=$url/$fn
		echo "Download $fn..	$du"
                curl -R -# -o arch_jars/$fn $du 

		cd arch/$a
		$jarCmd xvf ../../arch_jars/$fn
		rm -r META-INF
		cd ../..
	done
done

cd $curDir

# -----------------------------------------------------------------------
# MAVEN   MAVEN   MAVEN   MAVEN   MAVEN   MAVEN   MAVEN   MAVEN   MAVEN  
# -----------------------------------------------------------------------

# -----------------------------------------------------------------------
# Install all jars into the local maven repository
# -----------------------------------------------------------------------
echo
echo
echo
echo "........................................"
echo
all="gluegen-rt jmdns jocl joal jogl.all nativewindow.all"
for artifactId in $all
do
	echo $artifactId
	echo "      ----  START A ----" 
	echo mvn install:install-file -Dfile=lib/$artifactId.jar -DgroupId=$groupId -DartifactId=$artifactId -Dversion=$version -Dpackaging=jar
	mvn -f NOT_USE_POM_XML install:install-file -Dfile=lib/$artifactId.jar -DgroupId=$groupId -DartifactId=$artifactId -Dversion=$version -Dpackaging=jar
	echo "      ----  DONE A ----" 

        mvn -f NOT_USE_POM_XML deploy:deploy-file               \
        	-DgroupId=$groupId                              \
        	-DartifactId=$artifactId                        \
        	-Dversion=$version                     \
        	-Dpackaging=jar                                 \
        	-Dfile=lib/$artifactId.jar                      \
        	-DrepositoryId=spontech-maven-repository        \
        	-Durl=$deployUrl

	echo "      ----  DONE AA ----" 
done

# -----------------------------------------------------------------------
# Repack and install all native jars into the local maven repository
# All architecures has the same jar name and is selected by a classifier.
# This way, the automatic testings can unpack and install the native
# code by a normal maven rule.
# -----------------------------------------------------------------------
cd lib/arch
curDir=`pwd`
for a in *
do
	echo $a
	cd $a

	# Pack content into jogl-native-unknown.jar file
	$jarCmd cvf jogl-native-unknown-SNAPSHOT.jar *.so *.jnilib *.dylib *.dll 2>/dev/null

	echo "      ----  START B ----" 
	echo mvn install:install-file -Dfile=jogl-native-unknown-SNAPSHOT.jar -DgroupId=groupId -DartifactId=jogl-native -Dversion=$version -Dclassifier=$a -Dpackaging=jar
	mvn install:install-file -Dfile=jogl-native-unknown-SNAPSHOT.jar -DgroupId=$groupId -DartifactId=jogl-native -Dversion=$version -Dclassifier=$a -Dpackaging=jar
	echo "      ----  DONE B ----" 

        mvn -f NOT_USE_POM_XML deploy:deploy-file               \
        	-DgroupId=$groupId                              \
        	-DartifactId=jogl-native                        \
        	-Dversion=$version                      \
		-Dclassifier=$a					\
        	-Dpackaging=jar                                 \
        	-Dfile=jogl-native-unknown-SNAPSHOT.jar         \
        	-DrepositoryId=spontech-maven-repository        \
        	-Durl=$deployUrl

	echo "      ----  DONE BB ----" 

	rm jogl-native-unknown-SNAPSHOT.jar

	cd $curDir
done
cd ../..


