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
        (.. game -world (setBounds 0 -100 1200 1300))

        ;; Populate world
        (.. game -add (sprite 0 0 "world"))
        (set! (.. this -player) (.. game -add (sprite 600 1000 "player")))
        (set! (.. this -weaponSprite) (.. game -add (sprite 598 80 "weapon")))
        (.. this -weaponSprite -anchor (setTo 0.5 0.5))

        ;; Glasses

        (let [gs (.. game -add (sprite 0 100 "glass1"))]
          (set! (.. gs -visible) false)
          (set! (.. gs -fixedToCamera) true)
          (set! (.. this -g1) gs))
        (let [gs (.. game -add (sprite 0 100 "glass2"))]
          (set! (.. gs -visible) false)
          (set! (.. gs -fixedToCamera) true)
          (set! (.. this -g2) gs))
        (let [gs (.. game -add (sprite 0 100 "glass3"))]
          (set! (.. gs -visible) false)
          (set! (.. gs -fixedToCamera) true)
          (set! (.. this -g3) gs))
        (let [gs (.. game -add (sprite 0 100 "glass4"))]
          (set! (.. gs -visible) false)
          (set! (.. gs -fixedToCamera) true)
          (set! (.. this -g4) gs))



        ;; Define animations
        (.. this -player -animations (add "down" (clj->js [0 1 2]) anim-frame-rate true))
        (.. this -player -animations (add "up" (clj->js [3 4 5]) anim-frame-rate true))
        (.. this -player -animations (add "left" (clj->js [6 7 8]) anim-frame-rate true))
        (.. this -player -animations (add "right" (clj->js [9 10 11]) anim-frame-rate true))

        ;; Set up input
        (set! (.. this -cursors)
              (.. game -input -keyboard (createCursorKeys)))
        (set! (.. this -spacebar) (.. game -input -keyboard (addKey js/Phaser.Keyboard.SPACEBAR)))
        (set! (.. this -action) (.. game -input -keyboard (addKey js/Phaser.Keyboard.ENTER)))


        ;; Set up camera
        (.. this -player -anchor (setTo 0.5 0.5))
        (.. game -camera (follow (.-player this)))
        #_(set! (.. game -player -cameraOffset) 0 100)

        ;; Set up dialogue box
        (set! (.. this -dialogueBox) (.. game -add (sprite 0 0 "dialogueBox")))
        (set! (.. this -dialogueText)
              (.. game -add (text (/ (.. game -camera -width) 2)
                                  50
                                  ""
                                  dialogue-style)))
        (.. this -dialogueText -anchor (setTo 0.5 0.5))
        (set! (.. this -dialogueBox -fixedToCamera) true)
        (set! (.. this -dialogueText -fixedToCamera) true)
        (set! (.. this -spaceText))
        (set! (.. this -lastSpace) (.. game -time -now))
        #_(.triggerDialogue this ["First" "Second"])

        ;; Set up events TODO
        (set! (.. this -glass1Rect) (js/Phaser.Rectangle. 100 775 90 90))
        (set! (.. this -glass1Events) (:glass1 dialogueTree))

        (set! (.. this -glass2Rect) (js/Phaser.Rectangle. 100 367 90 90))
        (set! (.. this -glass2Events) (:glass2 dialogueTree))

        (set! (.. this -glass3Rect) (js/Phaser.Rectangle. 1010 775 90 90))
        (set! (.. this -glass3Events) (:glass3 dialogueTree))

        (set! (.. this -glass4Rect) (js/Phaser.Rectangle. 1010 365 90 90))
        (set! (.. this -glass4Events) (:glass4 dialogueTree))

        (set! (.. this -throneRect) (js/Phaser.Rectangle. 555 100 85 85))
        (set! (.. this -throneEvents) (:throne dialogueTree))

        (set! (.. this -doorRect) (js/Phaser.Rectangle. 565 1030 70 70))
        (set! (.. this -doorEvents) (:door dialogueTree))

        (set! (.. this -weapon) false)
        (set! (.. this -weaponSpotted) false)
        (set! (.. this -atThrone) false)
        (set! (.. this -currentGlass) nil)

        )

      (update [this]

        ;; If possible, offer weapon
        (when (and (.. this -atThrone) (.. this -weaponSpotted) (not (.. this -weapon)))
          (do
            ;;TODO text to indicate to pick up
            (when (.. this -action -isDown)
              (set! (.. this -weapon) true)
              (.. this -weaponSprite destroy)
              (.. this -player (loadTexture "playerHammer" 0))
              (.. this (triggerDialogue :hammer)))
            ))

        ;; If possible, offer to break glass
        (when (and (.. this -weapon)
                   (.. this -currentGlass))
          (do
            ;;TODO text to offer to break the glass
            (when (.. this -action -isDown)
              (set! (.. game -broken) (.. this -currentGlass))
              (.. game -state (start "ending")))
            ))

        ;; hide all glasses until we know we're in a dialogue
        (do
          (set! (.. this -g1 -visible) false)
          (set! (.. this -g2 -visible) false)
          (set! (.. this -g3 -visible) false)
          (set! (.. this -g4 -visible) false))

        ;; Dialogue situation - no movement
        (if-let [d (seq (.-dialogue this))]
          (do
            (when-let [g (.. this -currentGlass)]
              (case g
                :glass1  (set! (.. this -g1 -visible) true)
                :glass2 (set! (.. this -g2 -visible) true)
                :glass3 (set! (.. this -g3 -visible) true)
                :glass4 (set! (.. this -g4 -visible) true)))
            (.. this -dialogueText (setText (first d)))
            (when (and (.. this -spacebar -isDown)
                       (> (- (.. game -time -now)
                             (.. this -lastSpace))
                          500))
              (do
                (set! (.. this -lastSpace) (.. game -time -now))
                (.. this -dialogueText (setText ""))
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

        (set! (.. this -atThrone) false)
        (set! (.. this -currentGlass) false)

        #_(.collideWorld this)

        (when-let [ce (.collideEvents this)]
          (do

            (cond
              (= :throne ce) (set! (.. this -atThrone) true)
              (not= :door ce) (set! (.. this -currentGlass) ce))

            (when (and (.. this -spacebar -isDown)
                       (> (- (.. game -time -now)
                             (.. this -lastSpace))
                          500))
              (do
                (set! (.. this -lastSpace) (.. game -time -now))
                (when (and (.. this -atThrone)
                           (not (.. this -weaponSpotted)))
                  (set! (.. this -weaponSpotted) true))
                (.triggerDialogue this ce))))))

      (collideWorld [this]
        (let [p (.-player this)]


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
          (js/Phaser.Rectangle.containsPoint (.. this -throneRect) (.-player this)) :throne
          (js/Phaser.Rectangle.containsPoint (.. this -doorRect) (.-player this)) :door
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
            (set! (.. this -dialogue) ["The Crier"  "..."]))

          :glass4
          (if (seq (.. this -glass4Events))
            (let [evs (.. this -glass4Events)]
              (do
                (set! (.. this -dialogue) (first evs))
                (set! (.. this -glass4Events) (rest evs))))
            (set! (.. this -dialogue) ["The Ascension" "..."]))

          :throne
          (if (seq (.. this -throneEvents))
            (let [evs (.. this -throneEvents)]
              (do
                (set! (.. this -dialogue) (first evs))
                (set! (.. this -throneEvents) (rest evs))))
            (set! (.. this -dialogue) ["This throne is long cold." "..."]))

          :door
          (if (seq (.. this -doorEvents))
            (let [evs (.. this -doorEvents)]
              (do
                (set! (.. this -dialogue) (first evs))
                (set! (.. this -doorEvents) (rest evs))))
            (set! (.. this -dialogue) ["I must find another way out..."]))

          :hammer
          (set! (.. this -dialogue) ["You pick up the Royal Hammer."])))

      (render [this]
        (.. game -debug (geom (.. this -glass1Rect) "#0fffff"))
        (.. game -debug (geom (.. this -glass2Rect) "#0fffff"))
        (.. game -debug (geom (.. this -glass3Rect) "#0fffff"))
        (.. game -debug (geom (.. this -glass4Rect) "#0fffff"))
        (.. game -debug (geom (.. this -throneRect) "#0fffff"))
        (.. game -debug (geom (.. this -doorRect) "#0fffff"))
        (.. game -debug (spriteInfo (.. this -player) 32 32))))))
