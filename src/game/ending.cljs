(ns game.ending)

(defn Ending [game glass]
  (reify Object
    (create [this]
      (let [t (str "THE END: you broke " (.. game -broken))
            text-style (clj->js {:font "20px Arial"
                                 :fill "white"})]
        (set! (.. this -text) (.. game -add (text 0 0 t text-style)))))))
