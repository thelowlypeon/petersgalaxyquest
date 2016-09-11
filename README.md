# Peter's Galaxy Quest

This is a game I wrote back in CS 101 (in 2005!) as an independent study. It's nothing revolutionary, but was a ton of fun to work on.

## Installation

You'll need a JDK installed on your machine to compile this. I have no idea what version I was using back then, but it works now (except for audio) using [JDK 8u102](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html).

To run, simply compile everything in the src directory and run:

```
$ javac src/*.java -d bin/
$ java -cp bin/ BasicDraw
```

## Game Play

Once the game launches, your cursor becomes your spaceship. Click or hit <space> to fire lazers from your ship.

A shower of asteroids will start to approach your planet, which you need protect by exploding all the asteroids. But be careful! Your lazer gets hot, so don't fire too much.

## TODO

There are a few things that don't work the way the did in the ol' days:

* Music! The music was the best part.
* Audio: lazers and explosions! Who wouldn't want it!
* The high score list is failing to read, likely because of a path change or permissions

There are also some things I always wanted to do but didn't get around to:

* Cleanup. This code is gross. I didn't realize how undisciplined I was back then.
* Difficulty. The game gets _really_ difficult after a while, making it nearly impossible to get past level 4 or so.
* Efficiency. The game gets my fans spinning. Why?! It shouldn't be too much computation. Oh well.
