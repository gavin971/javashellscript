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
OBJECTDIR=build/Debug_Linux/${PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/src/jss.o \
	${OBJECTDIR}/src/cache.o \
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
	${MAKE}  -f nbproject/Makefile-Debug_Linux.mk dist/Debug/${PLATFORM}/jss

dist/Debug/${PLATFORM}/jss: /usr/lib/libboost_filesystem.a

dist/Debug/${PLATFORM}/jss: /usr/lib/libboost_system.a

dist/Debug/${PLATFORM}/jss: ${OBJECTFILES}
	${MKDIR} -p dist/Debug/${PLATFORM}
	${LINK.cc} -o dist/Debug/${PLATFORM}/jss ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/src/jss.o: src/jss.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -g -I../../boost_1_37_0/include/boost-1_37 -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/jss.o src/jss.cpp

${OBJECTDIR}/src/cache.o: src/cache.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -g -I../../boost_1_37_0/include/boost-1_37 -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/cache.o src/cache.cpp

${OBJECTDIR}/src/preprozessor.o: src/preprozessor.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	${RM} $@.d
	$(COMPILE.cc) -g -I../../boost_1_37_0/include/boost-1_37 -MMD -MP -MF $@.d -o ${OBJECTDIR}/src/preprozessor.o src/preprozessor.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf:
	${RM} -r build/Debug_Linux
	${RM} dist/Debug/${PLATFORM}/jss

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
