(ns game.main-game)

(defn MainGame [game]
  (let [still-frame 0
        anim-frame-rate 2
        speed 10

        dialogue-style (clj->js {:font "20px Arial"
                                 :fill "white"})
        UP (js/Phaser.Keyboard.)]
    (reify Object

      (create [this]
        (js/console.log "Entering Main Game!")

        ;; Define world bounds
        (.. game -world (setBounds 0 -100 600 700))

        ;; Populate world
        (.. game -add (sprite 0 0 "world"))
        (set! (.. this -dialogue-box) (.. game -add (sprite 0 0 "dialogue-box")))
        (set! (.. this -dialogue-text)
              (.. game -add (text (/ (.. game -camera -width) 2)
                                  50
                                  "It begins here"
                                  dialogue-style)))
        (.. this -dialogue-text -anchor (setTo 0.5 0.5))
        (set! (.. this -player) (.. game -add (sprite 10 10 "player")))

        ;; Define animations
        (.. this -player -animations (add "down" (clj->js [0 1 2]) anim-frame-rate true))
        (.. this -player -animations (add "up" (clj->js [3 4 5]) anim-frame-rate true))
        (.. this -player -animations (add "left" (clj->js [6 7 8]) anim-frame-rate true))
        (.. this -player -animations (add "right" (clj->js [9 10 11]) anim-frame-rate true))

        ;; Set up input
        (set! (.. this -cursors)
              (.. game -input -keyboard (createCursorKeys)))

        ;; Set up camera
        (.. this -player -anchor (setTo 0.5 0.5))
        (.. game -camera (follow (.-player this)))
        #_(set! (.. game -player -cameraOffset) 0 100)

        (set! (.. this -dialogue-box -fixedToCamera) true)
        (set! (.. this -dialogue-text -fixedToCamera) true)
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

          :else (set! (.. this -player -frame) still-frame))

        (.collideWorld this))

      (collideWorld [this]
        (let [p (.-player this)]
          (when (< (.-y p) 40)
            (set! (.-y p) 40))
          (when (> (.-y p) 560)
            (set! (.-y p) 560))
          (when (< (.-x p) 30)
            (set! (.-x p) 30))
          (when (> (.-x p) 570)
            (set! (.-x p) 570)))))))
