(ns mandelbrot
  (:import (javax.swing JFrame JLabel)
           (java.awt Graphics Dimension Color)
           (java.awt.image BufferedImage)
           (javax.imageio ImageIO)
           (java.io File)
  )
)

(declare export)

(defn add
  "Complex addition"
  [c1 c2]
  (map + c1 c2))

(defn multiply
  "Complex Multipication"
  [[real-a imag-a] [real-b imag-b]]
  [(- (* real-a real-b)
      (* imag-a imag-b))

   (+ (* real-a imag-b)
      (* imag-a real-b))])

(defn abs
  "Complex Absulute Value"
  [[real imag]]
  (Math/sqrt 
   (+ (* real real)
      (* imag imag))))

(defn calc-path
  [x y max-iterations]
  (let  [c [x y]]
    (loop [z c 
           path []
           iterations 0]
      (if (> iterations max-iterations)
        []
        (if (> (abs z) 2.0)
          (conj path z)
          (recur (add c (multiply z z)) (conj path z) (inc iterations)))))))

(defn point-to-coordinate [size [real imag]]
  [(int (+ (* 0.3 size (+ real 0.5)) (/ size 2)))
   (int (+ (* 0.3 size imag) (/ size 2)))])

(defn buffer-set [{size :size buffer :buffer} point]
  (let  [[x y] (point-to-coordinate size point)]
    (if (and (> x 0)
             (> y 0)
             (< x size)
             (< y size))
      (aset-int buffer y x (+ 1 (aget buffer y x))))))

(defn generate [fractal & [ex]]
  (let  [{:keys [buffer iteration]} fractal]
    (doseq [point (iterate inc 1)]
      (let  [x    (- (rand 6) 3)
             y    (- (rand 6) 3)
             path (calc-path x y iteration)]

        (if (= (mod point 1000000) 0)
          (do
            (println "Point: " point)
            (if ex
              (let [filename (format "out/fractal.%030d.png" point)]
                (println "Exporting to" filename)
                (future (export fractal filename))
              )
            )
          )
        )

        (doseq [p path] (buffer-set fractal p))))))

(defn start [fractal]
  (future (generate fractal)))

(defn startgen [fractal]
  (future (generate fractal true)))

(defn calc-pixel-color
  [iteration max-iterations]
  (let [gray (int (/ (* iteration 255) max-iterations))
        r    gray
        g    (min (int ( / (* 5 ( * gray gray)) 255)) 255)
        b    (min (int (+ 40 ( / (* 5 (* gray gray)) 255))) 255)]
    (try
      (Color. r g b)
      (catch Exception e (new Color 0 0 0)))))

(defn paint-canvas [buffer size graphics]
  (let  [biggest  (apply max (map #(apply max %) buffer))]
    (doseq [y (range size)
            x (range size)]

      (.setColor graphics (calc-pixel-color (aget buffer y x) biggest))
      (.drawLine graphics x y x y))))

(defn draw [{buffer :buffer size :size}]
  (let [image  (BufferedImage. size size BufferedImage/TYPE_INT_RGB)
        canvas (proxy [JLabel] []
                 (paint [g] (.drawImage g image 0 0 this)))]

    (paint-canvas buffer size (.createGraphics image))

    (doto (JFrame.)
      (.add canvas)
      (.setSize (Dimension. size size))
      (.show))))

(defn export [{buffer :buffer size :size} filename]
  (let [image (BufferedImage. size size BufferedImage/TYPE_INT_RGB)
        canvas (proxy [JLabel] []
                 (paint [g] (.drawImage g image 0 0 this)))
        file (File. filename)]
    (paint-canvas buffer size (.createGraphics image))
    (ImageIO/write image "png" file)
    (println filename "saved")
  )
)

(def fractal {:buffer (make-array Integer/TYPE 800 800) :size 800 :iteration 50})
