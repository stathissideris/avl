(ns org.stathissideris.avl-test
  (:require [midje.sweet :refer :all]
            [org.stathissideris.avl :refer :all]))

(defn eager-calc-height [tree]
  (m/match
   tree
   [nil _ right _] (inc (eager-calc-height right))
   [left _ nil _] (inc (eager-calc-height left))
   [left _ right _] (inc (max (eager-calc-height left) (eager-calc-height right)))
   :else 0))

(defn eager-balance-factor [[left _ right]]
  (- (eager-calc-height left)
     (eager-calc-height right)))

(facts "about mknode"
  (mknode 10) => [nil 10 nil 1]
  (mknode 1 2 3) => [1 2 3 1]
  (mknode 1 2 3 10) => [1 2 3 10])

(facts "about mktree"
  (mktree 10) => [nil 10 nil 1]
  (mktree 10 20 30) => (mknode (mknode 10) 20 (mknode 30))
  (mktree [1 2 3] 4 [5 6 7]) => (mknode (mknode (mknode 1) 2 (mknode 3)) 4 (mknode (mknode 5) 6 (mknode 7)))

  (mktree [[9 12 14] 17 [19 23 nil]] 50 [[nil 54 67] 72 76])
  =>
  (mknode
   (mknode
    (mknode (mknode 9) 12 (mknode 14))
    17
    (mknode (mknode 19) 23 (mknode nil)))
   50
   (mknode (mknode (mknode nil) 54 (mknode 67)) 72 (mknode 76)))

  (as-vector (mktree [[9 12 14] 17 [19 23 nil]] 50 [[nil 54 67] 72 76]))
  =>
  [[[9 12 14] 17 [19 23 nil]] 50 [[nil 54 67] 72 76]])

(facts "about calc-height"
  (calc-height (mktree 10)) => 1
  (calc-height (mktree 1 2 3)) => 2
  (calc-height (mktree [0 1 1.5] 2 3)) => 3
  (calc-height (mktree nil 1 [nil 2 3])) => 3)

(facts "about eager-calc-height"
  (eager-calc-height (mktree 10)) => (calc-height (mktree 10)) 
  (eager-calc-height (mktree 1 2 3)) => (calc-height (mktree 1 2 3)) 
  (eager-calc-height (mktree [0 1 1.5] 2 3)) => (calc-height (mktree [0 1 1.5] 2 3)) 
  (eager-calc-height (mktree nil 1 [nil 2 3])) => (calc-height (mktree nil 1 [nil 2 3])) )

(facts "about balance-factor"
  (balance-factor (mknode 10)) => 0
  (balance-factor (mknode (mknode 0) 1 nil)) => 1
  (balance-factor (mknode nil 1 (mknode 0))) => -1
  (balance-factor (mknode (mknode (mknode -1) 0 nil) 1 nil)) => 2
  (balance-factor (mknode (mknode nil 0 (mknode -1)) 1 nil)) => 2
  (balance-factor (mknode nil 1 (mknode (mknode -1) 0 nil))) => -2
  (balance-factor (mknode nil 1 (mknode nil 0 (mknode -1)))) => -2)

(facts "about rotate-left"
  (as-vector (rotate-left (mktree 1 2 [3 4 5]))) => [[1 2 3] 4 5]

  (rotate-left (mktree 1 2 [3 4 5]))
  => [[[nil 1 nil 1] 2 [nil 3 nil 1] 2] 4 [nil 5 nil 1] 3])

(facts "about rotate-right"
  (as-vector (rotate-right (mktree [1 2 3] 4 5))) => [1 2 [3 4 5]]

  (rotate-right (mktree [1 2 3] 4 5))
  => [[nil 1 nil 1] 2 [[nil 3 nil 1] 4 [nil 5 nil 1] 2] 3])




