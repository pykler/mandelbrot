==================
Mandelbrot Fractal
==================

Overview
--------

My First attempt at coding with Clojure.

Started off with the fractal code from: 

  * http://nakkaya.com/2009/10/04/fractals-in-clojure-buddhabrot-fractal/

With help from the code at:

  * https://github.com/travisjeffery/clojure-fractal/

And using the library:

  * https://github.com/RobinRamael/gif-clj

Ended up creating **Mandelbrot Fractal GIF Generator**

Usage
-----

I am not using lein to build this yet, it is quite raw. To run it, clone this
repo and from inside the repo dir start a clojure repl ... where you can type::

   (load-file "mandelbrot.clj")
   (mandelbrot/startgen mandelbrot/fractal)

Now a bunch of files are being generated in the *out* directory. The longer you
run the fractal the more points you get in the gif. The gif generated will be a
timelapse of the fractal generation at 1M point intervals.

To generate the gif::

   # TODO
