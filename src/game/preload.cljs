(ns game.preload)

(defn Preload [game]
  (reify Object

    (preload [this]
      ;; Setting up the loading bar
      (set! (.. game -preloadBar)
            (.. game -add (sprite (.. game -world -centerX)
                                  (.. game -world -centerY)
                                  "preloadBar")))
      (.. game -preloadBar -anchor (setTo 0.5 0.5))
      (.. game -load (setPreloadSprite (.. game -preloadBar)))

      ;; Loading all the assets
      (.. game -load (image "dialogue-box" "assets/dialogue-box.png"))
      (.. game -load (image "floor" "assets/floor.png"))
      (.. game -load (image "glass1" "assets/glass1.png"))
      (.. game -load (image "glass2" "assets/glass2.png"))
      (.. game -load (image "glass3" "assets/glass3.png"))
      (.. game -load (image "glass4" "assets/glass4.png"))
      (.. game -load (image "glass1" "assets/throne.png"))
      (.. game -load (spritesheet "player" "assets/player.png" 20 40 12)))

    (create [this]
      ())

    (update [this]
      (if (false? (.-ready game))
        (set! (.-ready game) true)
        (.. game -state (start "mainGame"))
        ;; (.. game -state (start "titleScreen"))
        ))))
