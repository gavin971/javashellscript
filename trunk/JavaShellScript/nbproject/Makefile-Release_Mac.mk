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
GREP=grep
NM=nm
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc
CCC=g++
CXX=g++
FC=gfortran
AS=as

# Macros
CND_PLATFORM=GNU-MacOSX
CND_CONF=Release_Mac
CND_DISTDIR=dist
CND_BUILDDIR=build

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=${CND_BUILDDIR}/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/src/cache.o \
	${OBJECTDIR}/src/config.o \
	${OBJECTDIR}/src/preprozessor.o \
	${OBJECTDIR}/src/jss.o


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
LDLIBSOPTIONS=../../boost_1_37_0/lib/x64/libboost_filesystem-xgcc40-mt.a ../../boost_1_37_0/lib/x64/libboost_system-xgcc40-mt.a

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	"${MAKE}"  -f nbproject/Makefile-${CND_CONF}.mk dist/jss

dist/jss: ../../boost_1_37_0/lib/x64/libboost_filesystem-xgcc40-mt.a

dist/jss: ../../boost_1_37_0/lib/x64/libboost_system-xgcc40-mt.a

dist/jss: ${OBJECTFILES}
	${MKDIR} -p dist
	${LINK.cc} -framework CoreFoundation -o dist/jss -Wl,-S ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/src/cache.o: src/cache.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O3 -s -DMACOS -I../../boost_1_37_0/include/boost-1_37 -I/System/Library/Frameworks/CoreFoundation.framework/Headers -o ${OBJECTDIR}/src/cache.o src/cache.cpp

${OBJECTDIR}/src/config.o: src/config.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O3 -s -DMACOS -I../../boost_1_37_0/include/boost-1_37 -I/System/Library/Frameworks/CoreFoundation.framework/Headers -o ${OBJECTDIR}/src/config.o src/config.cpp

${OBJECTDIR}/src/preprozessor.o: src/preprozessor.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O3 -s -DMACOS -I../../boost_1_37_0/include/boost-1_37 -I/System/Library/Frameworks/CoreFoundation.framework/Headers -o ${OBJECTDIR}/src/preprozessor.o src/preprozessor.cpp

${OBJECTDIR}/src/jss.o: src/jss.cpp 
	${MKDIR} -p ${OBJECTDIR}/src
	$(COMPILE.cc) -O3 -s -DMACOS -I../../boost_1_37_0/include/boost-1_37 -I/System/Library/Frameworks/CoreFoundation.framework/Headers -o ${OBJECTDIR}/src/jss.o src/jss.cpp

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r ${CND_BUILDDIR}/${CND_CONF}
	${RM} dist/jss

# Subprojects
.clean-subprojects:
