(ns game.ending)

(defn Ending [game]
  (reify Object

    (create [this]
      (let [t ""
            text-style (clj->js {:font "18px Arial"
                                 :fill "white"
                                 :wordWrap true
                                 :wordWrapWidth 300})]

        (set! (.. this -dialogueBox)
              (.. game -add (sprite 0 0 "dialogueBox")))
        (set! (.. this -endText)
              (.. game -add (text (/ (.. game -camera -width) 2)
                                  50
                                  t
                                  text-style)))
        (.. this -endText -anchor (setTo 0.5 0.5))

        ;; Text prep
        (case (.-broken game)
          :glass1
          (set! (.. this -beforeTextQueue)
                ["You raise the hammer above your head and have one last carefull look at the glass."
                 "Something ancient seems to call you in this green."
                 "For an instant, you are drawn."])
          :glass2
          (set! (.. this -beforeTextQueue)
                ["You raise the hammer above your head and have one last carefull look at the glass."
                 "Something in the way light plays in the purple glass makes you nauseous, or dizzy."
                 "For an instant, you are disgusted."])
          :glass3
          (set! (.. this -beforeTextQueue)
                ["You raise the hammer above your head and have one last carefull look at the glass."
                 "There is something terribly melancholy in the blue of that tear."
                 "For an instant, a sadness falls on you."])
          :glass4
          (set! (.. this -beforeTextQueue)
                ["You raise the hammer above your head and have one last carefull look at the glass."
                 "There is something so mysterious here. As though this glass..."
                 "...wasn't meant for you to look at."
                 "For an instant, you dream."]))

        (set! (.. this -afterTextQueue)
              ["You close your eyes..."
               "And strike."
               "..."
               "It's all so old. One blow was enough to shatter it utterly."
               "The sound of the hammer dropped on the floor echoes in the large throne room."
               "Carefully you climb out."
               "Shards of all colours litter the ground outside. You pick one up and put it into your pocket."
               "You leave the place, without turning back."
               "THE END"])

        ;; Glass Sprite
        (case (.. game -broken)
          :glass1
          (set! (.. this -glass) (.. game -add (sprite 0 100 "glass1")))
          :glass2
          (set! (.. this -glass) (.. game -add (sprite 0 100 "glass2")))
          :glass3
          (set!  (.. this -glass) (.. game -add (sprite 0 100 "glass3")))
          :glass4
          (set! (.. this -glass) (.. game -add (sprite 0 100 "glass4"))))


        (set! (.. this -lastSpace) (.. game -time -now))

        ;; Spacebar
        (set! (.. this -spacebar) (.. game -input -keyboard (addKey js/Phaser.Keyboard.SPACEBAR)))

        ))

    (update [this]

      (let [b (.. this -beforeTextQueue)
            a (.. this -afterTextQueue)]
        (cond
          (seq b)
          (do
            (.. this -endText (setText (first b)))
            (when (and (.. this -spacebar -isDown)
                       (> (- (.. game -time -now)
                             (.. this -lastSpace))
                          500))
              (do
                (set! (.. this -lastSpace) (.. game -time -now))
                (.. this -endText (setText ""))
                (set! (.-beforeTextQueue this) (rest b)))))

          (seq a)
          (do
            (set! (.. this -endText -y) (/ (.. game -camera -height) 2))
            (set! (.. this -glass -visible) false)
            (.. this -endText (setText (first a)))
            (when (and (.. this -spacebar -isDown)
                       (> (- (.. game -time -now)
                             (.. this -lastSpace))
                          500))
              (do
                (set! (.. this -lastSpace) (.. game -time -now))
                (.. this -endText (setText ""))
                (set! (.-afterTextQueue this) (rest a)))))

          :else
          (.. game -state (start "titleScreen")))))))
