(ns game.main-game
  (:require [game.story :refer [dialogueTree]]))

(defn MainGame [game]
  (let [still-frame 0
        anim-frame-rate 2
        speed 5

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
                                  ""
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
        (set! (.. this -spacebar) (.. game -input -keyboard (addKey js/Phaser.Keyboard.SPACEBAR)))


        ;; Set up camera
        (.. this -player -anchor (setTo 0.5 0.5))
        (.. game -camera (follow (.-player this)))
        #_(set! (.. game -player -cameraOffset) 0 100)

        ;; Set up dialogue box
        (set! (.. this -dialogue-box -fixedToCamera) true)
        (set! (.. this -dialogue-text -fixedToCamera) true)
        (set! (.. this -lastSpace) (.. game -time -now))
        #_(.triggerDialogue this ["First" "Second"])

        ;; Set up events TODO
        (set! (.. this -glass1Rect) (js/Phaser.Rectangle. 25 180 80 80))
        (set! (.. this -glass1Events) (:glass1 dialogueTree))

        (set! (.. this -glass2Rect) (js/Phaser.Rectangle. 25 380 80 80))
        (set! (.. this -glass2Events) (:glass2 dialogueTree))

        (set! (.. this -glass3Rect) (js/Phaser.Rectangle. 490 180 80 80))
        (set! (.. this -glass1Events) (:glass1 dialogueTree))

        (set! (.. this -glass4Rect) (js/Phaser.Rectangle. 490 380 80 80))
        (set! (.. this -glass4Events) (:glass2 dialogueTree))

        )

      (update [this]

        ;; Dialogue situation - no movement
        (if-let [d (seq (.-dialogue this))]
          (do
            (.. this -dialogue-text (setText (first d)))
            (when (and (.. this -spacebar -isDown)
                       (> (- (.. game -time -now)
                             (.. this -lastSpace))
                          500))
              (do
                (set! (.. this -lastSpace) (.. game -time -now))
                (.. this -dialogue-text (setText ""))
                (set! (.-dialogue this) (rest d)))))


          ;; Basic Movement
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

            :else (set! (.. this -player -frame) still-frame)))

        (.collideWorld this)
        (when-let [ce (.collideEvents this)]
          #_(js/console.log ce
                            (.. this -lastSpace)
                            (- (.. game -time -now)
                               (.. this -lastSpace)))
          (when (and (.. this -spacebar -isDown)
                     (> (- (.. game -time -now)
                           (.. this -lastSpace))
                        500))
            (do
              (js/console.log "HEY")
              (set! (.. this -lastSpace) (.. game -time -now))
              (.triggerDialogue this ce))))

        ;; DEBUG
        #_(js/console.log (str "Player posx: " (.. this -player -x) " posy: " (.. this -player -y)))
        )

      (collideWorld [this]
        (let [p (.-player this)]

          ;; Collide the world
          (when (< (.-y p) 40)
            (set! (.-y p) 40))
          (when (> (.-y p) 560)
            (set! (.-y p) 560))
          (when (< (.-x p) 30)
            (set! (.-x p) 30))
          (when (> (.-x p) 570)
            (set! (.-x p) 570))))

      (collideEvents [this]
        (cond
          (js/Phaser.Rectangle.containsPoint (.. this -glass1Rect) (.-player this)) :glass1
          (js/Phaser.Rectangle.containsPoint (.. this -glass2Rect) (.-player this)) :glass2
          (js/Phaser.Rectangle.containsPoint (.. this -glass3Rect) (.-player this)) :glass3
          (js/Phaser.Rectangle.containsPoint (.. this -glass4Rect) (.-player this)) :glass4
          :else false))

      (triggerDialogue [this k]
        (case k

          :glass1
          (if (seq (.. this -glass1Events))
            (let [evs (.. this -glass1Events)]
              (do
                (set! (.. this -dialogue) (first evs))
                (set! (.. this -glass1Events) (rest evs))))
            (set! (.. this -dialogue) ["The Sacred Egg" "..."]))

          :glass2
          (if (seq (.. this -glass2Events))
            (let [evs (.. this -glass2Events)]
              (do
                (set! (.. this -dialogue) (first evs))
                (set! (.. this -glass2Events) (rest evs))))
            (set! (.. this -dialogue) ["The Flower with Five Leaves" "..."]))

          :glass3
          (if (seq (.. this -glass3Events))
            (let [evs (.. this -glass3Events)]
              (do
                (set! (.. this -dialogue) (first evs))
                (set! (.. this -glass3Events) (rest evs))))
            (set! (.. this -dialogue) ["The Ascension" "..."]))

          :glass4
          (if (seq (.. this -glass4Events))
            (let [evs (.. this -glass4Events)]
              (do
                (set! (.. this -dialogue) (first evs))
                (set! (.. this -glass4Events) (rest evs))))
            (set! (.. this -dialogue) ["The Crier" "..."]))))

      (render [this]
        (.. game -debug (geom (.. this -glass1Rect) "#0fffff"))
        (.. game -debug (geom (.. this -glass2Rect) "#0fffff"))
        (.. game -debug (geom (.. this -glass3Rect) "#0fffff"))
        (.. game -debug (geom (.. this -glass4Rect) "#0fffff"))))))
