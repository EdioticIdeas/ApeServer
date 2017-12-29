/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SERVER.util;

/**
 *
 * @author Shub
 */
interface MyQueue<T> {
    public T remove();
    public boolean isEmpty();
    public void add(T t);
    public int size();
}
