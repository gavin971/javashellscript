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
AS=

# Macros
CND_PLATFORM=GNU-MacOSX
CND_CONF=Release_Mac
CND_DISTDIR=dist

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=build/${CND_CONF}/${CND_PLATFORM}

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

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=/Users/robert/Programm/boost_1_37_0/lib/x86/libboost_filesystem-xgcc40-mt.a /Users/robert/Programm/boost_1_37_0/lib/x86/libboost_system-xgcc40-mt.a

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	${MAKE}  -f nbproject/Makefile-Release_Mac.mk dist/jss

dist/jss: /Users/robert/Programm/boost_1_37_0/lib/x86/libboost_filesystem-xgcc40-mt.a

dist/jss: /Users/robert/Programm/boost_1_37_0/lib/x86/libboost_system-xgcc40-mt.a

dist/jss: ${OBJECTFILES}
	${MKDIR} -p dist
	${LINK.cc} -framework CoreFoundation -o dist/jss -Wl,-S ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/src/jss.o: nbproject/Makefile-${CND_CONF}.mk src/jss.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O2 -s -DMACOS -I../../boost_1_37_0/include/boost-1_37 -I/System/Library/Frameworks/CoreFoundation.framework/Headers -o ${OBJECTDIR}/src/jss.o src/jss.cpp

${OBJECTDIR}/src/cache.o: nbproject/Makefile-${CND_CONF}.mk src/cache.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O2 -s -DMACOS -I../../boost_1_37_0/include/boost-1_37 -I/System/Library/Frameworks/CoreFoundation.framework/Headers -o ${OBJECTDIR}/src/cache.o src/cache.cpp

${OBJECTDIR}/src/config.o: nbproject/Makefile-${CND_CONF}.mk src/config.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O2 -s -DMACOS -I../../boost_1_37_0/include/boost-1_37 -I/System/Library/Frameworks/CoreFoundation.framework/Headers -o ${OBJECTDIR}/src/config.o src/config.cpp

${OBJECTDIR}/src/preprozessor.o: nbproject/Makefile-${CND_CONF}.mk src/preprozessor.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O2 -s -DMACOS -I../../boost_1_37_0/include/boost-1_37 -I/System/Library/Frameworks/CoreFoundation.framework/Headers -o ${OBJECTDIR}/src/preprozessor.o src/preprozessor.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf:
	${RM} -r build/Release_Mac
	${RM} dist/jss

# Subprojects
.clean-subprojects:
