(ns game.main-game)

(defn MainGame [game]
  (let [still-frame 0
        dialogue-style (clj->js {:font "20px Arial"
                                 :fill "white"})]
    (reify Object

      (create [this]
        (js/console.log "Entering Main Game!")
        (.. game -add (sprite 0 0 "floor"))
        (.. game -add (sprite 0 0 "dialogue-box"))
        (set! (.. this -dialogue) (.. game -add (text 0 0
                                                      "It begins here"
                                                      dialogue-style)))
        (set! (.. this -player) (.. game -add (sprite 10 10 "player"))))

      (update [this]
        (if true
          (set! (.. game -player -frame) still-frame))))))
