/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SERVER.util;

import java.util.LinkedList;

/**
 *
 * @author Shub
 */
public class ClientReqResueue<t> implements MyQueue{

    

            LinkedList<t> list = new LinkedList<>();

    @Override
    public void add(Object t) {
        list.addLast((t) t);
    }
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public t remove() {
       return list.removeFirst();
    }
    
    @Override
    public int size() {
        return list.size();
    }
}
