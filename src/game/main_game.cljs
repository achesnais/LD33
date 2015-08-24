(ns game.main-game
  (:require [game.story :refer [dialogueTree]]))

(defn MainGame [game]
  (let [still-frame 0
        anim-frame-rate 4
        m-per-sec 500
        speed 30
        dialogue-style (clj->js {:font "16px Arial"
                                 :fill "white"})]
    (reify Object

      (create [this]

        ;; Define world bounds
        (.. game -world (setBounds 0 -100 1200 1300))

        ;; Populate world
        (.. game -add (sprite 0 0 "world"))
        (set! (.. this -weaponSprite) (.. game -add (sprite 598 80 "weapon")))
        (.. this -weaponSprite -anchor (setTo 0.5 0.5))
        (set! (.. this -player) (.. game -add (sprite 600 1072 "player")))
        (.. game -add (sprite 1080 1080 "bot-col"))
        (.. game -add (sprite 60 1081 "bot-col"))

        ;; Stained Glass
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

        ;; Animations
        (.. this -player -animations
            (add "down" (clj->js [1 2 3 4]) anim-frame-rate true))
        (.. this -player -animations
            (add "up" (clj->js [5 6 7 8]) anim-frame-rate true))
        (.. this -player -animations
            (add "right" (clj->js [9 10 11 12]) anim-frame-rate true))
        (.. this -player -animations
            (add "left" (clj->js [13 14 15 16]) anim-frame-rate true))

        (set! (.. this -lastMov) (.. game -time -now))

        ;; Input
        (set! (.. this -cursors)
              (.. game -input -keyboard (createCursorKeys)))
        (set! (.. this -interact)
              ;;Switching to E as space is not ideal for browsers
              #_(.. game -input -keyboard (addKey js/Phaser.Keyboard.SPACEBAR))
              (.. game -input -keyboard (addKey js/Phaser.Keyboard.E)))
        (set! (.. this -action)
              (.. game -input -keyboard (addKey js/Phaser.Keyboard.ENTER)))


        ;; Camera
        (.. this -player -anchor (setTo 0.5 0.5))
        (.. game -camera (follow (.-player this)))
        #_(set! (.. game -player -cameraOffset) 0 100)

        ;; Dialogue box
        (set! (.. this -dialogueBox)
              (.. game -add (sprite 0 0 "dialogueBox")))
        (set! (.. this -dialogueBox -fixedToCamera) true)

        (set! (.. this -dialogueText)
              (.. game -add (text (/ (.. game -camera -width) 2)
                                  50
                                  ""
                                  dialogue-style)))
        (.. this -dialogueText -anchor (setTo 0.5 0.5))
        (set! (.. this -dialogueText -fixedToCamera) true)

        (set! (.. this -interactText)
              (.. game -add (text (* 0.05 (.. game -camera -width))
                                  10
                                  "(e : interact)"
                                  (clj->js {:font "12px Arial" :fill "#dddddd"}))))
        (set! (.. this -interactText -fixedToCamera) true)

        (set! (.. this -hammerText)
              (.. game -add (text (* 0.65 (.. game -camera -width))
                                  10
                                  "(enter: pick hammer)"
                                  (clj->js {:font "12px Arial" :fill "#dddddd"}))))
        (set! (.. this -hammerText -fixedToCamera) true)
        (set! (.. this -hammerText -visible) false)

        (set! (.. this -lastInteract) (.. game -time -now))

        ;; Events
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
        (set! (.. this -first) true)
        (set! (.. this -triedOpenDoor) false)

        ;; Fire intro text
        (.. this (triggerDialogue :intro)))

      (update [this]

        ;; Set to visible by the end of update if necessary
        (set! (.. this -hammerText -visible) false)

        ;; If possible, offer weapon
        (when (and (.. this -atThrone)
                   (.. this -weaponSpotted)
                   (.. this -triedOpenDoor)
                   (not (.. this -weapon))
                   (not (seq (.-dialogue this))))
          (do
            (set! (.. this -hammerText -visible) true)
            (when (.. this -action -isDown)
              (set! (.. this -weapon) true)
              (.. this -weaponSprite destroy)
              #_(.. this -player (loadTexture "playerHammer" 0))
              (.. this (triggerDialogue :hammer)))
            ))

        ;; If possible, offer to break glass
        (when (and (.. this -weapon)
                   (.. this -triedOpenDoor)
                   (.. this -currentGlass)
                   (seq (.. this -dialogue)))
          (do
            (set! (.. this -hammerText -visible) true)
            (set! (.. this -hammerText -text) "(enter: break glass)")
            (when (.. this -action -isDown)
              (set! (.. game -broken) (.. this -currentGlass))
              (.. game -state (start "ending")))))

        ;; hide all glasses until we know we're in a dialogue
        (do
          (set! (.. this -g1 -visible) false)
          (set! (.. this -g2 -visible) false)
          (set! (.. this -g3 -visible) false)
          (set! (.. this -g4 -visible) false))

        ;; Dialogue situation - no movement
        (if-let [d (seq (.-dialogue this))]
          (do
            (set! (.. this -player -frame) 0)
            (when-let [g (.. this -currentGlass)]
              (case g
                :glass1  (set! (.. this -g1 -visible) true)
                :glass2 (set! (.. this -g2 -visible) true)
                :glass3 (set! (.. this -g3 -visible) true)
                :glass4 (set! (.. this -g4 -visible) true)))
            (.. this -dialogueText (setText (first d)))
            (when (and (.. this -interact -isDown)
                       (> (- (.. game -time -now)
                             (.. this -lastInteract))
                          m-per-sec))
              (do
                (set! (.. this -lastInteract) (.. game -time -now))
                (.. this -dialogueText (setText ""))
                (set! (.-dialogue this) (rest d)))))

          ;; Basic Movement
          (when (> (- (.. game -time -now)
                      (.. this -lastMov))
                   200)
            (do
              (cond
                (.. this -cursors -up -isDown)
                (do
                  (let [y  (.. this -player -y)]
                    (set! (.. this -player -y)
                          (- y speed)))
                  (.. this -player -animations (play "up"))
                  (set! (.. this -lastMov) (.. game -time -now)))

                (.. this -cursors -down -isDown)
                (do
                  (let [y  (.. this -player -y)]
                    (set! (.. this -player -y)
                          (+ y speed)))
                  (.. this -player -animations (play "down"))
                  (set! (.. this -lastMov) (.. game -time -now)))

                (.. this -cursors -left -isDown)
                (do
                  (let [x  (.. this -player -x)]
                    (set! (.. this -player -x)
                          (- x speed)))
                  (.. this -player -animations (play "left"))
                  (set! (.. this -lastMov) (.. game -time -now)))

                (.. this -cursors -right -isDown)
                (do
                  (let [x  (.. this -player -x)]
                    (set! (.. this -player -x)
                          (+ x speed)))
                  (.. this -player -animations (play "right"))
                  (set! (.. this -lastMov) (.. game -time -now)))

                :else (set! (.. this -player -frame) still-frame)))))

        (set! (.. this -atThrone) false)
        (set! (.. this -currentGlass) false)

        (.collideWorld this)

        (when-let [ce (.collideEvents this)]
          (do

            (cond
              (= :throne ce) (set! (.. this -atThrone) true)
              (not= :door ce) (set! (.. this -currentGlass) ce))

            (when (and (.. this -interact -isDown)
                       (> (- (.. game -time -now)
                             (.. this -lastInteract))
                          500))
              (do
                (set! (.. this -lastInteract) (.. game -time -now))
                (when (and (.. this -atThrone)
                           (not (.. this -weaponSpotted)))
                  (set! (.. this -weaponSpotted) true))
                (.triggerDialogue this ce))))))

      (collideWorld [this]
        (let [p (.-player this)]
          (when (< (.-y p) 100)
            (set! (.-y p) 100))
          (when (> (.-y p) 1072)
            (set! (.-y p) 1072))
          (when (< (.-x p) 120)
            (set! (.-x p) 120))
          (when (> (.-x p) 1075)
            (set! (.-x p) 1075))))

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
        (if (and (.. this -first)
                 (#{:glass1 :glass2 :glass3 :glass4} k))
          (do
            (set! (.. this -first) false)
            (set! (.. this -dialogue) ["...this place..."
                                       "...is very old indeed..."
                                       "...the secrets to making..."
                                       "...stained glass..."
                                       "...are long lost..."
                                       "...and the makers of this..."
                                       "...remembered..."
                                       "...only by people like me..."]))
          (case k

            :glass1
            (if (seq (.. this -glass1Events))
              (let [evs (.. this -glass1Events)]
                (do
                  (set! (.. this -dialogue) (first evs))
                  (set! (.. this -glass1Events) (rest evs))))
              (set! (.. this -dialogue) ["...The Choosing..."
                                         "..."]))

            :glass2
            (if (seq (.. this -glass2Events))
              (let [evs (.. this -glass2Events)]
                (do
                  (set! (.. this -dialogue) (first evs))
                  (set! (.. this -glass2Events) (rest evs))))
              (set! (.. this -dialogue) ["...The Heracleus..."
                                         "..."]))

            :glass3
            (if (seq (.. this -glass3Events))
              (let [evs (.. this -glass3Events)]
                (do
                  (set! (.. this -dialogue) (first evs))
                  (set! (.. this -glass3Events) (rest evs))))
              (set! (.. this -dialogue) ["...The Tear..."
                                         "..."]))

            :glass4
            (if (seq (.. this -glass4Events))
              (let [evs (.. this -glass4Events)]
                (do
                  (set! (.. this -dialogue) (first evs))
                  (set! (.. this -glass4Events) (rest evs))))
              (set! (.. this -dialogue) ["...this one evades naming..."
                                         "..."]))

            :throne
            (if (seq (.. this -throneEvents))
              (let [evs (.. this -throneEvents)]
                (do
                  (set! (.. this -dialogue) (first evs))
                  (set! (.. this -throneEvents) (rest evs))))
              (set! (.. this -dialogue) ["...this throne is long cold...."
                                         "...its fine white stones..."
                                         "...useless..."
                                         "..."]))

            :door
            (cond

              (and (.. this -weapon)
                   (.. this -triedOpenDoor)
                   (not (.. this -triedBreakDoor)))
              (do
                (set! (.. this -triedBreakDoor) true)
                (set! (.. this -dialogue) ["*WAM*"
                                           "..."
                                           "...barely a scratch..."
                                           "...need another way out."]))

              (not (.. this -triedOpenDoor))
              (do
                (set! (.. this -triedOpenDoor) true)
                (set! (.. this -dialogue)
                      ["*You try to open the gate*"
                       "...won't budge..."
                       "... need another way out..."
                       "...or something to break it open?"]))

              (.. this -triedBreakDoor)
              (set! (.. this -dialogue)
                    ["...it's too sturdy..."
                     "...I can't break it..."
                     "...need to find another way out."])

              :else
              (set! (.. this -dialogue)
                    ["...must find a way..."
                     "...to open it..."
                     "...or another way out?"]))

            :hammer
            (set! (.. this -dialogue) ["You pick up the Royal Hammer."
                                       "... I don't know..."
                                       "...if it'll work..."
                                       "...we'll see..."])

            :intro
            (set! (.. this -dialogue) ["*BANG*"
                                       "...damnit..."
                                       "...hope it's not locked..."
                                       "HELLO?"
                                       "...what is this place?"
                                       "...an old temple?"]))))

      (render [this]
        ()))))
