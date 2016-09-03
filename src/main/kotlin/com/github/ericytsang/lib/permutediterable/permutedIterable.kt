package com.github.ericytsang.lib.permutediterable

// todo: add test cases!!!
/**
 * returns an [Iterator] that returns the next permutation of elements of type
 * [T] for each call to [Iterator.next].
 */
fun <T> Iterable<Iterable<T>>.permutedIterable():Iterable<List<T>>
{
    return object:Iterable<List<T>>
    {
        override fun iterator():Iterator<List<T>>
        {
            return object:AbstractIterator<List<T>>()
            {
                private lateinit var iterables:MutableList<Iterable<T>>
                private lateinit var iterators:MutableList<Iterator<T>>
                private lateinit var permutedElements:MutableList<T>
                var isFirstIteration = true
                override fun computeNext()
                {
                    with(this@permutedIterable)
                    {
                        if (Thread.interrupted()) throw InterruptedException("interrupted!")
                        while (true)
                        {
                            // compute the next situations to combine
                            if (isFirstIteration)
                            {
                                isFirstIteration = false

                                if (any {!it.iterator().hasNext()})
                                {
                                    done()
                                    return
                                }
                                else
                                {
                                    iterables = map {it}.toMutableList()
                                    iterators = map {it.iterator()}.toMutableList()
                                    permutedElements = iterators.map {it.next()}.toMutableList()
                                    setNext(permutedElements.toList())
                                    return
                                }
                            }
                            else
                            {
                                for (i in iterators.indices)
                                {
                                    // if there is a next, get it for combining later
                                    if (iterators[i].hasNext())
                                    {
                                        permutedElements[i] = iterators[i].next()
                                        setNext(permutedElements.toList())
                                        return
                                    }

                                    // else reset the iterator for i
                                    else
                                    {
                                        iterators[i] = iterables[i].iterator()
                                        permutedElements[i] = iterators[i].next()
                                    }
                                }

                                // exit if there are no more situations to combine
                                done()
                                return
                            }
                        }
                    }
                }
            }
        }
    }
}
