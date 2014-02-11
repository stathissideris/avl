(ns org.stathissideris.avl)

;;http://en.wikipedia.org/wiki/AVL_tree

(require '[clojure.core.match :as m])

(defn calc-height [tree]
  (m/match
   tree
   [nil _ right nil] (inc (calc-height right))
   [left _ nil nil] (inc (calc-height left))
   [left _ right nil] (inc (max (calc-height left) (calc-height right)))
   [left _ right height] height
   :else 0))

(defn mknode
  ([node] [nil node nil 1])
  ([left node right]
     (let [n [left node right nil]]
       (mknode left node right (calc-height n))))
  ([left node right height]
     [left node right height]))

(defn mktree
  ([node] (mknode node))
  ([left node right]
     (mknode (if (sequential? left) (apply mktree left) (mktree left))
             node
             (if (sequential? right) (apply mktree right) (mktree right)))))

(defn left [n] (first n))
(defn node [n] (second n))
(defn right [n] (nth n 2))
(defn height [n] (nth n 3))

(defn balance-factor [[left _ right]]
  (- (calc-height left)
     (calc-height right)))

(defn rotate-left
  [[a parent [b child c]]]
  (mknode (mknode a parent b) child c))

(defn rotate-right
  [[[a child b] parent c]]
  (mknode a child (mknode b parent c)))

(defn rebalance [tree]
 (let [[left node right height] tree
       factor (balance-factor tree)]
   (condp = factor
     0 tree, -1 tree, 1 tree
     2 (let [tree
             (if-not (= -1 (balance-factor left))
               tree
               (mknode (rotate-left left) node right))]
         (rotate-right tree))
     -2 (let [tree
              (if-not (= 1 (balance-factor right))
                tree
                (mknode left node (rotate-right right)))]
          (rotate-left tree)))))

(defn insert [[left node right height] value & [comparator]]
  (if (= nil left node right)
    (mknode value)
    (let [comparator (or comparator compare)
          comparison (comparator value node)]
      (rebalance
       (cond
        (zero? comparison)
        (mknode left value right height)

        (neg? comparison)
        (if left
          (mknode (insert left value comparator) node right)
          (mknode (mknode value) node right))
        
        (pos? comparison)
        (if right
          (mknode left node (insert right value comparator))
          (mknode left node (mknode value))))))))

(defn insert-all [tree values & [comparator]]
  (reduce (fn [t v] (insert t v comparator)) tree values))

(defn search [[left node right] value & [comparator]]
  (let [comparator (or comparator compare)
        comparison (comparator value node)]
    (condp = comparison
      0 true
      -1 (when left (recur left value [comparator]))
      1 (when right (recur right value [comparator])))))

(defn as-vector [tree]
  (m/match
   tree
   [nil node nil _] node
   [left node right _] [(as-vector left) node (as-vector right)]
   :else nil))

(defn print-tree [tree]
  (clojure.pprint/pprint (as-vector tree)))

(defn compare-ranges [[a1 b1] [a2 b2]]
  (cond (< b1 a2) -1
        (> a1 b2) 1
        :else 0))

(defn compare-number-to-range [x [a b]]
  (cond (and (<= a x b)) 0
        (< x a) -1
        (< b x) 1))

(comment
  (= tree (insert-all (mknode 50) [17 12 23 14 9 19 72 54 67 76]))
  (time (def tree2 (insert-all (mktree 0) (take 10000 (repeatedly (fn [] (int (rand 10000)))))))))
