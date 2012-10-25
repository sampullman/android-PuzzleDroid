# Makefile for cubex by Eric
CC=g++
LINK=g++
CFLAGS=-O2
LFLAGS=
INCLUDES=
OBJS=cubex.o main.o
RM=/bin/rm -f

all: build

build: $(OBJS)
	$(LINK) $(LFLAGS)  -o cubex  $(OBJS)

clean:
	$(RM) $(OBJS)

cubex.o: cubex.cpp $(INCLUDES) cubex.h
	$(CC) $(CFLAGS) -c cubex.cpp

main.o: main.cpp $(INCLUDES) cubex.h
	$(CC) $(CFLAGS) -c main.cpp

dummy:
