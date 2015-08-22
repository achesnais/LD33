(ns game.title-screen)

(defn TitleScreen [game]
  (reify Object

    (create [this]
      (let [title "Too Late, Too Bitter"
            title-style (clj->js {:font "30px Arial"
                                  :fill "white"})
            title-x (.. game -world -centerX)
            title-y (- (.. game -world -centerY) 75)
            instructions "Press space to begin."
            instructions-style (clj->js {:font "18px Arial"
                                         :fill "white"})
            instr-x (.. game -world -centerX)
            instr-y (.. game -world -centerY)
            title-text (.. game -add
                           (text title-x
                                 title-y
                                 title
                                 title-style))
            instr-text (.. game -add
                           (text instr-x
                                 instr-y
                                 instructions
                                 instructions-style))]

        (.. instr-text -anchor (set 0.5))
        (.. title-text -anchor (set 0.5))

        (.. game -input -keyboard
            (addCallbacks nil nil #(.. game -state (start "mainGame"))))))))
