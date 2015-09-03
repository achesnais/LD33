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
      (.. game -load (image "dialogueBox" "assets/dialogue-box.png"))
      (.. game -load (image "world" "assets/world-v2.png"))
      (.. game -load (image "glass1" "assets/glass1.png"))
      (.. game -load (image "glass2" "assets/glass2.png"))
      (.. game -load (image "glass3" "assets/glass3.png"))
      (.. game -load (image "glass4" "assets/glass4.png"))
      (.. game -load (image "glass1" "assets/throne.png"))
      (.. game -load (image "weapon" "assets/weapon.png"))
      (.. game -load (image "bot-col" "assets/bot-col.png"))
      (.. game -load (spritesheet "player" "assets/player.png" 20 40 17)))

    (create [this]
      (set! (.. game -time -advancedTiming) true)
      (set! (.. game -stage -smoothed) false))

    (update [this]
      (.. game -state (start "titleScreen")))))
