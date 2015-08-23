(ns game.title-screen)

(defn TitleScreen [game]
  (reify Object

    (create [this]
      (let [title "Too Late, Too Bitter"
            title-style (clj->js {:font "30px Arial"
                                  :fill "white"})
            title-x (* 0.5 (.. game -camera -width))
            title-y (* 0.33 (.. game -camera -height))
            instructions "Press space to begin."
            instructions-style (clj->js {:font "18px Arial"
                                         :fill "grey"})
            instr-x (* 0.5 (.. game -camera -width))
            instr-y (* 0.66  (.. game -camera -height))]

        (set! (.. this -titleText)
              (.. game -add
                  (text title-x
                        title-y
                        title
                        title-style)))
        (set! (.. this -instrText)
              (.. game -add
                  (text instr-x
                        instr-y
                        instructions
                        instructions-style)))

        (.. this -instrText -anchor (set 0.5))
        (.. this -titleText -anchor (set 0.5))))

    (update [this]
      (if (.. game -input -keyboard (isDown js/Phaser.Keyboard.SPACEBAR))
        (.. game -state (start "mainGame" true))))

    (render [this]
      ())))
