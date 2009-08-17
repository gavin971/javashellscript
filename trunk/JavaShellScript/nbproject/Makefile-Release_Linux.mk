#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran

# Macros
PLATFORM=GNU-Linux-x86

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=build/Release_Linux/${PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/src/jss.o \
	${OBJECTDIR}/src/cache.o \
	${OBJECTDIR}/src/config.o \
	${OBJECTDIR}/src/preprozessor.o

# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=/usr/lib/libboost_filesystem.a /usr/lib/libboost_system.a

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	${MAKE}  -f nbproject/Makefile-Release_Linux.mk dist/jss

dist/jss: /usr/lib/libboost_filesystem.a

dist/jss: /usr/lib/libboost_system.a

dist/jss: ${OBJECTFILES}
	${MKDIR} -p dist
	${LINK.cc} -o dist/jss -s ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/src/jss.o: src/jss.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O3 -s -DLINUX -o ${OBJECTDIR}/src/jss.o src/jss.cpp

${OBJECTDIR}/src/cache.o: src/cache.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O3 -s -DLINUX -o ${OBJECTDIR}/src/cache.o src/cache.cpp

${OBJECTDIR}/src/config.o: src/config.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O3 -s -DLINUX -o ${OBJECTDIR}/src/config.o src/config.cpp

${OBJECTDIR}/src/preprozessor.o: src/preprozessor.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O3 -s -DLINUX -o ${OBJECTDIR}/src/preprozessor.o src/preprozessor.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf:
	${RM} -r build/Release_Linux
	${RM} dist/jss

# Subprojects
.clean-subprojects:
