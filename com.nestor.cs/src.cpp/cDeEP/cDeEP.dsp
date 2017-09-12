# Microsoft Developer Studio Project File - Name="cDeEP" - Package Owner=<4>
# Microsoft Developer Studio Generated Build File, Format Version 6.00
# ** DO NOT EDIT **

# TARGTYPE "Win32 (x86) Console Application" 0x0103

CFG=cDeEP - Win32 Debug
!MESSAGE This is not a valid makefile. To build this project using NMAKE,
!MESSAGE use the Export Makefile command and run
!MESSAGE 
!MESSAGE NMAKE /f "cDeEP.mak".
!MESSAGE 
!MESSAGE You can specify a configuration when running NMAKE
!MESSAGE by defining the macro CFG on the command line. For example:
!MESSAGE 
!MESSAGE NMAKE /f "cDeEP.mak" CFG="cDeEP - Win32 Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "cDeEP - Win32 Release" (based on "Win32 (x86) Console Application")
!MESSAGE "cDeEP - Win32 Debug" (based on "Win32 (x86) Console Application")
!MESSAGE 

# Begin Project
# PROP AllowPerConfigDependencies 0
# PROP Scc_ProjName ""
# PROP Scc_LocalPath ""
CPP=cl.exe
RSC=rc.exe

!IF  "$(CFG)" == "cDeEP - Win32 Release"

# PROP BASE Use_MFC 2
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir "Release"
# PROP BASE Intermediate_Dir "Release"
# PROP BASE Target_Dir ""
# PROP Use_MFC 2
# PROP Use_Debug_Libraries 0
# PROP Output_Dir "Release"
# PROP Intermediate_Dir "Release"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MD /W3 /GX /O2 /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /D "_AFXDLL" /Yu"stdafx.h" /FD /c
# ADD CPP /nologo /MD /W3 /GX /O2 /I "d:\pdm\jeps\jep-alg" /D "WIN32" /D "NDEBUG" /D "_CONSOLE" /D "_MBCS" /D "_AFXDLL" /Yu"stdafx.h" /FD /c
# ADD BASE RSC /l 0x415 /d "NDEBUG" /d "_AFXDLL"
# ADD RSC /l 0x415 /d "NDEBUG" /d "_AFXDLL"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /machine:I386
# ADD LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /machine:I386
# Begin Special Build Tool
SOURCE="$(InputPath)"
PostBuild_Cmds=cd d:\pdm\jeps\cdeep	copy Release\*.exe ..\Release\*.*
# End Special Build Tool

!ELSEIF  "$(CFG)" == "cDeEP - Win32 Debug"

# PROP BASE Use_MFC 2
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir "Debug"
# PROP BASE Intermediate_Dir "Debug"
# PROP BASE Target_Dir ""
# PROP Use_MFC 1
# PROP Use_Debug_Libraries 1
# PROP Output_Dir "Debug"
# PROP Intermediate_Dir "Debug"
# PROP Target_Dir ""
# ADD BASE CPP /nologo /MDd /W3 /Gm /GX /ZI /Od /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /D "_AFXDLL" /Yu"stdafx.h" /FD /GZ /c
# ADD CPP /nologo /MTd /W3 /Gm /GX /ZI /Od /I "d:\pdm\jeps\jep-alg" /D "WIN32" /D "_DEBUG" /D "_CONSOLE" /D "_MBCS" /FR /Yu"stdafx.h" /FD /GZ /c
# ADD BASE RSC /l 0x415 /d "_DEBUG" /d "_AFXDLL"
# ADD RSC /l 0x415 /d "_DEBUG"
BSC32=bscmake.exe
# ADD BASE BSC32 /nologo
# ADD BSC32 /nologo
LINK32=link.exe
# ADD BASE LINK32 kernel32.lib user32.lib gdi32.lib winspool.lib comdlg32.lib advapi32.lib shell32.lib ole32.lib oleaut32.lib uuid.lib odbc32.lib odbccp32.lib /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept
# ADD LINK32 /nologo /subsystem:console /debug /machine:I386 /pdbtype:sept

!ENDIF 

# Begin Target

# Name "cDeEP - Win32 Release"
# Name "cDeEP - Win32 Debug"
# Begin Group "Source Files"

# PROP Default_Filter "cpp;c;cxx;rc;def;r;odl;idl;hpj;bat"
# Begin Group "JEP-ALG.CPP"

# PROP Default_Filter ""
# Begin Source File

SOURCE="..\JEP-ALG\AppData.cpp"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\Borders.cpp"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\CObArrayExt.cpp"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\ConvexSpace.cpp"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\Dataset.cpp"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\DiDuBorders.cpp"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\IO.cpp"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\Itemset.cpp"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\JEP.cpp"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\ProgressStep.cpp"
# End Source File
# End Group
# Begin Source File

SOURCE=.\cDeEP.cpp
# End Source File
# Begin Source File

SOURCE=.\cDeEP.rc
# End Source File
# Begin Source File

SOURCE=.\StdAfx.cpp
# ADD CPP /Yc"stdafx.h"
# End Source File
# End Group
# Begin Group "Header Files"

# PROP Default_Filter "h;hpp;hxx;hm;inl"
# Begin Group "JEP-ALG.H"

# PROP Default_Filter ""
# Begin Source File

SOURCE="..\JEP-ALG\AppData.h"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\Borders.h"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\CObArrayExt.h"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\ConvexSpace.h"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\Dataset.h"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\DiDuBorders.h"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\IO.h"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\Itemset.h"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\JEP.h"
# End Source File
# Begin Source File

SOURCE="..\JEP-ALG\ProgressStep.h"
# End Source File
# End Group
# Begin Source File

SOURCE=.\cDeEP.h
# End Source File
# Begin Source File

SOURCE=.\Resource.h
# End Source File
# Begin Source File

SOURCE=.\StdAfx.h
# End Source File
# End Group
# Begin Group "Resource Files"

# PROP Default_Filter "ico;cur;bmp;dlg;rc2;rct;bin;rgs;gif;jpg;jpeg;jpe"
# End Group
# Begin Source File

SOURCE=.\ReadMe.txt
# End Source File
# End Target
# End Project
