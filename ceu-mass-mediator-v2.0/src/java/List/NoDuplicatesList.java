/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package List;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author María Postigo. San Pablo-CEU
 *
 */
public class NoDuplicatesList<E> extends LinkedList<E> {

    @Override
    public boolean add(E e) {
        if (this.contains(e)) {
            return false;
        } else {
            return super.add(e);
        }
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        Collection<E> copy = new LinkedList<E>(collection);
        copy.removeAll(this);
        return super.addAll(copy);
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> collection) {
        Collection<E> copy = new LinkedList<E>(collection);
        copy.removeAll(this);
        return super.addAll(index, copy);
    }

    @Override
    public void add(int index, E element) {
        if (this.contains(element)) {
            return;
        } else {
            super.add(index, element);
        }
    }
}
