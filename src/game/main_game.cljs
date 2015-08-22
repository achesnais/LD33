(ns game.main-game)

(defn MainGame [game]
  (let [still-frame 0
        anim-frame-rate 2
        speed 1

        dialogue-style (clj->js {:font "20px Arial"
                                 :fill "white"})
        UP (js/Phaser.Keyboard.)]
    (reify Object

      (create [this]
        (js/console.log "Entering Main Game!")

        ;; Populate world
        (.. game -add (sprite 0 0 "floor"))
        (.. game -add (sprite 0 0 "dialogue-box"))
        (set! (.. this -dialogue) (.. game -add (text 0 0
                                                      "It begins here"
                                                      dialogue-style)))
        (set! (.. this -player) (.. game -add (sprite 10 10 "player")))

        ;; Define animations
        (.. this -player -animations (add "down" (clj->js [0 1 2]) anim-frame-rate true))
        (.. this -player -animations (add "up" (clj->js [3 4 5]) anim-frame-rate true))
        (.. this -player -animations (add "left" (clj->js [6 7 8]) anim-frame-rate true))
        (.. this -player -animations (add "right" (clj->js [9 10 11]) anim-frame-rate true))

        ;; Set up input
        (set! (.. this -cursors)
              (.. game -input -keyboard (createCursorKeys)))
        )

      (update [this]
        (cond
          (.. this -cursors -up -isDown)
          (do
            (let [y  (.. this -player -y)]
              (set! (.. this -player -y)
                    (- y speed)))
            (.. this -player -animations (play "up")))
          (.. this -cursors -down -isDown)
          (do
            (let [y  (.. this -player -y)]
              (set! (.. this -player -y)
                    (+ y speed)))
            (.. this -player -animations (play "down")))

          (.. this -cursors -left -isDown)
          (do
            (let [x  (.. this -player -x)]
              (set! (.. this -player -x)
                    (- x speed)))
            (.. this -player -animations (play "left")))

          (.. this -cursors -right -isDown)
          (do
            (let [x  (.. this -player -x)]
              (set! (.. this -player -x)
                    (+ x speed)))
            (.. this -player -animations (play "right")))

          :else (set! (.. this -player -frame) still-frame))))))
