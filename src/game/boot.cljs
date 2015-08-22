(ns game.boot)

(defn Boot [game]
  (reify Object

    (preload [this]
      (.. game -load (image "preloadBar" "assets/loading-bar.png")))

    (create [this]
      (set! (.. game -scale -pageAlignHorizontally) true)
      (set! (.. game -scale -pageAlignVertically) true)
      (set! (.. game -stage -backgroundColor) "#000000")
      (.. game -state (start "preload")))))
