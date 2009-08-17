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
PLATFORM=GNU-MacOSX

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=build/Debug_Mac/${PLATFORM}

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
LDLIBSOPTIONS=/Users/robert/Programm/boost_1_37_0/lib/x86/libboost_filesystem-xgcc40-mt.a /Users/robert/Programm/boost_1_37_0/lib/x86/libboost_system-xgcc40-mt.a

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	${MAKE}  -f nbproject/Makefile-Debug_Mac.mk dist/jss

dist/jss: /Users/robert/Programm/boost_1_37_0/lib/x86/libboost_filesystem-xgcc40-mt.a

dist/jss: /Users/robert/Programm/boost_1_37_0/lib/x86/libboost_system-xgcc40-mt.a

dist/jss: ${OBJECTFILES}
	${MKDIR} -p dist
	${LINK.cc} -framework CoreFoundation -o dist/jss ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/src/jss.o: src/jss.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -g -DMACOS -I../../boost_1_37_0/include/boost-1_37 -o ${OBJECTDIR}/src/jss.o src/jss.cpp

${OBJECTDIR}/src/cache.o: src/cache.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -g -DMACOS -I../../boost_1_37_0/include/boost-1_37 -o ${OBJECTDIR}/src/cache.o src/cache.cpp

${OBJECTDIR}/src/config.o: src/config.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -g -DMACOS -I../../boost_1_37_0/include/boost-1_37 -o ${OBJECTDIR}/src/config.o src/config.cpp

${OBJECTDIR}/src/preprozessor.o: src/preprozessor.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -g -DMACOS -I../../boost_1_37_0/include/boost-1_37 -o ${OBJECTDIR}/src/preprozessor.o src/preprozessor.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf:
	${RM} -r build/Debug_Mac
	${RM} dist/jss

# Subprojects
.clean-subprojects:
