# avl

A simple implementation of AVL binary search trees. This is not super
optimised -- the main use case is to allow different comparators to be
used for inserting and searching. Deletion not
implemented. Documentation pending.

## Usage

How to create a tree:

    > (use 'org.stathissideris.avl)
    > (def tree (insert-all (mktree 50) [17 12 23 14 9 19 72 54 67 76]))

Look up a value:

    > (search tree 12)
    true
    > (search tree 100)
    nil
    
### Custom comparators

Let's see how to build a tree that contains ranges and check if
certain numbers belong to those ranges.

First, define a function for insertion:

    (defn compare-ranges [[a1 b1] [a2 b2]]
      (cond (< b1 a2) -1
            (> a1 b2) 1
            :else 0))

Construct a tree using the range comparator:

    (def tree (insert-all (mktree [1 10]) [[40 50] [100 150] [200 400]] compare-ranges))

Define a constructor for looking up numbers:

    (defn compare-number-to-range [x [a b]]
      (cond (and (<= a x b)) 0
            (< x a) -1
            (< b x) 1))

...and use it to check whether numbers belong in any of the ranges:

    > (search tree 3 compare-number-to-range)
    true
    > (search tree -1 compare-number-to-range)
    nil
    > (search tree 210 compare-number-to-range)
    true
    > (search tree 170 compare-number-to-range)
    nil

## License

Copyright © 2014 Efstathios Sideris

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
