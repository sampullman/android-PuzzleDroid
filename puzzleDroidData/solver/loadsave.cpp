/*
 * loadsave.cpp
 * cubex load / save serializer
 * by Eric Dietz, Sun 06 Nov 2005, version 1
 */

// includes
#include "loadsave.h"

// declarations

// loader
int loadcube (FILE *fp, Cubex *cube)
{
  fscanf
  (fp,
"%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d%d",
&cube->Cub[-1+2][ 2+2][ 1+2], &cube->Cub[ 0+2][ 2+2][ 1+2], &cube->Cub[ 1+2][ 2+2][ 1+2],
&cube->Cub[ 0+2][ 1+2][ 0+2], &cube->cenfix,
&cube->Cub[-1+2][ 2+2][ 0+2], &cube->Cub[ 0+2][ 2+2][ 0+2], &cube->Cub[ 1+2][ 2+2][ 0+2],
&cube->Cub[-1+2][ 0+2][ 0+2], &cube->Cub[ 0+2][ 0+2][-1+2], &cube->Cub[ 1+2][ 0+2][ 0+2], &cube->Cub[ 0+2][ 0+2][ 1+2],
&cube->Cub[-1+2][ 2+2][-1+2], &cube->Cub[ 0+2][ 2+2][-1+2], &cube->Cub[ 1+2][ 2+2][-1+2],
&cube->Cub[ 0+2][-1+2][ 0+2],
&cube->Cub[-2+2][ 1+2][ 1+2], &cube->Cub[-2+2][ 1+2][ 0+2], &cube->Cub[-2+2][ 1+2][-1+2],
&cube->Cub[-1+2][ 1+2][-2+2], &cube->Cub[ 0+2][ 1+2][-2+2], &cube->Cub[ 1+2][ 1+2][-2+2],
&cube->Cub[ 2+2][ 1+2][-1+2], &cube->Cub[ 2+2][ 1+2][ 0+2], &cube->Cub[ 2+2][ 1+2][ 1+2],
&cube->Cub[ 1+2][ 1+2][ 2+2], &cube->Cub[ 0+2][ 1+2][ 2+2], &cube->Cub[-1+2][ 1+2][ 2+2],
&cube->Cub[-2+2][ 0+2][ 1+2], &cube->Cub[-2+2][ 0+2][ 0+2], &cube->Cub[-2+2][ 0+2][-1+2],
&cube->Cub[-1+2][ 0+2][-2+2], &cube->Cub[ 0+2][ 0+2][-2+2], &cube->Cub[ 1+2][ 0+2][-2+2],
&cube->Cub[ 2+2][ 0+2][-1+2], &cube->Cub[ 2+2][ 0+2][ 0+2], &cube->Cub[ 2+2][ 0+2][ 1+2],
&cube->Cub[ 1+2][ 0+2][ 2+2], &cube->Cub[ 0+2][ 0+2][ 2+2], &cube->Cub[-1+2][ 0+2][ 2+2],
&cube->Cub[-2+2][-1+2][ 1+2], &cube->Cub[-2+2][-1+2][ 0+2], &cube->Cub[-2+2][-1+2][-1+2],
&cube->Cub[-1+2][-1+2][-2+2], &cube->Cub[ 0+2][-1+2][-2+2], &cube->Cub[ 1+2][-1+2][-2+2],
&cube->Cub[ 2+2][-1+2][-1+2], &cube->Cub[ 2+2][-1+2][ 0+2], &cube->Cub[ 2+2][-1+2][ 1+2],
&cube->Cub[ 1+2][-1+2][ 2+2], &cube->Cub[ 0+2][-1+2][ 2+2], &cube->Cub[-1+2][-1+2][ 2+2],
&cube->Cub[-1+2][-2+2][-1+2], &cube->Cub[ 0+2][-2+2][-1+2], &cube->Cub[ 1+2][-2+2][-1+2],
&cube->Cub[-1+2][-2+2][ 0+2], &cube->Cub[ 0+2][-2+2][ 0+2], &cube->Cub[ 1+2][-2+2][ 0+2],
&cube->Cub[-1+2][-2+2][ 1+2], &cube->Cub[ 0+2][-2+2][ 1+2], &cube->Cub[ 1+2][-2+2][ 1+2]
  );
  return 0;
}

// saver
int savecube (FILE *fp, Cubex *cube)
{
  fprintf
  (fp,
"\
        %i %i %i     %i      %i\n\
        %i %i %i   %i %i %i %i\n\
        %i %i %i     %i\n\
 %i %i %i  %i %i %i  %i %i %i  %i %i %i\n\
 %i %i %i  %i %i %i  %i %i %i  %i %i %i\n\
 %i %i %i  %i %i %i  %i %i %i  %i %i %i\n\
        %i %i %i\n\
        %i %i %i\n\
        %i %i %i\n\
",
cube->Cub[-1+2][ 2+2][ 1+2], cube->Cub[ 0+2][ 2+2][ 1+2], cube->Cub[ 1+2][ 2+2][ 1+2],
cube->Cub[ 0+2][ 1+2][ 0+2], cube->cenfix,
cube->Cub[-1+2][ 2+2][ 0+2], cube->Cub[ 0+2][ 2+2][ 0+2], cube->Cub[ 1+2][ 2+2][ 0+2],
cube->Cub[-1+2][ 0+2][ 0+2], cube->Cub[ 0+2][ 0+2][-1+2], cube->Cub[ 1+2][ 0+2][ 0+2], cube->Cub[ 0+2][ 0+2][ 1+2],
cube->Cub[-1+2][ 2+2][-1+2], cube->Cub[ 0+2][ 2+2][-1+2], cube->Cub[ 1+2][ 2+2][-1+2],
cube->Cub[ 0+2][-1+2][ 0+2],
cube->Cub[-2+2][ 1+2][ 1+2], cube->Cub[-2+2][ 1+2][ 0+2], cube->Cub[-2+2][ 1+2][-1+2],
cube->Cub[-1+2][ 1+2][-2+2], cube->Cub[ 0+2][ 1+2][-2+2], cube->Cub[ 1+2][ 1+2][-2+2],
cube->Cub[ 2+2][ 1+2][-1+2], cube->Cub[ 2+2][ 1+2][ 0+2], cube->Cub[ 2+2][ 1+2][ 1+2],
cube->Cub[ 1+2][ 1+2][ 2+2], cube->Cub[ 0+2][ 1+2][ 2+2], cube->Cub[-1+2][ 1+2][ 2+2],
cube->Cub[-2+2][ 0+2][ 1+2], cube->Cub[-2+2][ 0+2][ 0+2], cube->Cub[-2+2][ 0+2][-1+2],
cube->Cub[-1+2][ 0+2][-2+2], cube->Cub[ 0+2][ 0+2][-2+2], cube->Cub[ 1+2][ 0+2][-2+2],
cube->Cub[ 2+2][ 0+2][-1+2], cube->Cub[ 2+2][ 0+2][ 0+2], cube->Cub[ 2+2][ 0+2][ 1+2],
cube->Cub[ 1+2][ 0+2][ 2+2], cube->Cub[ 0+2][ 0+2][ 2+2], cube->Cub[-1+2][ 0+2][ 2+2],
cube->Cub[-2+2][-1+2][ 1+2], cube->Cub[-2+2][-1+2][ 0+2], cube->Cub[-2+2][-1+2][-1+2],
cube->Cub[-1+2][-1+2][-2+2], cube->Cub[ 0+2][-1+2][-2+2], cube->Cub[ 1+2][-1+2][-2+2],
cube->Cub[ 2+2][-1+2][-1+2], cube->Cub[ 2+2][-1+2][ 0+2], cube->Cub[ 2+2][-1+2][ 1+2],
cube->Cub[ 1+2][-1+2][ 2+2], cube->Cub[ 0+2][-1+2][ 2+2], cube->Cub[-1+2][-1+2][ 2+2],
cube->Cub[-1+2][-2+2][-1+2], cube->Cub[ 0+2][-2+2][-1+2], cube->Cub[ 1+2][-2+2][-1+2],
cube->Cub[-1+2][-2+2][ 0+2], cube->Cub[ 0+2][-2+2][ 0+2], cube->Cub[ 1+2][-2+2][ 0+2],
cube->Cub[-1+2][-2+2][ 1+2], cube->Cub[ 0+2][-2+2][ 1+2], cube->Cub[ 1+2][-2+2][ 1+2]
  );
  return 0;
}

//
